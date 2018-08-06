package com.blockchain.exception;

import com.blockchain.base.BaseData;
import com.blockchain.common.ResultGenerator;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;


/**
 * 全局异常捕获处理类
 */
@ControllerAdvice
@Order(2)
public class AppExceptionHandler {

    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseData handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        logger.error("ERROR ======> {}", e);
        return ResultGenerator.genFailResult(e.getMessage());
    }

    @ExceptionHandler(value = ApiException.class)
    @ResponseBody
    public BaseData handleApiException(ApiException e) {

        logger.error("ERROR ======> {}", e);
        return ResultGenerator.genFailResult(e.getCode(),e.getMessage());
    }

}
