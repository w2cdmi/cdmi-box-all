package com.huawei.sharedrive.uam.uservip.domain;

import java.util.Date;

//个人vip
public class UserVip {
	
	private long cloudUserId;
	
	private long enterpriseUserId;
	
	private long enterpriseAccountId;
	
	private long enterpriseId;
	
	//购买vipId
	private long productId;
	
	//vip开通时间
	private Date startDate;
	
	//vip到期时间
	private Date expireDate;
	
	//更新时间
	private Date updateDate;

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public long getEnterpriseUserId() {
		return enterpriseUserId;
	}

	public void setEnterpriseUserId(long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public long getEnterpriseAccountId() {
		return enterpriseAccountId;
	}

	public void setEnterpriseAccountId(long enterpriseAccountId) {
		this.enterpriseAccountId = enterpriseAccountId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
