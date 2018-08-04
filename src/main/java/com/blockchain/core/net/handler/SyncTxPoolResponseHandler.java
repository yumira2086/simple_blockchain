package com.blockchain.core.net.handler;

import com.blockchain.bean.transaction.Transaction;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * ——————尼玛保佑———————
 * --┏┓-----┏┓------
 * ┏┛┻━━━┛┻┓-----
 * ┃　　　━　　　┃-----
 * ┃　┳┛　┗┳　┃-----
 * ┃　　　┻　　　┃-----
 * ┗━┓　　　┏━┛-----
 * ---┃　　　┗━━━┓--
 * ---┃　       　　┣┓
 * ---┃　　　　　　　┏┛
 * ---┗┓┓┏━┳┓┏┛--
 * ----┗┻┛　┗┻┛----
 * ——————Bug退散————————
 * <p>
 * Created by: Yumira.
 * Created on: 2018/7/30-下午12:16.
 * Description:
 */
@Component
public class SyncTxPoolResponseHandler extends AbstractMessageHandler<String> {

    @Autowired
    private TransactionPool transactionPool;

    @Override
    public Class<String> parseBodyClass() {
        return String.class;
    }

    @Override
    public Object handler(MessagePacket packet, String body, ChannelContext channelContext) throws Exception {
        List<Transaction> transactions = JsonUtil.toList(body.toString(),Transaction.class);
        if (transactions.size() > 0){
            logger.info("收到来自 " + packet.getAppId() + " 的交易池数据，开始同步");
            for (Transaction transaction : transactions) {
                transactionPool.addTransaction(transaction);
            }
        }
        return null;
    }
}
