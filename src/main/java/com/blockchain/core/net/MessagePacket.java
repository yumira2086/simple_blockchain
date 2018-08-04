package com.blockchain.core.net;

import com.blockchain.base.BaseMessageBody;
import com.blockchain.common.Constants;

import java.io.UnsupportedEncodingException;

/**
 * Created by: Yumira.
 * Created on: 2018/7/27-下午2:25.
 * Description:
 */
public class MessagePacket extends BaseMessageBody {

    /**
     *  消息头的长度
     *  type 1字节
     *  responseMsgId 8字节
     */
    public static final int HEADER_LENGTH = 13;
    /**
     * 消息类型，其值在Type中定义
     */
    private byte type;//这里要定义为byte类型，不然解码会报错

    private byte[] body;

    private String content;

    public MessagePacket() {
        super();
    }

    /**
     * @param type type
     * @param body body
     * created by tanyaowu
     */
    public MessagePacket(byte type, byte[] body) {
        super();
        this.type = type;
        this.body = body;
    }

    public MessagePacket(byte type, String body) {
        super();
        this.type = type;
        setBody(body);
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    @Override
    public String logstr() {
        return "" + type;
    }

    /**
     * @param body
     *         the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        try {
            this.body = body.getBytes(Constants.CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param type
     *         the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }


}
