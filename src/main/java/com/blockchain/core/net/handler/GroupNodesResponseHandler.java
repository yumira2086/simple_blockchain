package com.blockchain.core.net.handler;

import com.alibaba.fastjson.JSONArray;
import com.blockchain.bean.block.Node;
import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.Connecter;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.utils.CommonUtil;
import com.blockchain.utils.JsonUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by: Yumira.
 * Created on: 2018/8/1-下午1:26.
 * Description:
 */
@Component
public class GroupNodesResponseHandler extends AbstractMessageHandler<JSONArray> {

    @Override
    public Class<JSONArray> parseBodyClass() {
        return JSONArray.class;
    }

    @Override
    public Object handler(MessagePacket packet, JSONArray body, ChannelContext channelContext) throws Exception {
        if (body != null) {
            Set<Node> nodes = new HashSet<>(JsonUtil.toList(body.toString(), Node.class));
            //这里要过滤掉本地已经连接上的所有节点，不然会重复连接造成资源浪费
            HashSet<org.tio.core.Node> localSet = new HashSet<>(Connecter.getCurrentOnlineNodesContainsLocal());
            Set<Node> temp = new HashSet<>();
            temp.addAll(nodes);
            temp.retainAll(localSet);//先取交集
            nodes.removeAll(temp);//再取差集  过滤掉本地已连接的节点
            if (nodes.size() > 0) {
                logger.info("准备连接新节点：" + nodes);
            }
            Connecter.bindToGroup(CommonUtil.toCoreNodeSet(nodes));
        }
        return null;
    }
}
