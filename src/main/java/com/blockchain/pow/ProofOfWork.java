package com.blockchain.pow;

import com.blockchain.bean.block.Block;
import com.blockchain.bean.block.BlockBody;
import com.blockchain.bean.block.BlockHeader;
import com.blockchain.bean.merkle.MerkleTree;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.common.App;
import com.blockchain.common.ResultCode;
import com.blockchain.common.TransactionStatus;
import com.blockchain.core.net.Connecter;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.crypto.Sha256;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.BlockManager;
import com.blockchain.utils.JsonUtil;
import com.blockchain.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午2:56.
 * Description: 矿工类
 */

@EnableAsync
public class ProofOfWork {


    @Autowired
    private BlockManager blockManager;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private AccountPool accountPool;

    /**
     * 强制停止
     */
    public
    /**
     * 随机数
     */
    long nonce = 0;

    /**
     * 回调接口
     */
    private WorkerCallBack workingListener;


    /**
     * 判断当前矿工是否正在工作
     */
    public boolean isWorking = false;

    /**
     * 创建新的矿工，设定难度目标值，需要从远端拉取
     * 对1进行移位运算，左移n位就是乘以2的n次方，得到我们的难度目标值
     * @return
     */
    public BigInteger getTarget(int targetBits) {
        return BigInteger.ONE.shiftLeft((256 - targetBits));
    }

    /**
     * 运行工作量证明，开始挖矿，找到小于难度目标值的Hash
     * 挖矿过程中需要实时更新：交易数据，时间戳，难度因子
     * 执行方式，单线程，异步
     * @return
     */
    @Async
    public synchronized void mine() throws ApiException {
        try {
            nonce = 1;
            App.ALLOW_MINE = true;
            checkIsReady();
            Block block = new Block();
            //记录开始时间
            long startTime = System.currentTimeMillis();
            setWorking(true);
            workingListener.onStart(startTime);
            //判断创世区块
            if (blockManager.getLastBlock() == null){
                block = genesisBlock();
                workingListener.onComplete(block);
                return;
            }
            //循环挖矿
            while (nonce < Long.MAX_VALUE && App.ALLOW_MINE) {
                //准备数据
                block.setBlockBody(packTransactions());
                block.setBlockHeader(prepareBlockHeader(block.getBlockBody()));
                //取出待计算的区块数据
                String data = getPreHashData(block);
                //开始计算hash，通过sha256计算16进制hash值，64位
                String currentHash = Sha256.sha256(data);
                //把计算出来的hash值转换成10进制，如果小于难度目标值，也就是满足前导零个数，即挖矿成功，记录下nonce值
                if (new BigInteger(currentHash, 16).compareTo(getTarget(block.getBlockHeader().targetBits)) == -1) {
                    block.setHash(currentHash);
                    //修改交易状态
                    confirmTransactionStatus(block);
                    workingListener.onComplete(block);
                    break;
                } else {
                    nonce++;
                    workingListener.onWorking(block.getBlockHeader().getTimeStamp()-startTime,nonce,currentHash);
                }
            }
        }  catch (TrustSDKException e) {
            e.printStackTrace();
        }  finally {
            setWorking(false);
            App.ALLOW_MINE = true;
        }
    }


    /**
     * 检查状态
     * @throws ApiException
     */
    public void checkIsReady() throws ApiException {
        if (StringUtil.isEmpty(App.PUBLIC_KEY) && StringUtil.isEmpty(blockManager.getPublicKey())){
            throw new ApiException(ResultCode.FAIL,"请设置挖矿公钥");
        }
        if (isWorking()){
            throw new ApiException(ResultCode.FAIL,"矿工正在辛苦劳作，放过他吧");
        }
        if (Connecter.getCurrentOnlineNodes().size() > 0 && !App.isSyncComplete){
            throw new ApiException(ResultCode.FAIL,"本地区块还未同步完成，请稍等");
        }
    }


    /**
     * 取出计算hash之前的block数据
     * blockBody
     * @return
     */
    public String getPreHashData(Block block){
        return block.getBlockHeader().toString() + block.getBlockBody().toString();
    }

    /**
     * 从交易池取出准备打包的交易数据，即区块体
     * @return
     */
    private BlockBody packTransactions() throws ApiException {
        List<Transaction> transactions = transactionPool.getTransactions();
        List<String> accounts = accountPool.getAccounts();//允许accounts为空
//        if (transactions.size() == 0){   //  todo 这里根据需求选择是否注释掉，即便正在挖矿也会抛异常停下来，直到有新的交易产生
//            throw new ApiException(ResultCode.FAIL,"当前交易池没有数据，请等待新交易被打包");
//        }
        BlockBody blockBody = new BlockBody(transactions,accounts);
        return blockBody;
    }

    /**
     * 根据区块体，生成区块头，准备计算hash
     * @param blockBody
     * @return
     * @throws TrustSDKException
     */
    private BlockHeader prepareBlockHeader(BlockBody blockBody) throws ApiException {
        BlockHeader blockHeader = new BlockHeader();
        List<Transaction> transactions = blockBody.getTransactions();
        //获取每个交易信息的hash值
        List<String> hashList = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            hashList.add(transactions.get(i).getTxHash());
        }
        //计算所有交易信息的hashRoot，作为默克尔树的根节点
        blockHeader.setHashMerkleRoot(hashList.size() > 0 ? new MerkleTree(hashList).build().getRoot() : "");
        //把公钥放进区块头,即接收矿工费的账户   11pkwDUGPM5hURFGhvdxXrYeMbxJ4bxPY
        blockHeader.setPublicKey(StringUtil.isEmpty(App.PUBLIC_KEY) ? blockManager.getPublicKey() : App.PUBLIC_KEY);
        //设置区块头时间戳
        blockHeader.setTimeStamp(System.currentTimeMillis());
        //设置版本号，从配置文件读取
        blockHeader.setVersion(App.VERSION);
        //区块高度，从1开始
        blockHeader.setNumber((blockManager.getLastBlockNumber() + 1));
        //指向前一个区块的hash值，从levelDB读取最后一条记录
        blockHeader.setPreviousBlockHash(blockManager.getLastBlockHash());
        //设置难度因子
        blockHeader.setTargetBits(App.TARGET_BITS);
        //设置随机数
        blockHeader.setNonce(nonce);
        return blockHeader;
    }

    /**
     * 确认交易状态
     */
    public void confirmTransactionStatus(Block block){
        List<Transaction> transactions = block.getBlockBody().getTransactions();
        for (Transaction transaction : transactions) {
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
    }

    /**
     * 设置挖矿监听器
     */
    public void setWorkingListener(WorkerCallBack workingListener) {
        this.workingListener = workingListener;
    }

    /**
     * 生成创世区块，这里偷懒了直接用json转一下
     * @return
     */
    public Block genesisBlock(){
        return JsonUtil.toBean("{\n" +
                "    \"blockBody\":{\n" +
                "        \"transactions\":[\n" +
                "          ]\n" +
                "    },\n" +
                "    \"blockHeader\":{\n" +
                "        \"hashMerkleRoot\":\"\",\n" +
                "        \"nonce\":0,\n" +
                "        \"number\":1,\n" +
                "        \"previousBlockHash\":\"\",\n" +
                "        \"publicKey\":\"\",\n" +
                "        \"targetBits\":0,\n" +
                "        \"timeStamp\":0,\n" +
                "        \"version\":1\n" +
                "    },\n" +
                "    \"hash\":\"0000000000000000000000000000000000000000000000000000000000000000\"\n" +
                "}\n" +
                "\n",Block.class);
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
