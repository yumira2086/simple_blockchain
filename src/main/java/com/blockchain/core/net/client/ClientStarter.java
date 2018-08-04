package com.blockchain.core.net.client;

import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.common.Constants;
import com.blockchain.core.net.Connecter;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.Sender;
import com.blockchain.manmger.BlockManager;
import com.blockchain.utils.CommonUtil;
import com.blockchain.utils.JsonUtil;
import com.blockchain.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tio.client.AioClient;
import org.tio.client.ClientGroupContext;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.utils.lock.SetWithLock;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午2:56.
 * Description:
 */
@Component
@DependsOn(value = "serverStarter")
public class ClientStarter extends MessageClientListener{

    @Autowired
    private BlockManager blockManager;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private volatile boolean isNodesReady = false; // 节点是否已准备好


    /**
     * 启动节点后自动连接数据库中的其他节点ip
     */
    @EventListener(ApplicationReadyEvent.class)
    public void connectToGroup() throws IOException {
        ApplicationContextProvider.getBean(Connecter.class).init(this);
        Connecter.bindToGroup(new HashSet<>(Connecter.getNodes()));
        if (!StringUtil.isEmpty(App.PUBLIC_KEY)){
            logger.info("当前节点的账户公钥为：" + App.PUBLIC_KEY);
        }
    }


    @Override
    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String s, boolean b) {
        logger.info("连接关闭：节点地址为-" + channelContext.getServerNode());
        Connecter.unBind(channelContext);
    }

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
        if (isConnected) {
            logger.info("连接节点成功，地址为-" + channelContext.getServerNode());
            //连接成功后，将节点加入群组
            Sender.addToGroup(channelContext, Constants.GROUP_NAME);
            //告诉对方连接成功
            Sender.sendTo(channelContext,new MessageBuilder().buildConnectSuccessPacket());
            List<Node> currentOnlineNodes = Connecter.getCurrentOnlineNodesContainsLocal();
            logger.info("当前群组成员："+ JsonUtil.toJSONString(currentOnlineNodes));
            //如果当前连接节点大于1，发送同步区块的请求
            if(!App.IS_SINGLE_NODE && currentOnlineNodes.size() > 0){
                synchronized (ClientStarter.this) {
                    if(!isNodesReady){
                        isNodesReady = true;
                        //发送同步广播
                        syncBlock();
                        syncTransactionPool();

                    }
                }
            }
        } else {
            logger.info("连接节点失败，地址为-" + channelContext.getServerNode());
            Connecter.unBind(channelContext);
        }

    }

    /**
     * 定时同步最新的Block
     * 5min
     */
    @Scheduled(fixedRate = 300000,initialDelay = 300000)
    public void syncBlock() {
        logger.info("---------开始同步新区块--------");
        //在这里发请求，去获取group别人的新区块
        MessagePacket syncMessage = MessageBuilder.buildSyncBlockPacket(blockManager.getLastBlockHash());
        Sender.sendGroup(syncMessage);
    }


    /**
     * 定时同步最新的交易池
     * 不需要同步账户信息
     */
    @Scheduled(fixedRate = 300000,initialDelay = 300000)
    public void syncTransactionPool() {
        logger.info("---------开始同步交易池--------");
        //在这里发请求，去获取别人最大的交易池
        MessagePacket syncMessage = MessageBuilder.syncTransactionPoolPacket();
        Sender.sendGroup(syncMessage);
    }




}
