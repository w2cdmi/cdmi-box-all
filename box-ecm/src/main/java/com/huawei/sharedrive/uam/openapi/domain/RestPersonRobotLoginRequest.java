package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestPersonRobotLoginRequest extends RestUserLoginCreateRequest implements Serializable{
	
	private String wxUnionId;
	
	private String appId;

	public String getWxUnionId() {
		return wxUnionId;
	}

	public void setWxUnionId(String wxUnionId) {
		this.wxUnionId = wxUnionId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	

}
