package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestRobotLoginRequest extends RestUserLoginCreateRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long enterpriseUserId;
	private long enterpriseId;
	public long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public long getEnterpriseUserId() {
		return enterpriseUserId;
	}
	public void setEnterpriseUserId(long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}
	

}
