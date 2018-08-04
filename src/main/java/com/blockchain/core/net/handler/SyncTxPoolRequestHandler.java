package com.blockchain.core.net.handler;

import com.blockchain.bean.transaction.Transaction;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.Sender;
import com.blockchain.core.pool.TransactionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午12:06.
 * Description:
 */
@Component
public class SyncTxPoolRequestHandler extends AbstractMessageHandler {

    @Autowired
    private TransactionPool transactionPool;

    @Override
    public Class parseBodyClass() {
        return Object.class;
    }

    @Override
    public Object handler(MessagePacket packet, Object body, ChannelContext channelContext) throws Exception {
        logger.info("收到 " + packet.getAppId() + " 的 同步交易池 的消息");
        //从交易池取出数据
        List<Transaction> transactions = transactionPool.getTransactions();
        if (transactions.size() > 0){
            //把本地交易池数据返给对方
            MessagePacket messagePacket = MessageBuilder.syncTransactionPoolRespPacket(transactions);
            //让对方知道回复的是哪条消息
            messagePacket.setResponseMsgId(packet.getMessageId());
            //通过channelContext，把最新数据返回给对方
            Sender.sendTo(channelContext,messagePacket);
        }else {//本地无数据可同步
            //不作处理
            logger.info("本地交易池没有交易数据可同步");
        }
        return null;
    }

}
