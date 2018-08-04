package com.blockchain.core.net.server;

import com.blockchain.common.Constants;
import com.blockchain.core.net.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午2:50.
 * Description:
 */
public class MessageServerListener implements ServerAioListener {
	private static Logger logger = LoggerFactory.getLogger(MessageServerListener.class);

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		logger.info("收到 {} 的连接", channelContext);

		//连接后，需要把连接会话对象设置给channelContext
		//channelContext.setAttribute(new ShowcaseSessionContext());
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int i) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int i) throws Exception {
//		logger.info("onAfterReceived channelContext:{}, packet:{}, packetSize:{}");
	}


	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
//		logger.info("onAfterSent channelContext:{}, packet:{}, isSentSuccess:{}", channelContext, Json.toJson(packet), isSentSuccess);
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long l) throws Exception {

	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
//		Sender.unBind(channelContext);
	}
}
