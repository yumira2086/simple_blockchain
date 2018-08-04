package com.blockchain.bean.block;

import java.util.List;

/**
 * 区块头
 * created by yumira 2018/2/27.
 */
public class BlockHeader {
    /**
     * 版本号
     */
    private int version;
    /**
     * 上一区块的hash
     */
    private String previousBlockHash = "";
    /**
     * merkle tree根节点hash
     */
    private String hashMerkleRoot;
    /**
     * 生成该区块的公钥，也就是挖到矿后矿工费用发送的地址
     */
    private String publicKey;
    /**
     * 区块高度
     */
    private int number;
    /**
     * 时间戳
     */
    private long timeStamp;
    /**
     * 随机数,从0开始递增
     */
    private long nonce;
    /**
     * 难度目标位，如果为0 ，target就是79位长整型
     */
    public int targetBits;

    /**
     * 该区块里每条交易信息的hash集合，按顺序来的，通过该hash集合能算出根节点hash
     */

    @Override
    public String toString() {
        return "BlockHeader{" +
                "version=" + version +
                ", hashPreviousBlock='" + previousBlockHash + '\'' +
                ", hashMerkleRoot='" + hashMerkleRoot + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", number=" + number +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                ", targetBits=" + targetBits +
                '}';
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getHashMerkleRoot() {
        return hashMerkleRoot;
    }

    public void setHashMerkleRoot(String hashMerkleRoot) {
        this.hashMerkleRoot = hashMerkleRoot;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public int getTargetBits() {
        return targetBits;
    }

    public void setTargetBits(int targetBits) {
        this.targetBits = targetBits;
    }
}
