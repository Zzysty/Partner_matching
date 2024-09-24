package com.zzy.yupaobackend.common;

/**
 * 错误码
 */
public enum ErrorCode {
    SUCCESS(0, "OK", "成功"),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "用户未登录", ""),
    NO_AUTH(40101, "用户无权限", ""),
    SYSTRM_ERROR(50000, "系统内部异常", "");


    private final int code;
    private final String msg;
    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }
}
