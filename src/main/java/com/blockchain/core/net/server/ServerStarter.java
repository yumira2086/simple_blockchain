package com.blockchain.core.net.server;

import com.blockchain.common.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by: Yumira.
         * Created on: 2018/7/27-下午2:50.
         * Description:
         */
@Component
@DependsOn(value = "app")
public class ServerStarter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void serverStart() throws IOException {
        org.tio.server.intf.ServerAioHandler serverAioHandler = new MessageServerAioHandler();
        org.tio.server.intf.ServerAioListener serverAioListener = new MessageServerListener();
        ServerGroupContext serverGroupContext = new ServerGroupContext(App.LOCAL_IP,serverAioHandler, serverAioListener);
        serverGroupContext.setHeartbeatTimeout(-1);//不需要心跳
        AioServer aioServer = new AioServer(serverGroupContext);
        //本机启动服务
        aioServer.start(App.LOCAL_IP, App.LOCAL_PORT);
        logger.info("开启server服务，监听端口:"+App.LOCAL_IP+":"+App.LOCAL_PORT);
    }
}
