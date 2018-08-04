package com.blockchain.core;

import com.blockchain.bean.block.Account;
import com.blockchain.bean.block.Block;
import com.blockchain.bean.block.BlockBody;
import com.blockchain.bean.block.BlockHeader;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.checker.BlockChecker;
import com.blockchain.common.App;
import com.blockchain.core.net.handler.SyncBlockResponseHandler;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.AccountManager;
import com.blockchain.manmger.BlockManager;
import com.blockchain.server.CheckService;
import com.blockchain.crypto.Cryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午8:05.
 * Description: 区块确认后，执行区块体中交易和账户信息的执行器
 */
@Component
public class BlockExecutor {

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private BlockChecker checker;
    @Autowired
    private SyncBlockResponseHandler syncBlockResponseHandler;
    @Autowired
    private BlockManager blockManager;

    private Logger logger = LoggerFactory.getLogger(BlockExecutor.class);

    /**
     * 执行区块体中的指令，顺序执行 1.新账户 2.交易 3.矿工奖励
     * @param block
     */
    public void execute(Block block) throws ApiException {
        //跳过创世区块
        if (checker.checkGenesisBlock(block) != CheckService.OK){
            executeAccounts(block.getBlockBody());
            executeTransactions(block.getBlockBody());
            executeWorkReward(block.getBlockHeader());
        }
    }

    /**
     * 回滚区块体中的指令  1.交易 2.矿工奖励
     */
    public void rollback(Block block) {
        try {
            rollbackTransactions(block.getBlockBody());
            rollbackReward(block.getBlockHeader());
            //这里需要更新的LastBlock是当前block的prevhash
            blockManager.updateLastBlock(block.getBlockHeader().getPreviousBlockHash());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行矿工奖励
     * 取出区块头里的公钥，生成地址，即矿工费接收地址
     */
    private void executeWorkReward(BlockHeader blockHeader) {
        String publicKey = blockHeader.getPublicKey();
        try {
            String address = Cryptor.generateAddress(publicKey);
            workReward(address);
        } catch (Exception e) {
            // 公钥信息错误，矿工奖励作废
            e.printStackTrace();
        }
    }


    /**
     * 遍历区块体中的地址，存入本地作为新账户
     * 请确认传入的block是经过校验后的block
     * 至少要保证block的区块高度不小于本地最后一个区块
     */
    private void executeAccounts(BlockBody blockBody) throws ApiException {
        List<String> addresses = blockBody.getAddresses();
        for (String address : addresses) {
            accountManager.putAccount(new Account(address,BigDecimal.ZERO));
        }
    }


    /**
     * 交易执行方法,这里存在几种情况，需要好好考虑
     * 1.如果收款方账户不存在，是否需要继续执行
     *   这里我选择用收款地址创建一个新账户，对新账户执行转账并存入数据库，这个新账户是没有私钥的，意味着这笔钱的丢失
     * 2.是否需要考虑转账金额的溢出，参考solidity，嘛........不过java应该没什么好担心的
     * 3.保证绝对的线程安全
     */
    private synchronized void executeTransactions(BlockBody blockBody) throws ApiException {
        List<Transaction> transactions = blockBody.getTransactions();
        for (Transaction transaction : transactions) {
            //转账账户
            Account address_from = accountManager.getAccount(transaction.getFrom());
            //收款账户
            Account address_to = accountManager.getAccount(transaction.getTo());

            //如果收款地址账户不存在，则创建一个新账户
            if (address_to == null) {
                address_to = new Account(transaction.getTo(), BigDecimal.ZERO);
            }
            //执行转账操作,更新账户余额
            safeTran(address_from, address_to, transaction.getAmount());
        }
    }

    /**
     * 交易回滚
     * @param blockBody
     * @throws ApiException
     */
    private synchronized void rollbackTransactions(BlockBody blockBody) throws ApiException {
        List<Transaction> transactions = blockBody.getTransactions();
        for (Transaction transaction : transactions) {
            //转账账户变成收款账户
            Account address_from = accountManager.getAccount(transaction.getTo());
            //收款账户变成转账账户
            Account address_to = accountManager.getAccount(transaction.getFrom());
            //收款地址在这里不可能不存在，
            //执行转账回滚操作
            safeTran(address_from, address_to, transaction.getAmount());
        }
    }

    /**
     * 执行转账
     */
    public void safeTran(Account from,Account to,BigDecimal amount) throws ApiException {
        BigDecimal from_balance = from.getBalance();//这里要用两个临时变量存一下值，道理大家都懂，就不用讲了
        BigDecimal to_balance = to.getBalance();
        //如果转账账户扣完钱不大于之前的值 且 收款账户收到钱后不小于之前的值 且 转账账户余额大于或等于转账金额
        if (from_balance.compareTo(from_balance.subtract(amount)) != -1
                && to_balance.compareTo(to_balance.add(amount)) != 1
                && from.getBalance().compareTo(amount) != -1){
            //执行扣款
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
            //更新数据库
            accountManager.putAccount(from);
            accountManager.putAccount(to);
        }
    }

    /**
     * 执行转账
     */
    public void workReward(String address) throws ApiException {
        //如果转账账户扣完钱不大于之前的值 且 收款账户收到钱后不小于之前的值 且 转账账户余额大于或等于转账金额
        Account account = accountManager.getAccount(address);
        if (account == null){
            account = new Account(address, BigDecimal.ZERO);
        }
        account.setBalance(account.getBalance().add(BigDecimal.valueOf(App.MINE_REWARD)));
        accountManager.putAccount(account);
        //如果是同步区块，就不显示这条log
        if (syncBlockResponseHandler.getCurrentSyncBlock() == null) {
            logger.info("矿工奖励 " + App.MINE_REWARD + " 已存入账户：" + address);
        }
    }

    /**
     * 回滚矿工奖励
     * 这里要根据需求考虑一下是要不要回滚矿工奖励，毕竟人家没有功劳也有苦劳
     */
    public void rollbackReward(BlockHeader blockHeader) throws Exception {
        String address = Cryptor.generateAddress(blockHeader.getPublicKey());
        Account account = accountManager.getAccount(address);
        if (account != null){
            account.setBalance(account.getBalance().subtract(BigDecimal.valueOf(App.MINE_REWARD)));
            logger.info("回收 address："+ address + " 的矿工奖励 " + App.MINE_REWARD );
        }
        accountManager.putAccount(account);
    }
}
