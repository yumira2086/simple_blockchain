package com.blockchain.core.net.handler;

import com.blockchain.checker.CheckResult;
import com.blockchain.common.App;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.server.CheckService;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午9:41.
 * Description:
 */
@Component
public class ConfirmBlockResponseHandler extends AbstractMessageHandler<CheckResult> {

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
            App.ALLOW_MINE = false;

        }
        return null;
    }
}
