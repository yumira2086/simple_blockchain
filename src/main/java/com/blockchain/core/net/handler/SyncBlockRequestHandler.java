package com.blockchain.core.net.handler;

import com.blockchain.bean.block.Block;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.Sender;
import com.blockchain.manmger.BlockManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/28-上午12:51.
 * Description:
 */
@Component
public class SyncBlockRequestHandler extends AbstractMessageHandler<String> {

    @Autowired
    private BlockManager blockManager;

    @Override
    public Class<String> parseBodyClass() {
        return String.class;
    }

    @Override
    public Object handler(MessagePacket packet, String body, ChannelContext channelContext) throws Exception {

        logger.info("收到 " + channelContext.getClientNode() + " 的 同步最新Block 的消息，请求者的lastblock为：" + body);
        //传来的hash如果为空，说明对方一个Block都没有，是新节点
        //查询自己的nextblock，返回给对方
        Block nextBlock = blockManager.getNextBlockByHash(body);
        //如果为null，代表已经是最新区块了
        MessagePacket messagePacket = MessageBuilder.buildOneBlockPacket(nextBlock);
        //让对方知道回复的是哪条消息
        messagePacket.setResponseMsgId(packet.getMessageId());
        //通过channelContext，把最新区块包返回给对方
        Sender.sendTo(channelContext,messagePacket);
        if (nextBlock == null) {
            logger.info("和 " + channelContext.getClientNode() + " 相比，本地的区块是最新的");
        }
        return null;
    }
}
