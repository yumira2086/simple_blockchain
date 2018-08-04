package com.blockchain.common;

/**
 * 交易状态
 */
public enum TransactionStatus {

	SUCCESS("success", 1),
	APPENDING("appending", 0),
	FAIL("fail", -1);

	private String key;
	private int value;

	TransactionStatus(String key, int value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
