package com.blockchain.server;

import com.blockchain.common.ResultCode;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.bean.transaction.TransactionRequest;
import com.blockchain.checker.RequestChecker;
import com.blockchain.crypto.Sha256;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;
import com.blockchain.crypto.Cryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by: Yumira.
 * Created on: 2018/7/26-下午4:44.
 * Description:
 */
@Component
public class TransactionService {


    /**
     * 通过请求构建一条交易
     * @param transactionRequest
     * @return
     * @throws TrustSDKException
     * @throws ApiException
     * @throws UnsupportedEncodingException
     */
    public Transaction buildTransaction(TransactionRequest transactionRequest) throws ApiException{
        Transaction transaction;
        try {
            transaction = new Transaction();
            BeanUtils.copyProperties(transactionRequest, transaction);
            transaction.setPublicKey(Cryptor.generatePublicKey(transactionRequest.getPrivateKey()));
            transaction.setTxHash(Sha256.sha256(RequestChecker.checkPreHashTransction(transaction).toString()));
            transaction.setSign(Cryptor.signString(transactionRequest.getPrivateKey(),transaction.getTxHash()));
        } catch (Exception e) {
            throw new ApiException(ResultCode.PARAMETER_ERROR,"参数异常");
        }
        return transaction;
    }


}
