package com.blockchain.core.net.handler;

import com.blockchain.bean.block.Block;
import com.blockchain.checker.CheckResult;
import com.blockchain.core.BlockExecutor;
import com.blockchain.core.net.AbstractMessageHandler;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.MessagePacket;
import com.blockchain.core.net.Sender;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.manmger.BlockManager;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.server.CheckService;
import com.blockchain.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午8:40.
 * Description:
 */
@Component
public class BlockMineSuccessHandler extends AbstractMessageHandler<Block> {

    @Autowired
    private BlockManager blockManager;
    @Autowired
    private CheckService checkService;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private AccountPool accountPool;
    @Autowired
    private BlockExecutor blockExecutor;


    @Override
    public Class<Block> parseBodyClass() {
        return Block.class;
    }

    /**
     * 这里是收到其他人挖到新区快消息的处理方法，需要做n件事
     * 1.校验分叉，如果由于网络阻塞等原因，收到跟本地最末尾区块相同区块高度的多个区块，且hash校验都通过，代表区块链产生分叉，
     *   判断基准可以根据实际情况取舍，产生分叉后，本地最末区块一定已经被执行，这里要回滚数据，重新执行合法的分叉区块
     * 2.校验新区块的合法性
     * 3.上面校验都通过后，清空本地交易池中已存在于区块中的交易数据
     * 4.清空本地账户池中的数据并更新本地数据库
     * 5.拼接到本地区块链上
     * 6.执行新区块中BlockDody中的内容，顺序一定是先更新本地账户数据库，再执行交易，
     *   因为不排除有其他账户往新地址里打钱，且交易和账户被打包到同一个区块里，防止顺序出错，我写在BlockExcutor里统一执行了
     * 7.执行POW的矿工奖励，我也写在Excutor里了
     * 8.执行完毕，告诉对方
     */
    @Override
    public Object handler(MessagePacket packet, Block body, ChannelContext channelContext) throws Exception {
        logger.info("收到 " + channelContext.getClientNode() + " 挖到新区块的消息 Block: "+ JsonUtil.toJSONString(body));
        //校验新区块的合法性
        CheckResult confirm = checkService.checkBlock(body);
        if (confirm.getCode() == CheckService.OK){
            //清空本地交易池中已存在于区块中的交易数据
            transactionPool.updateTransactions(body);
            //清空本地账户池中已存在于区块中的新账户
            accountPool.updateAccounts(body);
            //拼接到本地区块链上
            blockManager.addBlock(body);
            //执行区块体中的信息
            blockExecutor.execute(body);
            logger.info("区块 " + body.getHash() + " 校验通过，已同步到本地");
        }
        //回复同步消息
        MessagePacket messagePacket = MessageBuilder.buildBlockResponsePacket(confirm);
        messagePacket.setResponseMsgId(packet.getMessageId());
        Sender.sendTo(channelContext,messagePacket);
        return null;
    }
}
