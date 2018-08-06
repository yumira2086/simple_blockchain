package com.blockchain.core.controller;

import com.blockchain.base.BaseData;
import com.blockchain.common.ResultGenerator;
import com.blockchain.bean.block.Account;
import com.blockchain.bean.block.AccountResponse;
import com.blockchain.bean.block.PairKey;
import com.blockchain.core.pool.AccountPool;
import com.blockchain.exception.ApiException;
import com.blockchain.manmger.AccountManager;
import com.blockchain.crypto.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountManager accountManager;
	@Autowired
	private AccountPool accountPool;

	/**
	 * 创建账户
	 * @return
	 */
	@PostMapping("/new")
	public BaseData newAccount() throws Exception {
		//生成椭圆曲线密钥对
		PairKey pairKey = Cryptor.generatePairKey();
		Account account = accountManager.newAccount(pairKey.getPublicKey());
		return ResultGenerator.genSuccessResult(new AccountResponse(pairKey.getKeyWords(),pairKey.getPrivateKey(),pairKey.getPublicKey(),account.getAddress()));
	}

	@PostMapping()
	public BaseData getAccount(@RequestParam("address") String address){
	    Account account = accountManager.getAccount(address);
	    return ResultGenerator.genSuccessResult(account);
	}


	/**
	 * 获取当前所有账户数据  仅供测试环境使用
	 */
	@PostMapping("/all")
	public BaseData getAll() {
		return ResultGenerator.genSuccessResult(accountManager.getAllAccount());
	}


	/**
	 * 查看本地账户池中的数据
	 */
	@PostMapping("/pool")
	public BaseData getPool() throws ApiException {
		return ResultGenerator.genSuccessResult(accountPool.getAccounts());
	}

	/**
	 * 清空本地账户池中的新账户
	 */
	@PostMapping("/pool/clear")
	public BaseData clearPool() throws ApiException {
		accountPool.clearPool();
		return ResultGenerator.genSuccessResult();
	}

}
