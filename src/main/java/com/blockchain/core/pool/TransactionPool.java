package com.blockchain.core.pool;

import com.blockchain.bean.block.Block;
import com.blockchain.bean.transaction.Transaction;
import com.blockchain.common.Constants;
import com.blockchain.db.DbStore;
import com.blockchain.utils.JsonUtil;
import com.blockchain.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Yumira.
 * Created on: 2018/7/29-上午3:27.
 * Description: 本地交易池
 */
@Component
public class TransactionPool {

	@Autowired
	private DbStore dbStore;

	/**
	 * 添加交易
	 * @param transaction
	 */
	public synchronized boolean addTransaction(Transaction transaction) {
		List<Transaction> transactions = getTransactions();
		boolean exists = false;
		//检验交易是否存在本地，避免重复添加
		for (Transaction tx : transactions) {
			if (tx.getTxHash().equals(transaction.getTxHash())) {
				exists = true;
			}
		}
		if (!exists) {
			transactions.add(transaction);
			//更新本地交易池
			dbStore.put(Constants.KEY_TRANSACTION_POOL, JsonUtil.toJSONString(transactions));
		}
		return !exists;
	}

	/**
	 * 清理新区块与本地交易池交集中的数据
	 */
	public void updateTransactions(Block block){
		List<Transaction> localTxs = getTransactions();
		List<Transaction> blockTxs = block.getBlockBody().getTransactions();
		for (int i = blockTxs.size() - 1; i >= 0; i--) {
			for (int j = localTxs.size() - 1; j >= 0; j--) {
				if (blockTxs.get(i).getTxHash().equals(localTxs.get(j).getTxHash())){
					localTxs.remove(j);
				}
			}
		}
		updateTransactions(localTxs);
	}


	/**
	 * 取出交易池中的数据
	 */
	public List<Transaction> getTransactions() {
		String result = dbStore.get(Constants.KEY_TRANSACTION_POOL);
		if (!StringUtil.isEmpty(result)){
			return JsonUtil.toList(result.toString(),Transaction.class);
		}
		return new ArrayList<>();
	}


	/**
	 * 更新交易池所有数据
	 */
	public void updateTransactions(List<Transaction> transactions){
		dbStore.put(Constants.KEY_TRANSACTION_POOL, JsonUtil.toJSONString(transactions));
	}


	/**
	 * 清空交易池
	 */
	public void clearPool() {
		dbStore.put(Constants.KEY_TRANSACTION_POOL,"");
	}

}
