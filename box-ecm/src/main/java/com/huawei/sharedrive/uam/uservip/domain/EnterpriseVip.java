package com.huawei.sharedrive.uam.uservip.domain;

import java.util.Date;
//企业vip
public class EnterpriseVip {
	
	private	long enterpriseAccountId;
	
	private long enterpriseId;
	
	//购买vipId
	private long productId;
	
	//vip开通时间
	private Date startDate;
	
	//vip到期时间
	private Date expireDate;
	

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public long getEnterpriseAccountId() {
		return enterpriseAccountId;
	}

	public void setEnterpriseAccountId(long enterpriseAccountId) {
		this.enterpriseAccountId = enterpriseAccountId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}
	
	
	
	

}
