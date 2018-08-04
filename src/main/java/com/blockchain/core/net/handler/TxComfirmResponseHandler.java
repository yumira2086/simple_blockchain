package com.blockchain.core.net.handler;

import com.blockchain.bean.transaction.Transaction;
import com.blockchain.common.TransactionStatus;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-上午11:10.
 * Description:
 */
@Component
public class TxComfirmResponseHandler extends AbstractMessageHandler<Transaction> {

    @Override
    public Class<Transaction> parseBodyClass() {
        return Transaction.class;
    }

    @Override
    public Object handler(MessagePacket packet, Transaction body, ChannelContext channelContext) throws Exception {
        if (body == null || body.getStatus() == TransactionStatus.FAIL){
            logger.error("来自 "+packet.getAppId()+" 的<交易确认失败>消息, {}", body);
        }else {
            logger.info("来自 "+packet.getAppId()+" 的<交易确认成功>消息, {}", body.getTxHash());
        }
        return null;
    }
}
