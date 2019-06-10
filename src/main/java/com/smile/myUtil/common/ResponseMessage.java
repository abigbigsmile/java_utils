package com.smile.myUtil.common;

public class ResponseMessage<T> {

    private String message;
    private int status;
    private T data;

    public ResponseMessage(int status) {
        this.status = status;
    }

    public ResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public ResponseMessage(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseMessage(String message, int status, T data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public static ResponseMessage createSuccess(){
        return new ResponseMessage(StatusCode.SUCCESS.getCode());
    }

    public static ResponseMessage createSuccessMessage(String message){
        return new ResponseMessage(StatusCode.SUCCESS.getCode(), message);
    }

    public static <T> ResponseMessage<T> createSuccess(T data){
        return new ResponseMessage(StatusCode.SUCCESS.getCode(), data);
    }

    public static <T> ResponseMessage<T> createSuccess(String message, T data){
        return new ResponseMessage(message, StatusCode.SUCCESS.getCode(), data);
    }

    public static ResponseMessage createError(){
        return new ResponseMessage(StatusCode.ERROR.getCode());
    }

    public static ResponseMessage createErrorMessage(String message){
        return new ResponseMessage(StatusCode.ERROR.getCode(), message);
    }

    public static <T> ResponseMessage<T> createError(T data){
        return new ResponseMessage(StatusCode.ERROR.getCode(), data);
    }

    public static <T> ResponseMessage<T> createError(String message, T data){
        return new ResponseMessage(message, StatusCode.ERROR.getCode(), data);
    }



}
