package com.blockchain.crypto;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * created by yumira 2018/2/27.
 */
public class Sha256 {
    public static String sha256(String input) {
        return DigestUtil.sha256Hex(input);
    }

}
