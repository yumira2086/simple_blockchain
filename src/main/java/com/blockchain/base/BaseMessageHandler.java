package com.blockchain.base;

import com.blockchain.core.net.MessagePacket;
import com.blockchain.exception.ApiException;
import org.tio.core.ChannelContext;

import java.io.UnsupportedEncodingException;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午10:37.
 * Description: 消息处理接口
 */
public interface BaseMessageHandler<T> {
    /**
     * 统一封装的消息处理方法
     */
    Object handler(MessagePacket messagePacket,ChannelContext channelContext) throws Exception;
}
