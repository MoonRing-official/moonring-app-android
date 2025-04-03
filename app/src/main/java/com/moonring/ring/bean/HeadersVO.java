package com.moonring.ring.bean;


/**
 * author : Administrator
 * <p>
 * time   : 2021/9/1/001
 * desc   :{"cookieId":""}
 */
public class HeadersVO {



    private String cookieId;
    private String device;
    private String version;
    private String cityId;


    private String pid;



    private boolean effect;
    private boolean music;
    private String sig;


    private String address;



    private String xAcceptLanguage;

    public String getSig() {
        return sig;
    }

    public void setSig(String sig) {
        this.sig = sig;
    }

    public String getCookieId() {
        return cookieId;
    }

    public HeadersVO setCookieId(String cookieId) {
        this.cookieId = cookieId;
        return this;
    }

    public String getDevice() {
        return device;
    }

    public HeadersVO setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public HeadersVO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getCityId() {
        return cityId;
    }

    public HeadersVO setCityId(String cityId) {
        this.cityId = cityId;
        return this;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isEffect() {
        return effect;
    }

    public HeadersVO setEffect(boolean effect) {
        this.effect = effect;
        return this;
    }

    public boolean isMusic() {
        return music;
    }

    public HeadersVO setMusic(boolean music) {
        this.music = music;
        return this;
    }



    public String getxAcceptLanguage() {
        return xAcceptLanguage;
    }

    public void setxAcceptLanguage(String xAcceptLanguage) {
        this.xAcceptLanguage = xAcceptLanguage;
    }
}
