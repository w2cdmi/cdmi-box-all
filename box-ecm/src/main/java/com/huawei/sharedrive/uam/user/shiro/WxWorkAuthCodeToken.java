package com.huawei.sharedrive.uam.user.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class WxWorkAuthCodeToken extends UsernamePasswordToken {
    private String authCode;

    private String deviceType;

    private String deviceAddress;

    private String deviceAgent;

    private String deviceArea;

    private String deviceOS;

    private String sessionWebId;
    
    public WxWorkAuthCodeToken() {
        super();
    }

    public WxWorkAuthCodeToken(String code) {
        super("", new char[0]);
        this.authCode = code;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getDeviceAgent() {
        return deviceAgent;
    }

    public String getDeviceArea() {
        return deviceArea;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public String getSessionWebId() {
        return sessionWebId;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setDeviceAgent(String deviceAgent) {
        this.deviceAgent = deviceAgent;
    }

    public void setDeviceArea(String deviceArea) {
        this.deviceArea = deviceArea;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public void setSessionWebId(String sessionWebId) {
        this.sessionWebId = sessionWebId;
    }
}
