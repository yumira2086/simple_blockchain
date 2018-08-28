package com.blockchain.core.net.handler;

import com.blockchain.bean.block.Block;
import com.blockchain.bft.BftMaps;
import com.blockchain.bft.SimpleBft;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.App;
import com.blockchain.core.BlockExecutor;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.client.ClientStarter;
import com.blockchain.manmger.BlockManager;
import com.blockchain.server.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午9:41.
 * Description:
 */
@Component
public class ConfirmBlockResponseHandler extends AbstractMessageHandler<CheckResult> {


    @Autowired
    private BlockExecutor blockExecutor;
    @Autowired
    private BlockManager blockManager;
    @Autowired
    private ClientStarter clientStarter;

    public ConcurrentHashMap<Object,Integer> bftMap = new ConcurrentHashMap<>();

    private SimpleBft bft = initBft();

    @Override
    public Class<CheckResult> parseBodyClass() {
        return CheckResult.class;
    }

    @Override
    public Object handler(MessagePacket packet, CheckResult body, ChannelContext channelContext) throws Exception {
        if (body.getCode() == CheckService.OK){
            logger.info("来自 "+channelContext.getServerNode()+" 的<Block确认成功>消息, {}", body);
        }else {
            logger.error("来自 "+channelContext.getServerNode()+" 的<Block确认失败>消息, {}", body);
            App.ALLOW_MINE = false;//此处说明产生分叉，暂停挖矿

            // TODO: 2018/8/28 解决共识的并发问题
//            bft.receiveEvent(SimpleBft.KEY);
        }
        return null;
    }


    public void startBft(){
        bftMap.clear();
    }

    /**
     * 这里用拜占庭容错算法校验是否是本地区块错误导致分叉
     */
    private SimpleBft initBft(){
        return new SimpleBft<Object>(BftMaps.ConfirmBlockMap) {
            @Override
            public boolean equals(Object object1, Object object2) {
                return true;
            }

            @Override
            public void onAgreement() {
                blockExecutor.rollback(blockManager.getLastBlock());//回滚错误区块
                clientStarter.syncBlock();//重新同步区块
                App.ALLOW_MINE = true;
            }

            @Override
            public void onAgreeFail() {
                // TODO: 2018/8/28 共识没有分叉 ，说明是其他节点的问题
                App.ALLOW_MINE = true;
            }
        };
    }
}
