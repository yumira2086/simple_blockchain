package com.blockchain.core.net;

/**
 * 正数是请求类型，负数为响应类型
 * created by yumira 2018/3/9.
 */
public interface PacketType {
    /**
     * 连接成功
     */
    byte CONNECT_SUCCESS = 0;
    /**
     * 已挖到新的区块
     */
    byte BLOCK_COMPLETE_REQUEST = 1;
    /**
     * 同步新区块响应
     */
    byte BLOCK_COMPLETE_RESPONSE = -1;
    /**
     * 新账户创建
     */
    byte CREATE_ACCOUNT_REQUEST = 2;
    /**
     * 新账户创建响应
     */
    byte CREATE_ACCOUNT_RESPONSE = -2;
    /**
     * 发送消息
     */
    byte SEND_CONTENT_REQUEST = 3;
    /**
     * 当前群组成员
     */
    byte GROUP_NODES_RESPONSE = -3;
    /**
     * 同步区块的请求信息
     */
    byte SYNC_BLOCK_INFO_REQUEST = 5;
    /**
     * 同步区块的响应信息
     */
    byte SYNC_BLOCK_INFO_RESPONSE = -5;
    /**
     * 广播交易信息
     */
    byte TRANSACTION_INFO_REQUEST = 6;
    /**
     * 交易信息响应
     */
    byte TRANSACTION_INFO_RESPONSE = -6;
    /**
     * 广播交易池数据
     */
    byte TRANSACTION_POOL_REQUEST = 7;
    /**
     * 交易池信息响应
     */
    byte TRANSACTION_POOL_RESPONSE = -7;

}
