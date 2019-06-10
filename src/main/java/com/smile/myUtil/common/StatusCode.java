package com.smile.myUtil.common;

public enum StatusCode {

    SUCCESS(1, "SUCCESS"),
    ERROR(0, "ERROR");

    private final int code;
    private final String codeMsg;

    StatusCode(int code, String codeMsg) {
        this.code = code;
        this.codeMsg = codeMsg;
    }

    public int getCode() {
        return code;
    }

    public String getCodeMsg() {
        return codeMsg;
    }
}
