package com.huawei.sharedrive.uam.weixin.rest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/************************************************************
 * @author 7367
 * @version 3.0.1
 * @Description: <pre>企业微信小程序用户类</pre>
 ************************************************************/
public class WxworkWxMpUserInfo extends WxApiResponse implements Serializable {


	private static final long serialVersionUID = 436085667230568912L;

	@JsonProperty("userid")
    String userId;

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
