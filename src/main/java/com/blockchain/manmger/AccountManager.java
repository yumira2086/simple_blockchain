package com.blockchain.manmger;

import com.blockchain.bean.block.Account;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.Sender;
import com.blockchain.core.net.handler.SyncBlockResponseHandler;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.db.AccountStore;
import com.blockchain.exception.ApiException;
import com.blockchain.crypto.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by: Yumira.
 * Created on: 2018/7/24-下午11:18.
 * Description:
 */
@Component
public class AccountManager {

    @Autowired
    private AccountStore accountStore;
    @Autowired
    private AccountPool accountPool;
    @Autowired
    private SyncBlockResponseHandler syncBlockResponseHandler;


    /**
     * 创建或者更新本地账户
     */
    public void putAccount(Account account) throws ApiException {
        accountStore.putAccount(account);
        updateAccounts(account);//正式版需要注掉此行
    }

    /**
     * 获取指定的钱包账户
     * @param address
     * @return
     */
    public Account getAccount(String address){
        Account account = accountStore.getAccount(address);
        if (account == null){//如果本地数据库找不到，尝试去交易池里找
            List<String> accounts = accountPool.getAccounts();
            //交易池里找不到就说明正在同步区块，去当前正在同步的区块体里找
            if (accounts.contains(address)
                    || syncBlockResponseHandler.getCurrentSyncBlock() != null
                    && syncBlockResponseHandler.getCurrentSyncBlock()
                                               .getBlockBody()
                                               .getAddresses()
                                               .contains(address)){
                account = new Account(address,BigDecimal.ZERO);
            }
        }
        return account;
    }

    /**
     * 创建一个新账户，1.存入本地数据库  2.放入本地账户池 3.通知其他人新账户的创建
     * @return
     */
    public Account newAccount(String publicKey) throws Exception {
        String address = Cryptor.generateAddress(publicKey);
        Account account = new Account(address, BigDecimal.ZERO);
        putAccount(account);//1.
        accountPool.putAccount(account);//2.
        Sender.sendGroup(MessageBuilder.newAccountRequestPacket(account));//3.
        return account;
    }


    /**
     * 下面两个方法仅供测试环境使用，方便查看当前账户概览
     */
    public Map<String, BigDecimal> getAllAccount(){
        return accountStore.getAllAccount();
    }

    public void updateAccounts(Account account){
        accountStore.updateAccounts(account);
    }
}
