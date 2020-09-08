package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class WxWorkUserLoginRequest implements Serializable {
    private String appId;

    private String corpId;

    private String code;

    private String authCode;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
