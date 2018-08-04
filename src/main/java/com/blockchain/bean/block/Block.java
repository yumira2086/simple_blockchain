package com.blockchain.bean.block;

/**
 * 区块
 * created by yumira
 */
public class Block {
    /**
     * 区块头
     */
    private BlockHeader blockHeader;
    /**
     * 区块body
     */
    private BlockBody blockBody;
    /**
     * 该区块的hash
     */
    private String hash;

    /**
     * 构建一个Block
     * @param blockHeader
     * @param blockBody
     * @return
     */
    public static Block build(BlockHeader blockHeader,BlockBody blockBody){
        if (blockHeader == null || blockBody == null){
            return null;
        }
        Block block = new Block();
        block.setBlockBody(blockBody);
        block.setBlockHeader(blockHeader);
        return block;
    }


    public BlockHeader getBlockHeader() {
        return blockHeader;
    }

    public void setBlockHeader(BlockHeader blockHeader) {
        this.blockHeader = blockHeader;
    }

    public BlockBody getBlockBody() {
        return blockBody;
    }

    public void setBlockBody(BlockBody blockBody) {
        this.blockBody = blockBody;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "blockHeader=" + blockHeader +
                ", blockBody=" + blockBody +
                ", hash='" + hash + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Block){
            return hash.equals(((Block) obj).getHash());
        }
        return super.equals(obj);
    }
}
