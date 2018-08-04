package com.blockchain.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ——————尼玛保佑———————
 * --┏┓-----┏┓------
 * ┏┛┻━━━┛┻┓-----
 * ┃　　　━　　　┃-----
 * ┃　┳┛　┗┳　┃-----
 * ┃　　　┻　　　┃-----
 * ┗━┓　　　┏━┛-----
 * ---┃　　　┗━━━┓--
 * ---┃　       　　┣┓
 * ---┃　　　　　　　┏┛
 * ---┗┓┓┏━┳┓┏┛--
 * ----┗┻┛　┗┻┛----
 * ——————Bug退散————————
 * <p>
 * Created by: Yumira.
 * Created on: 2018/7/30-下午6:30.
 * Description:
 */
@Configuration // 声明当前类是一个配置类，相当于Spring配置的XML文件
@EnableAsync// 利用@EnableAsync注解开启异步任务的支持
public class AsyncConfig implements AsyncConfigurer {


    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数
        taskExecutor.setCorePoolSize(5);
        // 最大线程数
        taskExecutor.setMaxPoolSize(50);
        // 队列最大长度
        taskExecutor.setQueueCapacity(1000);
        // 线程池维护线程所允许的空闲时间(单位秒)
        taskExecutor.setKeepAliveSeconds(120);
        // 线程池对拒绝任务(无线程可用)的处理策略 ThreadPoolExecutor.CallerRunsPolicy策略 ,调用者的线程会执行该任务,如果执行器已关闭,则丢弃.
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }



}
