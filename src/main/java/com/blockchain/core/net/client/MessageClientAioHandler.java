package com.blockchain.core.net.client;

import com.blockchain.core.net.AbstractAioHandler;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.PacketType;
import com.blockchain.core.net.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午2:50.
 * Description:
 */
@Component
public class MessageClientAioHandler extends AbstractAioHandler implements ClientAioHandler{

    @Autowired
    private SyncBlockResponseHandler syncBlockResponseHandler;
    @Autowired
    private SyncTxPoolResponseHandler syncTxPoolResponseHandler;
    @Autowired
    private TxComfirmResponseHandler txComfirmResponseHandler;
    @Autowired
    private ConfirmBlockResponseHandler confirmBlockResponseHandler;
    @Autowired
    private GroupNodesResponseHandler groupNodesResponseHandler;

    private Logger logger = LoggerFactory.getLogger(MessageClientAioHandler.class);

    private static Map<Byte, AbstractMessageHandler<?>> handlerMap = new HashMap<>();

    @PostConstruct
    public void init() {
        //收到同步区块的响应
        handlerMap.put(PacketType.SYNC_BLOCK_INFO_RESPONSE, syncBlockResponseHandler);
        //收到同步交易池的响应
        handlerMap.put(PacketType.TRANSACTION_POOL_RESPONSE, syncTxPoolResponseHandler);
        //交易确认的响应
        handlerMap.put(PacketType.TRANSACTION_INFO_RESPONSE, txComfirmResponseHandler);
        //交易确认的响应
        handlerMap.put(PacketType.BLOCK_COMPLETE_RESPONSE, confirmBlockResponseHandler);
        //获取当前群组成员的响应
        handlerMap.put(PacketType.GROUP_NODES_RESPONSE, groupNodesResponseHandler);
    }

    /**
     * 关闭心跳
     * @return
     */
    @Override
    public Packet heartbeatPacket() {
        return null;
    }

    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {

        MessagePacket messagePacket = (MessagePacket) packet;
        Byte type = messagePacket.getType();
        AbstractMessageHandler<?> blockHandler = handlerMap.get(type);
        if (blockHandler != null) {
            //消费消息
//            logger.info("收到 " + channelContext.getServerNode() + " 对消息 " + messagePacket.getResponseMsgId() + " 的响应");
            blockHandler.handler(messagePacket, channelContext);
        }else {
            logger.error("错误的响应类型："+messagePacket.getType());
        }

    }
}
