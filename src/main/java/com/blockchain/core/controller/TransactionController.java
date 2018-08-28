package com.blockchain.core.controller;

import com.blockchain.base.BaseData;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.ResultGenerator;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.bean.transaction.TransactionRequest;
import com.blockchain.common.TransactionStatus;
import com.blockchain.checker.RequestChecker;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.PacketType;
import com.blockchain.core.net.Sender;
import com.blockchain.exception.ApiException;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.server.CheckService;
import com.blockchain.server.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by: Yumira.
 * Created on: 2018/7/26-下午3:41.
 * Description:
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    public TransactionService transactionService;
    @Autowired
    public CheckService checkService;
    @Autowired
    public TransactionPool transactionPool;


    /**
     * 构建一条交易，因为需要私钥签名，建议放在前端处理，避免私钥传递
     *
     {
    "from": "11pkwDUGPM5hURFGhvdxXrYeMbxJ4bxPY",
    "to": "14Jau3qb1i5MeAxkWSSKbcpN6hTLR9U5fi",
    "amount": 0.1,
    "privateKey": "IyJfQhKm8XVcQPOsas6qiNvFxPVo9pULIeOjedoA6GQ="
    }
     */
    @PostMapping("/build")
    public BaseData buildTransaction(@RequestBody TransactionRequest transactionRequest) throws ApiException{
        //这里校验请求参数
        RequestChecker.checkTransaction(transactionRequest);
        Transaction transaction = transactionService.buildTransaction(transactionRequest);
        //这里校验交易数据的合法性
        if (checkService.checkTran(transaction).getCode() != CheckService.OK) {
            transaction.setStatus(TransactionStatus.FAIL);
            ResultGenerator.genFailResult("交易构建失败");
        }
        //踩坑了，这里要注意一下返回结果会不会舍弃小数0，比如 0.0 ==> 0 最后导致hash校验不通过
        return ResultGenerator.genSuccessResult(transaction);
    }


    /**
     * 确认交易
     *
     * 用户确认，并且校验通过后，将交易打包到交易池，并广播交易给其他节点
     */
    @PostMapping("/confirm")
    public BaseData sendTransaction(@RequestBody Transaction transaction) throws ApiException {
        CheckResult result = checkService.checkTran(transaction);
        boolean confirm = transactionPool.addTransaction(transaction);
        //打包信息到交易池成功,即当前交易池不存在此交易hash
        if (confirm && result.getCode() == CheckService.OK){
            //发送给其他节点
            Sender.sendGroup(MessageBuilder.buildTransactionPacket(transaction, PacketType.TRANSACTION_INFO_REQUEST));
            return ResultGenerator.genSuccessResult();
        }else if (!confirm){
            return ResultGenerator.genFailResult("当前交易已存在");
        }else {
            return ResultGenerator.genFailResult(result.getMessage());
        }
    }

    /**
     * 查看本地交易池中的交易
     */
    @PostMapping("/pool")
    public BaseData getPool() throws ApiException {
        return ResultGenerator.genSuccessResult(transactionPool.getTransactions());
    }

    /**
     * 清空本地交易池中的交易
     */
    @PostMapping("/pool/clear")
    public BaseData clearPool() throws ApiException {
        transactionPool.clearPool();
        return ResultGenerator.genSuccessResult();
    }
}
