package com.blockchain.db;

import cn.hutool.json.JSONUtil;
import com.blockchain.bean.block.Account;
import com.blockchain.common.Constants;
import com.blockchain.common.ResultCode;
import com.blockchain.exception.ApiException;
import com.blockchain.utils.JsonUtil;
import com.blockchain.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by: Yumira.
 * Created on: 2018/7/24-下午10:46.
 * Description:
 */
@Component
public class AccountImpl implements AccountStore{

    @Autowired
    private DbStore levelDb;

    /**b
     * 地址 ==> 余额
     * @param account
     * @return
     */
    @Override
    public boolean putAccount(Account account) throws ApiException {
        try {
            levelDb.put(Constants.WALLETS_BUCKET_PREFIX + account.getAddress(), account.getBalance().toString());
        } catch (Exception e) {
            throw new ApiException(ResultCode.FAIL,"账户创建失败");
        }
        return true;
    }

    @Override
    public Account getAccount(String address) {
        BigDecimal balance = getBalance(address);
        if (balance == null) {
            return null;
        }
        return new Account(address,balance);
    }

    @Override
    public BigDecimal getBalance(String address) {
        String balance = levelDb.get(Constants.WALLETS_BUCKET_PREFIX + address);
        if (StringUtil.isEmpty(balance)) {
            return null;
        }
        return new BigDecimal(balance);
    }

    /**
     * 获取所有用户信息，仅供controller调用
     * @return
     */
    @Override
    public Map<String,BigDecimal> getAllAccount(){
        Map<String,BigDecimal> map = new HashMap<>();
        String result = levelDb.get(Constants.ALL_ACCOUNT_DATA);
        if (StringUtil.isEmpty(result)){
            return map;
        }
        List<Account> accounts = JsonUtil.toList(result, Account.class);
        for (Account account : accounts) {
            map.put(account.getAddress(),account.getBalance());
        }
        return map;
    }

    /**
     * 更新对外展示的所有账户信息，慎用
     * 若上线正式环境，无需对外暴露所有账户信息，请干掉这个方法
     * @param account
     */
    @Override
    public void updateAccounts(Account account){
        String result = levelDb.get(Constants.ALL_ACCOUNT_DATA);
        List<Account> accounts = new LinkedList<>();
        if (StringUtil.isEmpty(result)){
            accounts.add(account);
        }else {
            accounts.addAll(JsonUtil.toList(result, Account.class));
            boolean flag = false;
            for (Account a : accounts) {
                if (account.equals(a)){
                    flag = true;
                }
            }
            if (flag) {
                accounts.remove(account);
            }
            accounts.add(account);
        }
        levelDb.put(Constants.ALL_ACCOUNT_DATA,JsonUtil.toJSONString(accounts));
    }

}
