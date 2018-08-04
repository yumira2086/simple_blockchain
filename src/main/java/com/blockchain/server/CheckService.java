package com.blockchain.server;

import com.blockchain.bean.block.Block;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.checker.BlockChecker;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.ResultCode;
import com.blockchain.core.BlockExecutor;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.BlockManager;
import com.blockchain.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * created by yumira 2018/3/8.
 */
@Service
public class CheckService {

    public static final int OK = 0;
    public static final int ERROR = -1;

    private Logger logger = LoggerFactory.getLogger(CheckService.class);

    @Autowired
    private BlockChecker checker;
    @Autowired
    private BlockManager blockManager;
    @Autowired
    private BlockExecutor blockExecutor;

    /**
     * 校验区块信息,具体实现类 CheckImp
     * @param block block
     * @return 校验结果
     */
    public CheckResult checkBlock(Block block) throws ApiException, TrustSDKException {
        //如果是创世区块，不用下面的校验
        if (checker.checkGenesisBlock(block) == OK){
            return new CheckResult(OK, "OK");
        }
        //校验分叉
        if (checker.checkBranch(block) != OK){
            logger.info("区块链产生分叉，即将回滚交易");
            blockExecutor.rollback(blockManager.getLastBlock());
        }
        //校验区块高度是否匹配
        if (checker.checkNum(block) != OK) {
            return new CheckResult(ERROR, "block的高度不合法");
        }
        //校验前一区块
        if (checker.checkPreHash(block) != OK) {
            return new CheckResult(ERROR, "preHash与前一区块不符");
        }
        //校验时间戳
        if (checker.checkTime(block) != OK) {
            return new CheckResult(ERROR, "block的时间错误");
        }
        //校验区块中所有的交易是否合法
        if (checkTrans(block).getCode() != OK){
            return new CheckResult(ERROR, "交易信息不合法");
        }
        //校验hash
        if (checker.checkHash(block) != OK) {
            return new CheckResult(ERROR, "hash校验不通过");
        }
        //校验操作权限，暂时不需要，所以写死了
        if (checker.checkPermission(block) != OK) {
            return new CheckResult(ERROR, "没有数据写入权限");
        }
        return new CheckResult(OK, "OK");
    }

    /**
     * 校验交易是否合法
     *
     * @return 是否合法，为null则校验通过，其他则失败并返回原因
     */
    public CheckResult checkTrans(Block block) throws ApiException {

        if (block == null || block.getBlockBody() == null) {
            throw  new ApiException(ResultCode.FAIL, "Block参数缺失");
        }
        //校验交易条数
        List<Transaction> transactions = block.getBlockBody().getTransactions();
        // TODO: 2018/8/2 是否允许交易条数为空 ，不允许就取消注释
//        if (CollectionUtil.isEmpty(transactions)) {
//            throw  new ApiException(ResultCode.FAIL, "交易信息不能为空");
//        }
        //校验每一条交易
        CheckResult checkResult = new CheckResult(OK, "OK");
        for (Transaction transaction : transactions) {
            checkResult = checkTran(transaction);
            if (checkResult.getCode() == ERROR){
                return checkResult;
            }
        }
        return checkResult;
    }

    /**
     * 校验错误记得加上 transaction.setStatus(TransactionStatus.FAIL);
     * @param transaction
     * @return
     */
    public CheckResult checkTran(Transaction transaction) throws ApiException {
        //校验参数
        if (       transaction == null
                || StringUtil.isEmpty(transaction.getPublicKey())
                || StringUtil.isEmpty(transaction.getTxHash())
                || StringUtil.isEmpty(transaction.getSign())
                || StringUtil.isEmpty(transaction.getFrom())
                || StringUtil.isEmpty(transaction.getTo())) {
            throw  new ApiException(ResultCode.FAIL, "交易参数缺失");
        }
        //校验付款账户
        if (checker.checkPayAccount(transaction) != OK){
            throw  new ApiException(ResultCode.FAIL, "付款账户不存在:"+transaction.getFrom());
        }
        // 校验交易创建时间是否大于最后一个区块生成的时间，因为挖矿与交易池是实时交互的，所以  交易时间 < 区块生成时间  是不合法的，此举也可避免新区块挖出之后，重复交易的出现
        if (checker.checkTransactionTime(transaction) != OK){
            throw  new ApiException(ResultCode.FAIL, "交易时间不合法，请重新创建交易，tx:"+transaction.getTxHash());
        }
        //校验地址
        if (checker.checkAddress(transaction) != OK) {
            throw  new ApiException(ResultCode.FAIL, "地址与公钥不匹配，tx:"+transaction.getTxHash());
        }
        //校验签名
        if (checker.checkSign(transaction) != OK) {
            throw  new ApiException(ResultCode.FAIL, "交易签名错误，tx:"+transaction.getTxHash());
        }
        //校验转账金额
        if (checker.checkAmount(transaction) != OK) {
            throw  new ApiException(ResultCode.FAIL, "交易金额不合法: "+transaction.getAmount());
        }
        //验证账户余额
        if (checker.checkBlance(transaction) != OK) {
            throw  new ApiException(ResultCode.FAIL, "账户余额不足: "+transaction.getFrom());
        }
        return new CheckResult(OK, "OK");
    }

}
