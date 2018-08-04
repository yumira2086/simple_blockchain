package com.blockchain.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * created by yumira 2018/7/27.
 */
@Order(1)
@Component
public class App {
    /**
     * 节点的唯一标志
     */
    @Value("${appId}")
    public String appId;
    /**
     * 该客户的唯一标志
     */
    @Value("${name}")
    public String name;
    /**
     * 本地节点ip
     */
    @Value("${localIP}")
    private String localIp;
    /**
     * 本地节点端口
     */
    @Value("${localPort}")
    private int localPort;
    /**
     * 远端服务地址
     */
    @Value("${managerUrl}")
    private String url;
    /**
     * 请求超时时间
     */
    @Value("${httpTimeOut}")
    private int httpTimeOut;
    /**
     * 连接超时时间
     */
    @Value("${connectTimeout}")
    private int connectTimeout;
    /**
     * 难度目标位
     */
    @Value("${targetBits}")
    private int targetBits;
    /**
     * 难度目标位
     */
    @Value("${publicKey}")
    private String publicKey;
    /**
     * 当前版本号
     */
    @Value("${version}")
    private int version;
    /**
     * 单节点测试环境
     */
    @Value("${mineReward}")
    private double mineReward;
    /**
     * 单节点测试环境
     */
    @Value("${singleNode}")
    private Boolean singleNode;
    /**
     * 挖到矿之后是否自动继续挖矿
     */
    @Value("${autoWork}")
    private boolean autoWork;
    /**
     * 同步完成标识,未完成不允许挖矿
     */
    public static boolean isSyncComplete = false;

    /**
     * 本地字典，生成中文私钥
     */
    public static List<String> WORD_LIST;

    public static String VALUE;
    public static String NAME;
    public static String LOCAL_IP;
    public static int LOCAL_PORT;
    public static String MANAGER_URL;
    public static int HTTP_TIMEOUT;
    public static int CONNECT_TIMEOUT;
    public static int TARGET_BITS;
    public static double MINE_REWARD;
    public static int VERSION;
    public static boolean IS_SINGLE_NODE;
    public static String PUBLIC_KEY;
    public static boolean ALLOW_MINE;
    public static boolean AUTO_WORK;


    @PostConstruct
    public void init() {
        VALUE = appId;
        NAME = name;
        LOCAL_IP = localIp;
        LOCAL_PORT = localPort;
        MANAGER_URL = url;
        HTTP_TIMEOUT = httpTimeOut;
        CONNECT_TIMEOUT = connectTimeout;
        TARGET_BITS = targetBits;
        VERSION = version;
        IS_SINGLE_NODE = singleNode;
        MINE_REWARD = mineReward;
        PUBLIC_KEY = publicKey;
        AUTO_WORK = autoWork;
        ALLOW_MINE = true;
    }

}
