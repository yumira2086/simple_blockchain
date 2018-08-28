package com.blockchain.core.net;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.bean.block.Account;
import com.blockchain.bean.block.Block;
import com.blockchain.bean.block.Node;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.core.net.handler.ConfirmBlockResponseHandler;
import com.blockchain.core.net.handler.SyncBlockResponseHandler;
import com.blockchain.manmger.BlockManager;
import com.blockchain.utils.JsonUtil;

import java.util.List;

/**
 * 构建消息包的builder
 * created by yumira 2018/7/27.
 */
public class MessageBuilder {


    /**
     * 构建一条连接成功的消息,返回当前服务器节点
     * @return
     */
    public static MessagePacket buildConnectSuccessPacket() {
        MessagePacket packet = new PacketBuilder<Node>()
                .setType(PacketType.CONNECT_SUCCESS)
                .setBody(new Node(App.LOCAL_IP, App.LOCAL_PORT))
                .build();
        return packet;
    }

    /**
     * 返回组群
     * @return
     */
    public static MessagePacket buildGroupNodesPacket(String nodes) {
        MessagePacket packet = new PacketBuilder<JSONArray>()
                .setType(PacketType.GROUP_NODES_RESPONSE)
                .setBody(JSONArray.parseArray(nodes))
                .build();
        return packet;
    }

    /**
     * 构建一条sync消息
     * @return
     */
    public static MessagePacket buildSyncBlockPacket(String hash) {
        ApplicationContextProvider.getBean(SyncBlockResponseHandler.class).startBft();
        MessagePacket packet = new PacketBuilder<String>()
                .setType(PacketType.SYNC_BLOCK_INFO_REQUEST)
                .setBody(hash)
                .build();
        return packet;
    }

    /**
     * 构建一条Block响应消息
     * @return
     */
    public static MessagePacket buildOneBlockPacket(Block block) {
        MessagePacket packet = new PacketBuilder<Block>()
                .setType(PacketType.SYNC_BLOCK_INFO_RESPONSE)
                .setBody(block)
                .build();
        return packet;
    }

    /**
     * 构建一条交易消息
     * @return
     */
    public static MessagePacket buildTransactionPacket(Transaction transaction,byte type) {
        MessagePacket packet = new PacketBuilder<Transaction>()
                .setType(type)
                .setBody(transaction)
                .build();
        return packet;
    }

    /**
     * 构建一条同步交易池消息
     * @return
     */
    public static MessagePacket syncTransactionPoolPacket() {
        MessagePacket packet = new PacketBuilder<String>()
                .setType(PacketType.TRANSACTION_POOL_REQUEST)
                .setBody("")
                .build();
        return packet;
    }

    /**
     * 构建一条同步交易池消息的响应信息
     * @return
     */
    public static MessagePacket syncTransactionPoolRespPacket(List<Transaction> transactions) {
        MessagePacket packet = new PacketBuilder<String>()
                .setType(PacketType.TRANSACTION_POOL_RESPONSE)
                .setBody(JsonUtil.toJSONString(transactions))
                .build();
        return packet;
    }

    /**
     * 构建一条挖到新区块的消息
     * @return
     */
    public static MessagePacket newBlockRequestPacket(Block block) {
        MessagePacket packet = new PacketBuilder<Block>()
                .setType(PacketType.BLOCK_COMPLETE_REQUEST)
                .setBody(block)
                .build();
        return packet;
    }

    /**
     * 构建一条挖到新区块的消息
     * @return
     */
    public static MessagePacket buildBlockResponsePacket(CheckResult checkResult) {
        ApplicationContextProvider.getBean(ConfirmBlockResponseHandler.class).startBft();
        MessagePacket packet = new PacketBuilder<CheckResult>()
                .setType(PacketType.BLOCK_COMPLETE_RESPONSE)
                .setBody(checkResult)
                .build();
        return packet;
    }

    /**
     * 构建一条创建新账户的消息
     * @return
     */
    public static MessagePacket newAccountRequestPacket(Account account) {
        MessagePacket packet = new PacketBuilder<Account>()
                .setType(PacketType.CREATE_ACCOUNT_REQUEST)
                .setBody(account)
                .build();
        return packet;
    }
}
