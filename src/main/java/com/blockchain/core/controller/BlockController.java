package com.blockchain.core.controller;

import com.blockchain.base.BaseData;
import com.blockchain.bean.block.BlockBody;
import com.blockchain.bean.block.BlockHeader;
import com.blockchain.bean.merkle.MerkleTree;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.bean.transaction.TransactionRequest;
import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.common.ResultGenerator;
import com.blockchain.bean.block.Block;
import com.blockchain.common.Constants;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.PacketType;
import com.blockchain.core.net.Sender;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.BlockManager;
import com.blockchain.pow.ProofOfWork;
import com.blockchain.server.TransactionService;
import com.blockchain.crypto.Cryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by: Yumira.
 * Created on: 2018/7/25-下午4:38.
 * Description:
 */
@RestController
@RequestMapping("/block")
public class BlockController {


	@Autowired
	private BlockManager blockManager;
	@Autowired
    private ProofOfWork worker;


    Logger logger = LoggerFactory.getLogger(BlockController.class);


    /**
     * 测试连接用，向group发消息
     * @param content
     * @return
     * @throws Exception
     */
    @PostMapping("/send")
    public BaseData send(@RequestParam String content) throws Exception {
        MessagePacket packet = new MessagePacket();
        packet.setBody(content.getBytes(Constants.CHARSET));
        packet.setType(PacketType.SEND_CONTENT_REQUEST);
        Sender.sendGroup(packet);
        return ResultGenerator.genSuccessResult();
    }


    /**
     * 设置公钥
     * @return
     * @throws Exception
     */
    @PostMapping("/publickey")
    public BaseData setPublicKey(@RequestParam String publicKey) {
        App.PUBLIC_KEY = publicKey;
        blockManager.putPublicKey(publicKey);
        logger.info("设置公钥："+publicKey);
        return ResultGenerator.genSuccessResult();
    }


    /**
     * 强制停止挖矿
     * @return
     * @throws Exception
     */
    @PostMapping("/stop")
    public BaseData stopMine() {
        App.ALLOW_MINE = false;
        return ResultGenerator.genSuccessResult();
    }


//
//    @PostMapping("/test")
//    public BaseData test(@RequestParam String content) throws Exception {
//
//        TransactionRequest transactionRequest = new TransactionRequest();
//        transactionRequest.setFrom("11pkwDUGPM5hURFGhvdxXrYeMbxJ4bxPY");
//        transactionRequest.setTo("14Jau3qb1i5MeAxkWSSKbcpN6hTLR9U5fi");
//        transactionRequest.setPrivateKey("IyJfQhKm8XVcQPOsas6qiNvFxPVo9pULIeOjedoA6GQ=");
//        transactionRequest.setData(content);
//        transactionRequest.setAmount(new BigDecimal(0));
//        Transaction transaction = ApplicationContextProvider.getBean(TransactionService.class).buildTransaction(transactionRequest);
//        List<Transaction> transactionList = new ArrayList<>();
//        transactionList.add(transaction);
//
//        BlockBody blockBody = new BlockBody();
//        blockBody.setTransactions(transactionList);
//
//        //获取BlockBody中的交易信息列表
//        List<Transaction> transactions = blockBody.getTransactions();
//        //获取每个交易信息的hash值
//        List<String> hashList = new ArrayList<>();
//        for (int i = 0; i < transactions.size(); i++) {
//            hashList.add(transactions.get(i).getTxHash());
//        }
//        BlockHeader blockHeader = new BlockHeader();
//        //计算所有交易信息的hashRoot，作为默克尔树的根节点
//        blockHeader.setHashMerkleRoot(new MerkleTree(hashList).build().getRoot());
//        //把公钥放进区块头
//        blockHeader.setPublicKey(Cryptor.generatePublicKey("辜  达  蚂氨尝  拆龄祭 殴 缔 个 楚"));
//        //设置区块头时间戳
//        blockHeader.setTimeStamp(System.currentTimeMillis());
//        //设置版本号，从配置文件读取
//        blockHeader.setVersion(0);
//        //区块的序号，从1开始
//        blockHeader.setNumber((blockManager.getLastBlockNumber() + 1));
//        //指向前一个区块的hash值，从levelDB读取最后一条记录
//        blockHeader.setPreviousBlockHash(blockManager.getLastBlockHash());
//        //区块头+区块体=区块
//        Block block = Block.build(blockHeader,blockBody);
//        blockManager.addBlock(block);
//        return ResultGenerator.genSuccessResult(block);
//    }



    /**
     * 开始挖矿
     * @return
     */
    @PostMapping("/mine")
    public BaseData mine() throws ApiException{
        worker.mine();
        return ResultGenerator.genSuccessResult();
    }


    /**
     * 设置自动挖矿
     * @return
     */
    @PostMapping("/autowork")
    public BaseData setAutoMine(boolean isAutoWork){
        App.AUTO_WORK = isAutoWork;
        logger.info("设置自动挖矿："+isAutoWork);
        return ResultGenerator.genSuccessResult();
    }

    /**
	 * 查看最后一个区块
	 * @return
	 */
	@PostMapping("/lastblock")
	public BaseData lastBlock() throws ApiException{
		Block block = blockManager.getLastBlock();
		if (block == null) {
			return ResultGenerator.genSuccessResult();
		}
		return ResultGenerator.genSuccessResult(block);
	}


    /**
     * 查看最后一个区块hash
     * @return
     */
    @PostMapping("/lasthash")
    public BaseData lastHash() throws ApiException{
        return ResultGenerator.genSuccessResult(blockManager.getLastBlockHash());
    }


    /**
     * 查询区块高度
     * @return
     */
    @PostMapping("/height")
    public BaseData getChainHeight() throws ApiException{
        return ResultGenerator.genSuccessResult(blockManager.getLastBlockNumber());
    }



    /**
     * 根据hash查询区块
     * @return
     */
    @PostMapping("/getblock")
    public BaseData getBlock(@RequestParam("hash") String hash) throws ApiException{
        Block block = blockManager.getBlockByHash(hash);
        if (block == null){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genSuccessResult(block);
    }

    /**
     * 根据hash查询某区块的下一个区块
     * @return
     */
    @PostMapping("/getnextblock")
    public BaseData getNextBlock(@RequestParam("hash") String hash) throws ApiException{
        Block block = blockManager.getNextBlockByHash(hash);
        if (block == null){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genSuccessResult(block);
    }

    /**
     * 查询已确认的所有区块，仅供测试！！！！！
     * @return
     */
    @PostMapping("/getallblock")
    public BaseData getAllBlock() throws ApiException{
        return ResultGenerator.genSuccessResult(blockManager.getBlockChain());
    }


}
