/**
 * Project Name:trustsql_sdk
 * File Name:Constants.java
 * Package Name:com.tencent.trustsql.sdk
 * Date:Jul 26, 201711:17:18 AM
 * Copyright (c) 2017, Tencent All Rights Reserved.
 *
*/

package com.blockchain.common;

import java.math.BigInteger;

/**
 * 常量类
 */
public interface Constants {

	/**
	 * 所有账户数据
	 */
	String ALL_ACCOUNT_DATA = "all_account_data";
	/**
	 * LevelDB 交易池
	 */
	String KEY_TRANSACTION_POOL = "key_transaction_pool";
	/**
	 * LevelDB 账户池
	 */
	String KEY_ACCOUNT_POOL = "key_account_pool";
	/**
	 * LevelDB 本地存储的节点
	 */
	String LOCAL_NODES = "local_nodes";
	/**
	 * 钱包数据存储 hash 桶前缀
	 */
	String WALLETS_BUCKET_PREFIX = "wallet_";
	/**
	 * 本地节点公钥，也就是挖到矿转账的地址
	 */
	String PUBLICKEY = "publickey";
	/**
	 * 服务器分组名
	 */
	String GROUP_NAME = "block_group";

	/**
	 * 心跳超时时间
	 */
	int TIMEOUT = 5000;

	String CHARSET = "utf-8";

	String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
	String RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";
	BigInteger MAXPRIVATEKEY = new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);
}

