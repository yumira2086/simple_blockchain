package com.blockchain.db;

import com.blockchain.base.BaseData;
import com.blockchain.bean.block.Account;
import com.blockchain.exception.ApiException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by: Yumira.
 * Created on: 2018/7/24-下午10:25.
 * Description:
 */
public interface AccountStore {

    /**
     * 添加一个钱包账户
     * @param account
     * @return
     */
    boolean putAccount(Account account) throws ApiException;

    /**
     * 获取指定的钱包账户
     * @param address
     * @return
     */
    Account getAccount(String address);
    /**
     * 获取指定账户的余额
     * @param address
     * @return
     */
    BigDecimal getBalance(String address);

    Map<String,BigDecimal> getAllAccount();

    void updateAccounts(Account account);


}
