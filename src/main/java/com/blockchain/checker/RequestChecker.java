package com.blockchain.checker;

import com.blockchain.common.ResultCode;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.bean.transaction.TransactionRequest;
import com.blockchain.exception.ApiException;

/**
 * Created by: Yumira.
 * Created on: 2018/7/26-下午4:20.
 * Description:
 */
public class RequestChecker {

    /**
     * 检查构建交易的请求
     * @param transaction
     * @throws ApiException
     */
    public static void checkTransaction(TransactionRequest transaction) throws ApiException{
        checkNotNull(transaction.getFrom(),"付款地址不能为空");
        checkNotNull(transaction.getTo(),"收款地址不能为空");
        checkNotNull(transaction.getPrivateKey(),"缺少私钥");
        if (transaction.getFrom().equals(transaction.getTo())) {
            throw new ApiException(ResultCode.PARAMETER_ERROR,"收付款地址不可以相同");
        }
    }
    /**
     * 检查hash之前的交易参数是否缺失
     * @param transaction
     * @throws ApiException
     */
    public static Transaction checkPreHashTransction(Transaction transaction) throws ApiException{
        checkNotNull(transaction.getFrom(),"付款地址不能为空");
        checkNotNull(transaction.getTo(),"收款地址不能为空");
        checkNotNull(transaction.getPublicKey(),"缺少公钥");
        checkNotNull(transaction.getAmount(),"缺少转账金额");
        checkNotNull(transaction.getTimestamp(),"缺少时间戳");
        return transaction;
    }

    public static <T> T checkNotNull(T t) throws ApiException{
        if (t == null) {
            throw new ApiException(ResultCode.PARAMETER_ERROR,"请求参数缺失");
        } else {
            return t;
        }
    }

    public static <T> T checkNotNull(T t,String message) throws ApiException{
        if (t == null) {
            throw new ApiException(ResultCode.PARAMETER_ERROR,message);
        } else {
            return t;
        }
    }
}
