package com.blockchain.checker;

/**
 * Created by: Yumira.
 * Created on: 2018/7/25-上午12:46.
 * Description:
 */
public class CheckResult {
    /**
     * OK = 0、ERROR = 1
     */
    private int code;
    /**
     * 详细信息
     */
    private String message;

    public CheckResult() {
    }

    public CheckResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
