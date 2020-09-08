package com.huawei.sharedrive.uam.weixin.rest;

public class JsAuthObject {

	private String appId; // 企业应用ID
	
	private long timestamp; // 时间（秒）
	
	private String noncestr; // 随机生存字符串
	
	private String signature; // 签名

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
