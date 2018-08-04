package com.blockchain.core.net;

import com.blockchain.core.net.server.MessageServerAioHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by: Yumira.
 * Created on: 2018/7/28-下午1:42.
 * Description: 处理器动态代理类
 */
public class HandlerProxy implements InvocationHandler {

    private AbstractMessageHandler messageHandler;
    private Logger logger = LoggerFactory.getLogger(MessageServerAioHandler.class);

    public HandlerProxy(AbstractMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.debug("beforeHandler");
        method.invoke(messageHandler, args);
        logger.debug("afterHandler");
        return null;
    }
}
