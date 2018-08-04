package com.blockchain.core.net;

import com.blockchain.utils.JsonUtil;

/**
 * created by yumira 2018/7/27.
 */
public class PacketBuilder<T>{
    /**
     * 消息类型，其值在Type中定义
     */
    private byte type;

    private T body;

    public  PacketBuilder<T> setType(byte type) {
        this.type = type;
        return this;
    }

    public PacketBuilder<T> setBody(T body) {
        this.body = body;
        return this;
    }

    public MessagePacket build() {
        return new MessagePacket(type, JsonUtil.toJSONString(body));
    }
}
