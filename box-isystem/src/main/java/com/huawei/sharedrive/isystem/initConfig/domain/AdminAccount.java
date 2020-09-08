package com.huawei.sharedrive.isystem.initConfig.domain;

public class AdminAccount {

	private String loginName;

	private String name;

	// 1: isystem super admin
	// 2: isystem admin
	// 3: bms super admin
	// 4: bms admin
	// private byte type;

	private String email;

	private byte isConfigEnterpriseUser;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getIsConfigEnterpriseUser() {
		return isConfigEnterpriseUser;
	}

	public void setIsConfigEnterpriseUser(byte isConfigEnterpriseUser) {
		this.isConfigEnterpriseUser = isConfigEnterpriseUser;
	}

}
