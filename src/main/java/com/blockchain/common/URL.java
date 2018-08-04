package com.blockchain.common;

/**
 * Created by: Yumira.
 * Created on: 2018/7/29-上午2:02.
 * Description:
 */
public class URL {


    public static String getManagerBaseUrl(){
        return App.MANAGER_URL;
    }

    /**
     * 插入账户
     */
    public static String getInsertAccountUrl() {
        return getManagerBaseUrl() + "account/insert";
    }

    /**
     * 获取账户
     */
    public static String getAccountUrl() {
        return getManagerBaseUrl() + "account";
    }
}
