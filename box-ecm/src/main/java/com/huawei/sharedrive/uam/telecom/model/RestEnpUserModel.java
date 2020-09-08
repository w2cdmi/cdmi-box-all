package com.huawei.sharedrive.uam.telecom.model;

public class RestEnpUserModel {
	private String name;
	private String aliasName;
	private String EnterpriseDomainName;
	private String contactEmail;
	private String productId;
	private byte isCloudAppUser;
	private String accountIds;
	private Long authServerId;
	private Long enterpriseId;
	private String phoneNo;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

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

	public String getEnterpriseDomainName() {
		return EnterpriseDomainName;
	}

	public void setEnterpriseDomainName(String enterpriseDomainName) {
		EnterpriseDomainName = enterpriseDomainName;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getAccountIds() {
		return accountIds;
	}

	public void setAccountIds(String accountIds) {
		this.accountIds = accountIds;
	}

	public Long getAuthServerId() {
		return authServerId;
	}

	public void setAuthServerId(Long authServerId) {
		this.authServerId = authServerId;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

}
