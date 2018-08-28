package com.blockchain.core.pool;

import com.blockchain.bean.block.Account;
import com.blockchain.bean.block.Block;
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
 * Description: 本地的账户池
 */
@Component
public class AccountPool {

	@Autowired
	private DbStore dbStore;

	/**
	 * 添加账户
	 */
	public synchronized boolean putAccount(Account account) {
		List<String> accounts = getAccounts();
		boolean exists = false;
		//检验账户是否存在本地，避免重复添加
		for (String address : accounts) {
			if (account.getAddress().equals(address)) {
				exists = true;
			}
		}
		if (!exists) {
			accounts.add(account.getAddress());
			//更新本地账户池
			dbStore.put(Constants.KEY_ACCOUNT_POOL, JsonUtil.toJSONString(accounts));
		}
		return !exists;
	}

	/**
	 * 清理新区块与本地账户池交集中的数据
	 */
	public void updateAccounts(Block block){
		List<String> localAddr = getAccounts();
		List<String> blockAddr = block.getBlockBody().getAddresses();
		for (int i = blockAddr.size() - 1; i >= 0; i--) {
			for (int j = localAddr.size() - 1; j >= 0; j--) {
				if (blockAddr.get(i).equals(localAddr.get(j))){
					localAddr.remove(j);
				}
			}
		}
		updateAccounts(localAddr);
	}


	/**
	 * 取出账户池中的数据
	 */
	public List<String> getAccounts() {
		String result = dbStore.get(Constants.KEY_ACCOUNT_POOL);
		if (!StringUtil.isEmpty(result)){
			return JsonUtil.toList(result,String.class);
		}
		return new ArrayList<>();
	}


	/**
	 * 更新账户池所有数据
	 */
	public void updateAccounts(List<String> addresses){
		dbStore.put(Constants.KEY_ACCOUNT_POOL, JsonUtil.toJSONString(addresses));
	}


	/**
	 * 清空账户池
	 */
	public void clearPool() {
		dbStore.put(Constants.KEY_ACCOUNT_POOL,"");
	}

}
