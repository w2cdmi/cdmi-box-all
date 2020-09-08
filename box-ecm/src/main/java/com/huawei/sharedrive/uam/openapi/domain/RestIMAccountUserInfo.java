package com.huawei.sharedrive.uam.openapi.domain;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

public class RestIMAccountUserInfo {

	private Long cloudUserId;

	private String email;

	private Long userId;

	private String name;

	private String enterpriseName;

	private String mobile;

	private String departmentName;
	
	private byte accountType;
	
	public static RestIMAccountUserInfo convetToResponseUser(EnterpriseUser user, String enterpriseName, String departmentName) {
		RestIMAccountUserInfo rspUser = new RestIMAccountUserInfo();
		if (user.getCloudUserId() != 0L) {
			rspUser.setCloudUserId(user.getCloudUserId());
		}
		rspUser.setUserId(user.getId());
		rspUser.setEmail(user.getEmail());
		rspUser.setEnterpriseName(enterpriseName);
		rspUser.setName(user.getAlias());
		rspUser.setMobile(user.getMobile());
		rspUser.setDepartmentName(departmentName);
		rspUser.setAccountType(user.getType());
		return rspUser;
	}

	public Long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(Long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public byte getAccountType() {
		return accountType;
	}

	public void setAccountType(byte accountType) {
		this.accountType = accountType;
	}
	
}
