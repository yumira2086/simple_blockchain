package com.blockchain.bean.block;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 钱包账户
 */
public class Account implements Serializable{

	/**
	 * 钱包地址
	 */
	protected String address;

	/**
	 * 账户余额
	 */
	protected BigDecimal balance;

	public Account() {}

	public Account(String address, BigDecimal balance) {
		this.address = address;
		this.balance = balance;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "Account{" +
				"address='" + address + '\'' +
				", balance=" + balance +
				'}';
	}


	/**
	 * 这里要重写equals方法
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Account){
			return address.equals(((Account) obj).getAddress());
		}else {
			return super.equals(obj);
		}
	}

}
