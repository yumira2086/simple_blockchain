package com.blockchain.core.net;

import com.blockchain.base.BaseMessageHandler;
import com.blockchain.common.Constants;
import com.blockchain.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午10:43.
 * Description:
 */
public abstract class AbstractMessageHandler<T> implements BaseMessageHandler{

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 告诉抽象类body类型
     * @return
     */
    public abstract Class<T> parseBodyClass();

    /**
     * handler处理
     * @param packet packet
     * @param body 解析后的对象
     * @param channelContext channelContext
     * @return 用不上
     * @throws Exception Exception
     */
    public abstract Object handler(MessagePacket packet, T body, ChannelContext channelContext) throws Exception;

    /**
     * 收到消息后做二次处理
     * @param packet
     * @param channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object handler(MessagePacket packet, ChannelContext channelContext) throws Exception {
        String jsonStr;
        T t = null;
        if (packet.getBody() != null) {
            jsonStr = new String(packet.getBody(), Constants.CHARSET);
            t = JsonUtil.toBean(jsonStr, parseBodyClass());
        }
        return handler(packet, t, channelContext);
    }




}
