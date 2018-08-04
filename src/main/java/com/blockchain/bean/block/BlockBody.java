package com.blockchain.bean.block;

import com.blockchain.bean.transaction.Transaction;

import java.util.List;

/**
 * 区块body，里面存放交易的数据以及新增的账户地址
 * created by yumira 2018/6/28.
 */
public class BlockBody {

    private List<Transaction> transactions;

    private List<String> addresses;

    @Override
    public String toString() {
        return "BlockBody{" +
                "instructions=" + transactions +
                "addresses=" + addresses +
                '}';
    }

    public BlockBody() {
    }

    public BlockBody(List<Transaction> transactions,List<String> addresses) {
        this.transactions = transactions;
        this.addresses = addresses;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> instructions) {
        this.transactions = instructions;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }
}
