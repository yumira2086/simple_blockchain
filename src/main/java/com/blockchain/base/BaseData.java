package com.blockchain.base;


import com.blockchain.common.ResultCode;

public class BaseData<T> {
    private int code;
    private String message;
    private T data;

    @Override
    public String toString() {
        return "BaseData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public BaseData setCode(ResultCode resultCode) {
        this.code = resultCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public BaseData setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseData setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseData setData(T data) {
        this.data = data;
        return this;
    }
}
