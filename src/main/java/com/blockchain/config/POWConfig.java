package com.blockchain.config;

import com.blockchain.pow.ProofOfWork;
import com.blockchain.pow.WorkerCallBack;
import com.blockchain.pow.WorkingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by: Yumira.
 * Created on: 2018/7/30-下午7:13.
 * Description: 矿工初始化配置类
 */
@Configuration
public class POWConfig {

    @Autowired
    private WorkingListener workingListener;

    @Bean
    public ProofOfWork getWork() {
        ProofOfWork proofOfWork = new ProofOfWork();
        proofOfWork.setWorkingListener(workingListener);
        return proofOfWork;
    }

}
