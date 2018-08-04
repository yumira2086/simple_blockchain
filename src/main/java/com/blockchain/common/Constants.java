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
 * ClassName:Constants <br/>
 *
 */
public interface Constants {
	
	int PUBKEY_DIGEST_LENGTH = 90; // public key length
	int PRVKEY_DIGEST_LENGTH = 45; //private key length
	int ADDR_DIGEST_LENGTH = 35;   // address length
	int SIGN_DIGEST_LENGTH = 98;   // signature length
	int KEY_DES3_DIGEST_LENGTH = 24;  // max size of key for DES3 encrypt
	int KEY_AES128_DIGEST_LENGTH = 16; // max size of key for AES128 encrypt
	int TRANSSQL_DIGEST_LENGTH = 8192; // max size of trans sql for TrustSQL
	
	String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
	String RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";
	BigInteger MAXPRIVATEKEY = new BigInteger("00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140", 16);

	String INFO_SHARE_PUBKEY = "BC8s/4qEAvVl4Sv0LwQOWJcVU6Q5hBd+7LlJeEivVmUbdtwP4RTfN8x/G+muMhN8SrweyyVVMIcIrnMWoFqGfIA=";

	/**
	 * 最后一个区块hash的key，value就是最后一个区块的hash
	 */
	String KEY_LAST_BLOCK = "key_last_block";
    /**
     * 第一个区块hash的key，value就是第一个区块的hash
     */
    String KEY_FIRST_BLOCK = "key_first_block";
	/**
	 * 区块hash与区块本身的key value映射，key的前缀，如{key_block_xxxxxxx -> blockJson}
	 */
	String KEY_BLOCK_HASH_PREFIX = "key_block_";

	String KEY_REQUEST_PREFIX = "key_request_";
    /**
     * 保存区块的hash和下一区块hash，key为hash，value为下一区块hash，方便获取某一个block的下一个Block
     */
	String KEY_BLOCK_NEXT_PREFIX = "key_next_";
	/**
	 * 每个表的权限存储key
	 */
	String KEY_PERMISSION = "key_permission_";
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
	 * 区块数据存储 hash 桶前缀
	 */
	String BLOCKS_BUCKET_PREFIX = "block_";
	/**
	 * 钱包数据存储 hash 桶前缀
	 */
	String WALLETS_BUCKET_PREFIX = "wallet_";
	/**
	 * 本地节点公钥，也就是挖到矿转账的地址
	 */
	String PUBLICKEY = "publickey";
	/**
	 * 最后一个区块的区块高度
	 */
	String LAST_BLOCK_INDEX = BLOCKS_BUCKET_PREFIX+"last_block";

	/**
	 * 客户端节点列表存储 key
	 */
	String CLIENT_NODES_LIST_KEY = "client-node-list";

	/**
	 * 服务器分组名
	 */
	String GROUP_NAME = "block_group";

	/**
	 * 心跳超时时间
	 */
	int TIMEOUT = 5000;

	String CHARSET = "utf-8";
}

