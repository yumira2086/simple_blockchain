package com.blockchain.core.net.handler;

import com.blockchain.bean.transaction.Transaction;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.common.TransactionStatus;
import com.blockchain.core.net.*;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.server.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-上午10:57.
 * Description:
 */
@Component
public class TxComfirmRequestHandler extends AbstractMessageHandler<Transaction> {

    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private CheckService checkService;

    @Override
    public Class<Transaction> parseBodyClass() {
        return Transaction.class;
    }

    @Override
    public Object handler(MessagePacket packet, Transaction body, ChannelContext channelContext) throws Exception {
        logger.info("收到 "+packet.getAppId()+" 的交易确认请求， {}", body);
        CheckResult checkResult = checkService.checkTran(body);
        if (checkResult.getCode() == CheckService.OK){
            //将交易放入交易池，默认提交状态
            transactionPool.addTransaction(body);
//            logger.info("交易校验通过，已打包到交易池 tx：" + body.getTxHash());
        }else {
            body.setStatus(TransactionStatus.FAIL);
            logger.info("来自 "+packet.getAppId()+" 的交易确认失败，原因： {}", checkResult.getMessage());
        }

        //响应确认交易的结果
        MessagePacket messagePacket = MessageBuilder.buildTransactionPacket(body, PacketType.TRANSACTION_INFO_RESPONSE);
        //让对方知道回复的是哪条消息
        messagePacket.setResponseMsgId(packet.getMessageId());
        //通过channelContext，响应对方
        Sender.sendTo(channelContext,messagePacket);
        return null;
    }
}
