package com.blockchain.core.net;

import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.common.Constants;
import com.blockchain.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.client.ClientGroupContext;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;

import javax.annotation.Resource;

import static com.blockchain.common.Constants.GROUP_NAME;

/**
 * 发送消息的工具类
 * created by yumira 2018/3/12.
 */
@Component
public class Sender {

    public static Logger logger = LoggerFactory.getLogger(Sender.class);

    public static void sendGroup(MessagePacket messagePacket) {
        //对外发出广播，消费者 DisruptorClientHandler 和 DisruptorServerHandler 同时接收消息并根据blockPacket的type类型分别处理
//        ApplicationContextProvider.publishEvent(new ClientRequestEvent(messagePacket));
        //发送到一个group
        Aio.sendToGroup(ApplicationContextProvider.getBean(ClientGroupContext.class), GROUP_NAME, messagePacket);
    }

    public static void sendTo(ChannelContext channelContext,MessagePacket messagePacket){
        messagePacket.setAppId(App.NAME);
        Aio.send(channelContext, messagePacket);
    }

    public static void addToGroup(ChannelContext channelContext,String groupName){
        Aio.bindGroup(channelContext, groupName);
    }


}
