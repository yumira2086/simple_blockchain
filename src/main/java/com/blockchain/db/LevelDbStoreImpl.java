package com.blockchain.db;

import com.blockchain.common.ApplicationContextProvider;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * levelDB
 *
 * created by yumira 2018/4/20.
 */
@Component
public class LevelDbStoreImpl implements DbStore{

    Logger logger = LoggerFactory.getLogger(LevelDbStoreImpl.class);

    //不知道怎么回事，@Autowired有一定概率取不到，报空指针
    @Autowired
    private DB db;

    @Override
    public void put(String key, String value) {
        checkDb();
        db.put(Iq80DBFactory.bytes(key), Iq80DBFactory.bytes(value));
    }

    @Override
    public String get(String key) {
        checkDb();
        return Iq80DBFactory.asString(db.get(Iq80DBFactory.bytes(key)));
    }

    @Override
    public void remove(String key) {
        checkDb();
        db.delete(Iq80DBFactory.bytes(key));
    }

    public void checkDb(){
        if (db == null) {
            db = ApplicationContextProvider.getBean(DB.class);
        }
    }


    @PreDestroy
    public void close() {
        try {
            db.close();
            logger.info("关闭levelDb通道");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
