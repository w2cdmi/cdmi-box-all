package com.huawei.sharedrive.uam.organization.domain;

import java.util.Date;

public class EnterpriseUserDept {
	private Long departmentId;
	
	private Long enterpriseId;
	
	private Long enterpriseUserId;

	private Long srcDeptId;
	
	private Integer order;

	private Date createdAt;

	private Date modifiedAt;

	public EnterpriseUserDept() {
	}
	
	public EnterpriseUserDept(long enterpriseUserId, Long enterpriseId, long departmentId) {
		this.enterpriseUserId = enterpriseUserId;
		this.enterpriseId = enterpriseId;
		this.departmentId = departmentId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Long getEnterpriseUserId() {
		return enterpriseUserId;
	}

	public void setEnterpriseUserId(Long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Long getSrcDeptId() {
		return srcDeptId;
	}

	public void setSrcDeptId(Long srcDeptId) {
		this.srcDeptId = srcDeptId;
	}
}
