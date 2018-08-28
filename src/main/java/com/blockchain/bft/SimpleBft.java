package com.blockchain.bft;

import com.blockchain.core.net.Connecter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by: Yumira.
 * Created on: 2018/7/31-下午1:36.
 * Description: 拜占庭容错条件,用来校验所有在线节点agree事件
 */
public abstract class SimpleBft<T> implements BFT<T,T> {

    private Map<T,Integer> bftMap;

    public static final String KEY = "key";

    public Logger logger = LoggerFactory.getLogger(this.getClass());

    public SimpleBft(Map<T, Integer> bftMap) {
        this.bftMap = bftMap;
    }

    /**
     * 拜占庭容错的事件接收方法
     */
    public void receiveEvent(T t){
        if (totalSize() < 4){//如果当前连接节点小于4，不满足共识条件，直接返回OK
            onAgreement();
            onEnd();
            return;
        }

        if (bftMap.entrySet().size() == 0) {
//            System.out.println(bftAgreeCount());
            bftMap.put(t,1);
            System.out.println(bftMap.hashCode()+"/1:"+bftMap.get(t));
            return;
        }
        T flag = null;
        int rejectCnt = 0;
        for (Map.Entry<T, Integer> entry : bftMap.entrySet()) {
            rejectCnt = rejectCnt + entry.getValue();
            if (rejectCnt >= bftAgreeCount()){
                onAgreeFail();//如果票数已经达到2f+1，还未达到共识，即本次共识失败
                onEnd();
                return;
            }
            if (equals(entry.getKey(),t)){
                flag = entry.getKey();
            }
        }
        if (flag != null){
            int v = bftMap.get(flag);
            bftMap.put(flag,++v);
            System.out.println(bftMap.hashCode()+"/2:"+bftMap.get(t));
            if (bftMap.get(flag) >= bftAgreeCount()){
                onAgreement();//共识成功
                onEnd();
            }
        }else {
            bftMap.put(flag,1);
            System.out.println(bftMap.hashCode()+"/3:"+bftMap.get(t));
        }
    }

    public void clear(){
        bftMap.clear();
    }

    @Override
    public void onEnd() {
//        System.out.println(bftMap.toString());
    }

    @Override
    public int fSize() {
        return Connecter.fSize();
    }

    @Override
    public int bftAgreeCount() {
        return Connecter.bftAgreeCount();
    }

    @Override
    public int totalSize() {
        return Connecter.currentConnectedSize();
    }
}
