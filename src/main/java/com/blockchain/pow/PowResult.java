package com.blockchain.pow;


/**
 *  工作量计算结果
 *
 */
public class PowResult {

    /**
     * 计数器
     */
    private Double nonce;
    /**
     * hash值
     */
    private String hash;

    public PowResult(Double nonce, String hash) {
        this.nonce = nonce;
        this.hash = hash;
    }

    public Double getNonce() {
        return nonce;
    }

    public void setNonce(Double nonce) {
        this.nonce = nonce;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
