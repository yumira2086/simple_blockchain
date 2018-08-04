package com.blockchain.core.net.handler;

import com.blockchain.bean.block.Account;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.manmger.AccountManager;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.math.BigDecimal;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午8:39.
 * Description:
 */
@Component
public class NewAccountRequestHandler extends AbstractMessageHandler<Account> {


    @Override
    public Class<Account> parseBodyClass() {
        return Account.class;
    }

    /**
     * @param packet packet
     * @param body 解析后的对象
     * @param channelContext channelContext
     * @return
     * @throws Exception
     */
    @Override
    public Object handler(MessagePacket packet, Account body, ChannelContext channelContext) throws Exception {

        logger.info("收到 "+channelContext.getClientNode()+" 的新建账户请求，开始打包到账户池 {}", body.getAddress());
        AccountPool accountPool = ApplicationContextProvider.getBean(AccountPool.class);
        AccountManager accountManager = ApplicationContextProvider.getBean(AccountManager.class);
        if (body.getBalance().compareTo(BigDecimal.ZERO) == 0){//余额为0
            //将新账户存入本地数据库
            accountManager.putAccount(body);
            //将账户推入账户池，
            accountPool.putAccount(body);
//            logger.info("交易校验通过，已打包到交易池 tx：" + body.getTxHash());
        }
        //无需响应
        return null;
    }
}
