package com.blockchain.checker;

import com.blockchain.bean.block.Block;
import com.blockchain.bean.transaction.Transaction;

/**
 * 区块校验
 * created by yumira 2018/7/25.
 */
public interface BlockChecker {
    /**
     * 比较目标区块和自己本地的区块num大小
     * @param block
     * 被比较的区块
     * @return
     * 本地与目标区块的差值
     */
    int checkNum(Block block);

    /**
     * 校验区块分叉
     * @param block
     * @return
     */
    int checkBranch(Block block);
    /**
     * 校验区块内操作的权限是否合法
     * @param block
     * block
     * @return
     * 大于0合法
     */
    int checkPermission(Block block);

    /**
     * 校验hash、内部hash（merkle tree root hash）
     * @param block
     * block
     * @return
     * 大于0合法
     */
    int checkHash(Block block);

    /**
     * 校验前一区块的hash是否匹配
     * @param block
     * @return
     */

    int checkPreHash(Block block);
    /**
     * 校验区块生成时间
     */
    int checkTime(Block block);
    
    /**
     * 校验签名
     * @return block
     */
    int checkSign(Transaction transaction);
    /**
     * 检查账户余额
     */
    int checkPayAccount(Transaction transaction);
    /**
     * 检查账户余额
     */
    int checkBlance(Transaction transaction);
    /**
     * 检查转账金额
     */
    int checkAmount(Transaction transaction);

    /**
     * 校验地址与公钥
     */
    int checkAddress(Transaction transaction);
    /**
     * 校验交易时间的合法性
     */
    int checkTransactionTime(Transaction transaction);
    /**
     * 校验创世区块
     */
    int checkGenesisBlock(Block block);
}
