package com.blockchain.pow;

import com.blockchain.bean.block.Block;
import com.blockchain.checker.BlockChecker;
import com.blockchain.checker.CheckResult;
import com.blockchain.common.App;
import com.blockchain.common.ApplicationContextProvider;
import com.blockchain.config.POWConfig;
import com.blockchain.core.BlockExecutor;
import com.blockchain.core.net.MessageBuilder;
import com.blockchain.core.net.Sender;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.core.pool.TransactionPool;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.BlockManager;
import com.blockchain.server.CheckService;
import com.blockchain.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by: Yumira.
 * Created on: 2018/7/24-下午9:20.
 * Description:
 */

@Component
public class WorkingListener implements WorkerCallBack {

    @Autowired
    private BlockManager blockManager;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private BlockChecker blockChecker;
    @Autowired
    private AccountPool accountPool;
    @Autowired
    private BlockExecutor blockExecutor;
    @Autowired
    private CheckService checkService;

    private Logger logger = LoggerFactory.getLogger(POWConfig.class);

    private boolean flag = false;

    @Scheduled(fixedRate = 1001)
    public void setFlag(){
        flag = true;
    }

    /**
     * 开始
     * @param startTime
     */
    @Override
    public void onStart(Long startTime) {
        logger.info("=====================开始挖矿=====================");
    }

    /**
     * 挖矿进度回调
     * @param timeConsuming
     * @param nonce
     * @param currentHash
     */
    @Override
    public void onWorking(Long timeConsuming, Long nonce, String currentHash) {
        if (flag) {
            int curSec = (int) (timeConsuming / 1000);
            logger.info("耗时 " + curSec + " 秒，当前进度 "+ nonce +" 次，当前hash：" + currentHash);
            flag = false;
        }
    }

    /**
     * 这里就是挖到矿之后的回调事件，需要做n件事
     * 1.修改区块中交易状态为确认,这个在挖矿方法里已经执行了
     * 2.把新块拼到本地区块链上，
     * 3.执行新区块里的信息
     * 4.清空交易池和账户池，因为挖到矿意味着里面所有数据都已经被打包了，一旦收到新交易，可以设置自动开始挖矿
     * 5.告诉其他人自己挖到的新块，同步之，
     */
    @Override
    public void onComplete(Block block) throws ApiException, TrustSDKException {
        logger.info("挖出新区块："+ JsonUtil.toJSONString(block));
        CheckResult confirm = checkService.checkBlock(block);
        if (confirm.getCode() == CheckService.OK){
            //拼接到本地区块链上
            blockManager.addBlock(block);
            //执行区块体中的信息
            blockExecutor.execute(block);
            logger.info("区块 " + block.getHash() + " 校验通过，已同步到本地");
        }
        if (blockChecker.checkGenesisBlock(block) != CheckService.OK) {
            transactionPool.clearPool();
            accountPool.clearPool();
        }
        Sender.sendGroup(MessageBuilder.newBlockRequestPacket(block));
        //自动继续挖矿
        if (App.AUTO_WORK) {
            ApplicationContextProvider.getBean(ProofOfWork.class).mine();
        }
    }


}
