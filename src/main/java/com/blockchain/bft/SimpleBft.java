package com.blockchain.bft;

import com.blockchain.common.App;
import com.blockchain.core.net.Connecter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午1:36.
 * Description: 拜占庭容错条件,用来校验所有在线节点agree事件
 */
public abstract class SimpleBft<T> implements BFT<T,T> {

    private Map<T,Integer> bftMap = new ConcurrentHashMap<>();

    /**
     * 拜占庭容错的事件接受方法
     */
    public void receiveEvent(T t){
        if (totalSize() < 4){//如果当前连接节点小于4，不满足共识条件，直接返回OK
            onAgreement();
            return;
        }
        if (bftMap.entrySet().size() == 0) {
            bftMap.put(t,1);
            return;
        }
        T flag = null;
        int rejectCnt = 0;
        for (Map.Entry<T, Integer> entry : bftMap.entrySet()) {
            rejectCnt = rejectCnt + entry.getValue();
            if (rejectCnt >= bftLimitCount()){
                onAgreeFail();//如果票数已经达到2f+1，还未达到共识，即本次共识失败
                return;
            }
            if (equals(entry.getKey(),t)){
                flag = entry.getKey();
            }
        }
        if (flag != null){
            int v = bftMap.get(flag);
            bftMap.put(flag,++v);
            if (bftMap.get(flag) >= bftLimitCount()){
                onAgreement();//共识成功
            }
        }else {
            bftMap.put(flag,1);
        }
    }

    @Override
    public int bftSize() {
        return Connecter.pbftSize();
    }

    @Override
    public int bftLimitCount() {
        return Connecter.pbftAgreeCount();
    }

    @Override
    public int totalSize() {
        return Connecter.currentConnectedSize();
    }
}
