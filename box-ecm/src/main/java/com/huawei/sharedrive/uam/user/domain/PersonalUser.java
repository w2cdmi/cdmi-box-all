package com.huawei.sharedrive.uam.user.domain;

import java.util.Date;

public class PersonalUser {
	
	private long id;
	
	private String name;
	
	private String wxUnionId;
	
	private String password;
	
	private String qywxUnionIdId;
	
	private String mobile;
	
	private String email;
	
	private byte type;
	
	private Date createTime;
	
	private long quota;
	
	private byte usedWxrobot;
	
	private Date expirationTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWxUnionId() {
		return wxUnionId;
	}

	public void setWxUnionId(String wxUnionId) {
		this.wxUnionId = wxUnionId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQywxUnionIdId() {
		return qywxUnionIdId;
	}

	public void setQywxUnionIdId(String qywxUnionIdId) {
		this.qywxUnionIdId = qywxUnionIdId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getQuota() {
		return quota;
	}

	public void setQuota(long quota) {
		this.quota = quota;
	}

	public byte getUsedWxrobot() {
		return usedWxrobot;
	}

	public void setUsedWxrobot(byte usedWxrobot) {
		this.usedWxrobot = usedWxrobot;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	
	
	
	

}
