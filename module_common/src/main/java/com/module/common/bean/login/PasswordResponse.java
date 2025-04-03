package com.module.common.bean.login;

/**
 * author : Administrator
 * <p>
 * time   : 2024/3/12/012
 * desc   :
 */
public class PasswordResponse {



    private String phone;
    private String token;
    private int code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
