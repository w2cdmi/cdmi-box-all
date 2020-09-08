package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class RestWxUserLoginRequest implements Serializable {
    private String appId;

    private String code;

    private Long enterpriseId;
    
    private byte identity;

    public void checkParameter(HttpServletRequest request) {
        if (StringUtils.isBlank(code)) {
            String msg = "code is null.";
            throw new InvalidParamterException(msg);
        }

        if (StringUtils.isBlank(appId)) {
            String msg = "appId is null.";
            throw new InvalidParamterException(msg);
        }

//        TerminalServiceImpl.checkDeviceParamter(request);
    }

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
