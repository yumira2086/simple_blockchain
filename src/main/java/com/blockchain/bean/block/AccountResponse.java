package com.blockchain.bean.block;

/**
 * Created by: Yumira.
 * Created on: 2018/7/26-上午10:36.
 * Description:
 */
public class AccountResponse {

    private String keyWords;

    private String privateKey;

    private String publicKey;

    private String address;

    public AccountResponse(String keyWords,String privateKey, String publicKey, String address) {
        this.privateKey = privateKey;
        this.keyWords = keyWords;
        this.publicKey = publicKey;
        this.address = address;
    }

    public AccountResponse() {
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public String toString() {
        return "AccountResponse{" +
                "keyWords='" + keyWords + '\'' +
                ",privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
