/**
 * Project Name:trustsql_sdk
 * File Name:PairKey.java
 * Package Name:com.tencent.trustsql.sdk.bean
 * Date:Jul 26, 201710:27:04 AM
 * Copyright (c) 2017, Tencent All Rights Reserved.
 */

package com.blockchain.bean.block;

import com.blockchain.crypto.Cryptor;
import com.blockchain.utils.StringUtil;

/**
 * Created by: Yumira.
 * Created on: 2018/7/28-上午10:29.
 * Description: 实现了中文私钥的密钥对  私钥为12个常用汉字   汉字之间可以有任意多个空格
 *              汉字私钥或者私钥字符生成的公钥都是唯一的，在本项目中，汉字私钥只是私钥的另一种形式，
 *              可以用两者之间的任意一个对数据进行签名或者生成地址，处理的时候会自动匹配
 */
public class PairKey {

    /**
     * 公钥字符串
     */
    private String publicKey;
    /**
     * 私钥汉字
     */
    private String keyWords;
    /**
     * 私钥字符串
     */
    private String privateKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeyWords() {
        char[] chars = keyWords.toCharArray();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            buffer.append(chars[i]+" ");
        }
        return buffer.toString().trim();
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getPrivateKey() {
        if (StringUtil.isEmpty(privateKey)){
            return Cryptor.convertToPrivateKey(keyWords);
        }
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}

