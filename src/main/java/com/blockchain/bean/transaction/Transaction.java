package com.blockchain.bean.transaction;


import com.blockchain.common.TransactionStatus;

import java.math.BigDecimal;

/**
 * 交易对象
 * created by yumira 2018/6/28.
 * @since 18-4-6
 */
public class Transaction {

	/**
	 * 付款人地址
	 */
	private String from;
	/**
	 * 收款人地址
	 */
	private String to;
	/**
	 * 交易金额
	 */
	private BigDecimal amount;
	/**
	 * 附加数据
	 */
	private String data;
	/**
	 * 付款人公钥
	 */
	private String publicKey;
	/**
	 * 付款人签名
	 */
	private String sign;
	/**
	 * 交易时间戳
	 */
	private Long timestamp;
	/**
	 * 交易 Hash 值
	 */
	private String txHash;
	/**
	 * 交易状态
	 */
	private TransactionStatus status = TransactionStatus.APPENDING;
	/**
	 * 交易错误信息
	 */
	private String errorMessage;


	public Transaction(String from, String to, BigDecimal amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.timestamp = System.currentTimeMillis();
	}

	public Transaction() {
		this.timestamp = System.currentTimeMillis();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTxHash() {
		return txHash;
	}

	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Transaction{" +
				"from='" + from + '\'' +
				", to='" + to + '\'' +
				", publicKey=" + publicKey +
				", amount=" + amount +
				", timestamp=" + timestamp +
				", data='" + data + '\'' +
				'}';
	}
}
