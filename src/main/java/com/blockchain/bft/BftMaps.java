package com.blockchain.bft;

import com.blockchain.bean.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by: Yumira.
 * Created on: 2018/8/28-下午10:50.
 * Description:
 */
public class BftMaps {

    public static Map<Block,Integer> SyncBlockMap = new HashMap<>();
    public static Map<Object,Integer> ConfirmBlockMap = new HashMap<>();
}
