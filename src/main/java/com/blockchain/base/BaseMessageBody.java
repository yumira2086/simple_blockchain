package com.blockchain.base;


import com.blockchain.common.App;
import com.blockchain.utils.CommonUtil;
import org.tio.core.intf.Packet;

public class BaseMessageBody extends Packet {

	/**
	 * 消息发送时间
	 */
	private Long time = System.currentTimeMillis();
    /**
     * 每条消息的唯一id
     */
	private long messageId = CommonUtil.createMsgId();
    /**
     * 回复的哪条消息
     */
	private long responseMsgId;
    /**
     * 自己是谁
     */
	private String appId = App.VALUE;



    public BaseMessageBody() {
    }

    /**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

    public long getResponseMsgId() {
        return responseMsgId;
    }

    public void setResponseMsgId(long responseMsgId) {
        this.responseMsgId = responseMsgId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }


	@Override
	public String toString() {
		return "BasePacketBody{" +
				"time=" + time +
				", messageId='" + messageId + '\'' +
				", responseMsgId='" + responseMsgId + '\'' +
				", appId='" + appId + '\'' +
				'}';
	}
}
