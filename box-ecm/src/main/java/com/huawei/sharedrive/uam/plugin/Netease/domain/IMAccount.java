package com.huawei.sharedrive.uam.plugin.Netease.domain;

import java.util.Date;

public class IMAccount {
	
	private long accountId;
	
	private String name;
	
	private long cloudUserId;
	
	private String IMaccid;
	
	private String IMPwd;
	
	private Date createdAt;
	
	private long enterpriseId;
	
	private long userId;
	
	private String icon;
	
	
	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public String getIMaccid() {
		return IMaccid;
	}

	public void setIMaccid(String iMaccid) {
		IMaccid = iMaccid;
	}

	public String getIMPwd() {
		return IMPwd;
	}

	public void setIMPwd(String iMPwd) {
		IMPwd = iMPwd;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
}
