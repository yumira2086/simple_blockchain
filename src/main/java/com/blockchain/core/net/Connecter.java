package com.blockchain.core.net;

import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.common.Constants;
import com.blockchain.core.net.client.MessageClientListener;
import com.blockchain.db.DbStore;
import com.blockchain.utils.CommonUtil;
import com.blockchain.utils.JsonUtil;
import com.blockchain.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.client.AioClient;
import org.tio.client.ClientGroupContext;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.utils.lock.SetWithLock;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Created by: Yumira.
 * Created on: 2018/8/1-下午2:26.
 * Description:
 */
@Component
public class Connecter {

    @Autowired
    public ClientGroupContext clientGroupContext;

    private static Logger logger = LoggerFactory.getLogger(Connecter.class);

    public static AioClient aioClient;

    public void init(MessageClientListener messageClientListener) throws IOException {
        clientGroupContext.setClientAioListener(messageClientListener);
        clientGroupContext.setName(Constants.GROUP_NAME);
        aioClient = new AioClient(clientGroupContext);
    }

    public static void connectTo(Node serverNode) throws Exception {
        aioClient.asynConnect(serverNode,App.HTTP_TIMEOUT);
        saveNode(serverNode);//存到本地
    }


    public static void saveNode(Node node){
        DbStore dbStore = ApplicationContextProvider.getBean(DbStore.class);
        List<Node> nodeList = getNodes();
        if (!nodeList.contains(node)){
            nodeList.add(node);
            dbStore.put(Constants.LOCAL_NODES, JsonUtil.toJSONString(nodeList));
        }
    }

    public static List<Node> getNodes(){
        DbStore dbStore = ApplicationContextProvider.getBean(DbStore.class);
        List<Node> nodeList = new ArrayList<>();
        String nodes = dbStore.get(Constants.LOCAL_NODES);
        if (!StringUtil.isEmpty(nodes)){
            nodeList.addAll(JsonUtil.toList(nodes, com.blockchain.bean.block.Node.class));
        }
        return nodeList;
    }


    /**
     * 这里要考虑一下什么时候删除本地节点
     * @param node
     */
    public static void removeNode(Node node){
        DbStore dbStore = ApplicationContextProvider.getBean(DbStore.class);
        List<Node> nodeList = getNodes();
        if (nodeList.contains(node)){
            nodeList.remove(node);
            dbStore.put(Constants.LOCAL_NODES, JsonUtil.toJSONString(nodeList));
        }
    }

    /**
     * 获取当前在线的节点,不包括自己
     * @return
     */
    public static List<Node> getCurrentOnlineNodes(){
        ClientGroupContext clientGroupContext = ApplicationContextProvider.getBean(ClientGroupContext.class);
//        SetWithLock<ChannelContext> connecteds = clientGroupContext.connecteds;
        SetWithLock<ChannelContext> allChannelContexts = clientGroupContext.connecteds;
        Set<ChannelContext> set = allChannelContexts.getObj();
        //已连接的节点集合
        Set<org.tio.core.Node> connectedNodes = set.stream().map(ChannelContext::getServerNode).collect(Collectors.toSet());
        return new ArrayList<>(connectedNodes);
    }

    /**
     * 获取当前在线的节点
     * @return
     */
    public static List<Node> getCurrentOnlineNodesContainsLocal(){
        List<Node> currentOnlineNodes = getCurrentOnlineNodes();
        currentOnlineNodes.add(new Node(App.LOCAL_IP, App.LOCAL_PORT));
        return currentOnlineNodes;
    }

    /**
     * 此处连接的server的ip需要和服务器端保持一致，服务器删了，这边也要移出Group
     * 建立新连接
     * 如果收到对方连接成功的消息，检查自己有没有连接对方，如果没有连接，就自动连接对方
     */
    public static void bindToGroup(Set<Node> serverNodes) {
        //当前已经连接的
        Set<Node> connectedNodes = new HashSet<>(getCurrentOnlineNodesContainsLocal());
        //连接新节点
        for (Node node : serverNodes) {
            if (!connectedNodes.contains(node)) {
                connect(node);
            }
        }
    }



    /**
     * 连接一个节点
     * @param serverNode
     */
    public static void connect(Node serverNode) {
        try {
            if (!serverNode.equals(Connecter.getLocalServerNode())){
                logger.info("开始连接" + ":" + serverNode.toString());
                Connecter.connectTo(serverNode);
            }
        } catch (Exception e) {
            logger.info("连接 "+serverNode+" 失败");
        }
    }



    public static void unBind(ChannelContext channelContext){
        Aio.unbindGroup(Constants.GROUP_NAME,channelContext);
        Aio.remove(channelContext, "关闭连接" + channelContext.getServerNode());
//        removeNode(channelContext.getServerNode());//先注释掉
    }



    /**
     * 当前群组成员
     * @return
     */
    public static int currentConnectedSize(){
        return getCurrentOnlineNodesContainsLocal().size(); //加上自己
    }

    public static Node getLocalServerNode(){
        InetAddress localHost = CommonUtil.getLocalHostLANAddress();
        return new Node(localHost.getHostAddress(),App.LOCAL_PORT);
    }

    /**
     * 通过总节点数求出pbft算法中拜占庭节点数量f，总节点数为3f+1
     */
    public static int pbftSize() {
        //当前所有在线节点
        int total = getCurrentOnlineNodes().size();
        int f = (total - 1) / 3;
        if (f <= 0) {
            f = 1;
        }
        //如果要单节点测试，此处返回值改为0
        if(App.IS_SINGLE_NODE) return 0;
        return f;
    }

    /**
     * 通过恶意节点数量，求出允许放行的最小票数 2f+1
     * @return
     */
    public static int pbftAgreeCount() {
        return pbftSize() * 2 + 1;
    }

}
