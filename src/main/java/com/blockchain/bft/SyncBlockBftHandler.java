package com.blockchain.bft;

import com.blockchain.bean.block.Block;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午2:56.
 * Description: 同步区块的处理器
 */

public class SyncBlockBftHandler extends SimpleBft<Block> {

    @Override
    public boolean equals(Block object1, Block object2) {
        if (object1.getHash().equals(object2.getHash())){
            return true;
        }
        return false;
    }

    @Override
    public void onAgreement() {

    }

    @Override
    public void onAgreeFail() {

    }
}
