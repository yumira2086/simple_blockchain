package com.blockchain.core.net.server;

import com.blockchain.core.net.*;
import com.blockchain.core.net.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27.
 * Description:
 */
@Component
public class MessageServerAioHandler extends AbstractAioHandler implements ServerAioHandler {

    @Autowired
    private ConnectSuccessHandler connectSuccessHandler;
    @Autowired
    private SyncBlockRequestHandler syncBlockRequestHandler;
    @Autowired
    private SyncTxPoolRequestHandler syncTxPoolRequestHandler;
    @Autowired
    private TxComfirmRequestHandler txComfirmRequestHandler;
    @Autowired
    private BlockMineSuccessHandler blockMineSuccessHandler;
    @Autowired
    private NewAccountRequestHandler newAccountRequestHandler;
    @Autowired
    private MessageReceivedHandler messageReceivedHandler;


    private Logger logger = LoggerFactory.getLogger(MessageServerAioHandler.class);
    //用来装消息处理器的容器
    private static Map<Byte, AbstractMessageHandler<?>> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        //连接成功的消息处理器
        handlerMap.put(PacketType.CONNECT_SUCCESS, connectSuccessHandler);
        //同步区块的消息处理器
        handlerMap.put(PacketType.SYNC_BLOCK_INFO_REQUEST, syncBlockRequestHandler);
        //收到交易池同步的处理器
        handlerMap.put(PacketType.TRANSACTION_POOL_REQUEST, syncTxPoolRequestHandler);
        //收到交易消息的处理器
        handlerMap.put(PacketType.TRANSACTION_INFO_REQUEST, txComfirmRequestHandler);
        //挖到新块的消息处理器
        handlerMap.put(PacketType.BLOCK_COMPLETE_REQUEST, blockMineSuccessHandler);
        //收到创建账户消息的处理器
        handlerMap.put(PacketType.CREATE_ACCOUNT_REQUEST, newAccountRequestHandler);
        //节点之间消息接收处理器
        handlerMap.put(PacketType.SEND_CONTENT_REQUEST, messageReceivedHandler);
    }
    /**
     * 自己是server，此处接收到客户端来的消息。这里是入口
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        MessagePacket messagePacket = (MessagePacket) packet;
        AbstractMessageHandler<?> messageHandler = handlerMap.get(messagePacket.getType());
        if (messageHandler != null) {
            messageHandler.handler(messagePacket, channelContext);
        }else {
            logger.error("错误的消息类型："+messagePacket.getMessageId()+":"+new String(messagePacket.getBody()));
        }
    }
}
