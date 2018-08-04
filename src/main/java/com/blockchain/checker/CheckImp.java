package com.blockchain.checker;

import cn.hutool.core.util.StrUtil;
import com.blockchain.bean.block.Account;
import com.blockchain.bean.block.Block;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.crypto.Sha256;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.manmger.AccountManager;
import com.blockchain.manmger.BlockManager;
import com.blockchain.crypto.Cryptor;
import com.blockchain.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 各种校验的实现类
 * created by yumira 2O18/7/23.
 */
@Component
public class CheckImp implements BlockChecker {

    static final int OK = 0;
    static final int ERROR = -1;

    @Autowired
    private BlockManager dbBlockManager;
    @Autowired
    private AccountManager accountManager;



    @Override
    public int checkNum(Block block) {
        Block localBlock = dbBlockManager.getLastBlock();
        int localNum = 0;
        if (localBlock != null) {
            localNum = localBlock.getBlockHeader().getNumber();
        }
        //本地区块+1等于新来的区块高度时才同意
        if (localNum + 1 == block.getBlockHeader().getNumber()) {
            //同意生成区块
            return OK;
        }

        //拒绝
        return ERROR;
    }



    @Override
    public int checkBranch(Block block) {
        Block localBlock = dbBlockManager.getLastBlock();
        if (localBlock.getBlockHeader().getNumber() == block.getBlockHeader().getNumber() //如果新区块跟本地最末区块高度相同，且都指向同一个PreHash
                && localBlock.getBlockHeader().getPreviousBlockHash().equals(block.getBlockHeader().getPreviousBlockHash())
                && checkHash(block) == OK //且区块hash校验通过，说明是合法挖出来的区块
                && block.getBlockHeader().getNonce() > localBlock.getBlockHeader().getNonce()){//新区块计算次数大于最末区块
            //说明是区块链产生分叉，要做回滚处理
            return ERROR;
        }
        return OK;
    }


    @Override
    public int checkHash(Block block) {
        String hash = Sha256.sha256(block.getBlockHeader().toString() + block.getBlockBody().toString());
        if(!StrUtil.equals(block.getHash(),hash)){
            return ERROR;
        }
        return OK;
    }


    @Override
    public int checkPreHash(Block block) {
        Block lastBlock = dbBlockManager.getLastBlock();
        //创世块
        if (lastBlock == null && StringUtil.isEmpty(block.getBlockHeader().getPreviousBlockHash())) {
            return OK;
        }
        //新块的prev等于本地的last hash
        if (lastBlock != null && lastBlock.getHash().equals(block.getBlockHeader().getPreviousBlockHash())) {
            return OK;
        }
        return ERROR;
    }

    @Override
    public int checkTime(Block block) {
        Block lastBlock = dbBlockManager.getLastBlock();
        //新区块的生成时间比本地最后一个区块早
        if (lastBlock != null && block.getBlockHeader().getTimeStamp() <= lastBlock.getBlockHeader().getTimeStamp()) {
            //拒绝
            return ERROR;
        }
        return OK;
    }

    @Override
    public int checkSign(Transaction transaction) {
        boolean b = false;
        try {
            b = Cryptor.verifyString(transaction.getPublicKey(), Sha256.sha256(transaction.toString()), transaction.getSign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b ? OK : ERROR;
    }

    @Override
    public int checkPayAccount(Transaction transaction) {
        //验证付款账户是否存在
        Account sender = accountManager.getAccount(transaction.getFrom());
        if (sender == null) {
            return ERROR;
        }
        return OK;
    }


    @Override
    public int checkBlance(Transaction transaction) {
        //验证账户余额
        Account sender = accountManager.getAccount(transaction.getFrom());
        if (sender == null || sender.getBalance().compareTo(transaction.getAmount()) == -1) {
            return ERROR;
        }
        return OK;
    }

    @Override
    public int checkAmount(Transaction transaction) {
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0){
            return ERROR;
        }
        return OK;
    }

    @Override
    public int checkAddress(Transaction transaction) {
        boolean b = false;
        try {
            b = Cryptor.generateAddress(transaction.getPublicKey()).equals(transaction.getFrom());
        } catch (TrustSDKException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b ? OK : ERROR;
    }

    @Override
    public int checkTransactionTime(Transaction transaction) {
        Block lastBlock = dbBlockManager.getLastBlock();
        if (lastBlock == null){//创世区块
            return OK;
        }
        if (transaction.getTimestamp().compareTo(lastBlock.getBlockHeader().getTimeStamp()) == 1){
            return OK;
        }
        return ERROR;
    }


    @Override
    public int checkGenesisBlock(Block block) {
        if (new BigInteger(block.getHash(),16).compareTo(BigInteger.ZERO) == 0
                && StringUtil.isEmpty(block.getBlockHeader().getPreviousBlockHash())){
            return OK;
        }
        return ERROR;
    }


    @Override
    public int checkPermission(Block block) {
        //校验对表的操作权限
        // TODO: 2OK18/7/24 权限校验
        return OK;
    }



}
