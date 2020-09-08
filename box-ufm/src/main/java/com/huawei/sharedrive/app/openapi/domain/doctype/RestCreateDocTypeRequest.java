package com.huawei.sharedrive.app.openapi.domain.doctype;

public class RestCreateDocTypeRequest {
	private long id;
	private String name;
	private String value;
	private long isDefault;
	private long userId;
	private long appId;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(long isDefault) {
		this.isDefault = isDefault;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	
}
