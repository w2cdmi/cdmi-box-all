package com.huawei.sharedrive.uam.telecom.model;

public class RestEnterprise {
	private String name;
	private String domainName;
	private String contactEmail;
	private byte isCloudAppUser;

	public byte getIsCloudAppUser() {
		return isCloudAppUser;
	}

	public void setIsCloudAppUser(byte isCloudAppUser) {
		this.isCloudAppUser = isCloudAppUser;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
}
