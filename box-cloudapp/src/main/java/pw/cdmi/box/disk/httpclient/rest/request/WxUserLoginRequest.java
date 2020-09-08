package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class WxUserLoginRequest implements Serializable {
    private String appId;

    private String code;

    private Long enterpriseId;
    
    private byte identity;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

	public byte getIdentity() {
		return identity;
	}

	public void setIdentity(byte identity) {
		this.identity = identity;
	}
    
    
}
