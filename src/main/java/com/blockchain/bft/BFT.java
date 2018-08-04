package com.blockchain.bft;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午1:42.
 * Description:
 */
public interface BFT<T,K> {
    int bftSize();
    int bftLimitCount();
    int totalSize();
    boolean equals(T object1, K object2);
    void onAgreement();
    void onAgreeFail();
}
