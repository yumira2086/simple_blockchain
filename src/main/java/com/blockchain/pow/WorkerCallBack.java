package com.blockchain.pow;

import com.blockchain.bean.block.Block;
import com.blockchain.crypto.exception.TrustSDKException;
import com.blockchain.exception.ApiException;

/**
 * Created by: Yumira.
 * Created on: 2018/7/17-上午10:12.
 * Description:
 */
public interface WorkerCallBack {
    void onStart(Long startTime);
    void onWorking(Long timeConsuming,Long nonce,String currentHash);
    void onComplete(Block block) throws ApiException, TrustSDKException;
}
