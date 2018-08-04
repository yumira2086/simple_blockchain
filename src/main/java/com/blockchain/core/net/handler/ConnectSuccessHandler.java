package com.blockchain.core.net.handler;

import com.blockchain.bean.block.Node;
import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.*;
import com.blockchain.utils.CommonUtil;
import com.blockchain.utils.JsonUtil;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午11:27.
 * Description:
 */
@Component
public class ConnectSuccessHandler extends AbstractMessageHandler<Node> {

    @Override
    public Class<Node> parseBodyClass() {
        return Node.class;
    }

    @Override
    public Object handler(MessagePacket packet, Node body, ChannelContext channelContext) throws Exception {
        logger.info("收到来自 " + body + "  连接成功 的消息，准备检查连接状态");
        Set<Node> nodes = new HashSet<>();
        //如果收到对方连接成功的消息，检查自己有没有连接对方，如果没有连接，就自动连接对方
        nodes.add(new Node(channelContext.getClientNode().getIp(),body.getPort()));
        Connecter.bindToGroup(CommonUtil.toCoreNodeSet(nodes));
        //告诉对方当前群组内所有成员，如果是新节点的话，可以迅速连接至P2P网络
        Sender.sendTo(channelContext, MessageBuilder.buildGroupNodesPacket(JsonUtil.toJSONString(Connecter.getCurrentOnlineNodes())));
        return null;
    }
}
