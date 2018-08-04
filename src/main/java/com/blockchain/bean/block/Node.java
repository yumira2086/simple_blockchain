package com.blockchain.bean.block;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by: Yumira.
 * Created on: 2018/7/28-上午12:14.
 * Description: core.Node没有无参构造，json解析会报错
 */
public class Node extends org.tio.core.Node{


    public Node() {
        super(null,0);
    }

    public Node(String ip, int port) {
        super(ip, port);
    }

}
