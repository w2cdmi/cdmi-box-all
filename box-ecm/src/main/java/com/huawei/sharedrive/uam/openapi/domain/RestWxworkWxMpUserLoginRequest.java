package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class RestWxworkWxMpUserLoginRequest implements Serializable {

	private static final long serialVersionUID = -3922285391765600914L;

	private String code;

	private String corpId;
	
	private String appId;

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

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void checkParameter(HttpServletRequest request) {
		
		corpId = "wwff314b9b8085f16c";	//测试
		appId = "StorBox";	//测试
		
		if (StringUtils.isBlank(code)) {
			throw new InvalidParamterException("invalidate code: null");
		}

		if (StringUtils.isBlank(corpId)) {
			throw new InvalidParamterException("invalidate code: null");
		}
		
		if (StringUtils.isBlank(appId)) {
			throw new InvalidParamterException("invalidate appId: null");
		}
	}

}
