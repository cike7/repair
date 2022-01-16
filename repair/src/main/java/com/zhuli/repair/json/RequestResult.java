package com.zhuli.repair.json;


/**
 * @Description 请求结果
 * @Author zhuli
 * @Date 2021/5/6/9:50 PM
 */
public class RequestResult<T> {

    /**
     * 反馈代码
     */
    private int code;
    /**
     * 反馈消息
     */
    private String message;
    /**
     * 反馈结果
     */
    private T data;

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

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
