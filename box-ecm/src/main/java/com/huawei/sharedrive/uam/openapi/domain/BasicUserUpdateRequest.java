package com.huawei.sharedrive.uam.openapi.domain;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

public class BasicUserUpdateRequest {
	private String description;

	private String email;

	private Integer maxVersions;

	private String name;

	private String newPassword;

	private String oldPassword;

	private Integer regionId;

	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDescription() {
		return description;
	}

	public String getEmail() {
		return email;
	}

	public Integer getMaxVersions() {
		return maxVersions;
	}

	public String getName() {
		return name;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setMaxVersions(Integer maxVersions) {
		this.maxVersions = maxVersions;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public static void copyBasicToEnterpriseUser(EnterpriseUser enterpriseUser,
			BasicUserUpdateRequest restUserUpdateRequest) {
		enterpriseUser.setPassword(restUserUpdateRequest.getNewPassword());
		enterpriseUser.setEmail(restUserUpdateRequest.getEmail());
		enterpriseUser.setAlias(restUserUpdateRequest.getName());
		enterpriseUser.setDescription(restUserUpdateRequest.getDescription());
		enterpriseUser.setMobile(restUserUpdateRequest.getMobile());
	}

}
