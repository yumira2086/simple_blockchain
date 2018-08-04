package com.blockchain.manmger;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.blockchain.bean.block.Block;
import com.blockchain.common.Constants;
import com.blockchain.db.DbStore;
import com.blockchain.utils.StringUtil;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.utils.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/17-下午2:48.
 * Description:
 */
@Component
public class BlockManager {

    /**
     * 创世区块
     */
    public static final String ZERO_HASH = Hex.encodeHexString(new byte[32]);
    /**
     * 第一个区块hash的key，value就是第一个区块的hash
     */
    public static final String KEY_FIRST_BLOCK = "key_first_block";
    /**
     * 保存区块的hash和下一区块hash，key为hash，value为下一区块hash，方便获取某一个block的下一个Block
     * 因为KV数据库没有遍历操作，这样可以实现遍历
     */
    public static final String KEY_BLOCK_NEXT_PREFIX = "key_next_";
    /**
     * 最后一个区块hash的key，value就是最后一个区块的hash
     */
    public static final String KEY_LAST_BLOCK = "key_last_block";

    @Autowired
    private DbStore dbStore;

    /**
     * 添加新区块
     * @param block
     */
    public synchronized void addBlock(Block block) {
        String hash = block.getHash();
        //如果已经存在了，说明已经更新过该Block了
        if (!StringUtil.isEmpty(dbStore.get(hash))) {
            return;
        }
        //保证本地的区块hash可以串连起来
        if (!getLastBlockHash().equals(block.getBlockHeader().getPreviousBlockHash())){
            return;
        }
        //如果没有上一区块，说明该块就是创世块
        if (StringUtil.isEmpty(block.getBlockHeader().getPreviousBlockHash())) {
            dbStore.put(KEY_FIRST_BLOCK, hash);//存放创世区块
        } else {
            //保存上一区块对该区块的key value映射，k为上一区块的hash，v为本区块的hash
            dbStore.put(KEY_BLOCK_NEXT_PREFIX + block.getBlockHeader().getPreviousBlockHash(), hash);
        }
        //以Json格式存入levelDB
        dbStore.put(hash, Json.toJson(block));
        //更新最末尾区块
        updateLastBlock(hash);
    }

    /**
     * 通过hash取出区块
     */
    public Block getBlockByHash(String hash) {
        String blockJson = dbStore.get(hash);
        return JSON.parseObject(blockJson, Block.class);
    }

    /**
     * 查找第一个区块
     *
     * @return 第一个Block
     */
    public Block getFirstBlock() {
        String firstBlockHash = dbStore.get(KEY_FIRST_BLOCK);
        if (StrUtil.isEmpty(firstBlockHash)) {
            return null;
        }
        return getBlockByHash(firstBlockHash);
    }

    /**
     * 获取最后一个区块
     *
     * @return 最后一个区块
     */
    public Block getLastBlock() {
        String lastBlockHash = dbStore.get(KEY_LAST_BLOCK);
        if (StrUtil.isEmpty(lastBlockHash)) {
            return null;
        }
        return getBlockByHash(lastBlockHash);
    }

    /**
     * 更新最后一个区块
     */
    public void updateLastBlock(String lastHash) {
        //更新最末尾区块
        dbStore.put(KEY_LAST_BLOCK, lastHash);
    }

    /**
     * 获取所有区块
     * @return
     */
    public List<Block> getBlockChain(){
        List<Block> list = new ArrayList<>();
        Block temp = getFirstBlock();
        if (temp != null){
            list.add(temp);
            while (getNextBlock(temp) != null){
                list.add(getNextBlock(temp));
                temp = getNextBlock(temp);
            }
        }
        return list;
    }


    /**
     * 获取最后一个区块的hash
     */
    public String getLastBlockHash() {
        Block block = getLastBlock();
        if (block != null) {
            return block.getHash();
        }
        return "";
    }

    /**
     * 获取最后一个block的number，即区块高度
     */
    public int getLastBlockNumber() {
        Block block = getLastBlock();
        if (block != null) {
            return block.getBlockHeader().getNumber();
        }
        return 0;
    }

    /**
     * 获取某一个block的下一个Block
     */
    public Block getNextBlock(Block block) {
        if (block == null) {
            return getFirstBlock();
        }
        String nextHash = dbStore.get(KEY_BLOCK_NEXT_PREFIX + block.getHash());
        if (StringUtil.isEmpty(nextHash)) {
            return null;
        }
        return getBlockByHash(nextHash);
    }

    /**
     * 通过hash，获取某一个block的下一个Block
     */
    public Block getNextBlockByHash(String hash) {
        if (StringUtil.isEmpty(hash)) {
            return getFirstBlock();
        }
        String nextHash = dbStore.get(KEY_BLOCK_NEXT_PREFIX + hash);
        if (StringUtil.isEmpty(nextHash)) {
            return null;
        }
        return getBlockByHash(nextHash);
    }


    /**
     * 设置挖矿公钥
     */
    public void putPublicKey(String publicKey) {
        dbStore.put(Constants.PUBLICKEY,publicKey);
    }

    /**
     * 获取挖矿公钥
     */
    public String getPublicKey() {
        return dbStore.get(Constants.PUBLICKEY);
    }

}
