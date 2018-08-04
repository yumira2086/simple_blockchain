package com.blockchain.common;

import com.blockchain.base.BaseData;

public class ResultGenerator {
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";

    public static BaseData genSuccessResult() {
        return new BaseData()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static BaseData genSuccessResult(Object data){
        return new BaseData()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static BaseData genFailResult(String message) {
        return new BaseData()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static BaseData genFailResult(ResultCode resultCode, String message) {
        return new BaseData()
                .setCode(resultCode)
                .setMessage(message);
    }
}
