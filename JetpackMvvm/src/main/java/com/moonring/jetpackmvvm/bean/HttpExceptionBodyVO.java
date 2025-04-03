package com.moonring.jetpackmvvm.bean;

/**
 * author : Administrator
 * <p>
 * time   : 2022/9/13/013
 * desc   :HttpException 状态下会有这些信息
 */
public class HttpExceptionBodyVO {

    /**
     * code : 404420
     * msg : user is not bound wallet
     * request_id :
     * status_code : 404
     */

    private int code;
    private String msg;
    private String request_id;
    private int status_code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }
}
