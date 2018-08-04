package com.blockchain.aspect;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by: Yumira.
 * Created on: 2018/7/25-下午4:34.
 * Description:
 */

@Component
@Aspect
public class CommonAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void allPost(){}
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void allGet(){}
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void allRequest(){}

    @Before("allPost() || allGet() || allRequest()")
    public void beforeRequest(){
//        logger.info("before request");
    }

    @After("allPost() || allGet() || allRequest()")
    public void afterRequest(){
//        logger.info("after request");
    }

}
