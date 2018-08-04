package com.blockchain.core.net.handler;

import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.utils.StringUtil;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

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
 * Created on: 2018/8/1-下午3:22.
 * Description:
 */
@Component
public class MessageReceivedHandler extends AbstractMessageHandler<String> {

    @Override
    public Class<String> parseBodyClass() {
        return String.class;
    }

    @Override
    public Object handler(MessagePacket packet, String body, ChannelContext channelContext) throws Exception {
        if (!StringUtil.isEmpty(body)){
            logger.info(packet.getAppId() + "：" + body);
        }
        return null;
    }
}
