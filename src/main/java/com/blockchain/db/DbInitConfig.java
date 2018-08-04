package com.blockchain.db;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * 配置本地k-v数据库，推荐使用levelDB
 * created by yumira 2018/7/13.
 */
@Configuration
public class DbInitConfig {

    @Bean
    @ConditionalOnProperty("db.levelDB")
    public DB levelDB() throws IOException {
        org.iq80.leveldb.Options options = new org.iq80.leveldb.Options();
        options.createIfMissing(true);
        return Iq80DBFactory.factory.open(new File("./levelDB"), options);
    }

}
