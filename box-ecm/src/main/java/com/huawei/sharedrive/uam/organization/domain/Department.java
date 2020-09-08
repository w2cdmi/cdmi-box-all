package com.huawei.sharedrive.uam.organization.domain;

import java.io.Serializable;
import java.util.Date;

public class Department implements Serializable {
	private static final long serialVersionUID = -6520281177878024379L;

	public final static byte STATE_ENABLE = 0;
    public final static byte STATE_NOT_SYNC = -1;//未授权
	public final static byte STATE_DISMISS = -2; //部门标记为解散
	public final static byte STATE_HAND_OVER = -3; //部门移交完成
	public final static byte STATE_DELETE = -4; //部门待删除

	private Long departmentId;
	private Long enterpriseId;
	private Long parentId;
	private Integer order;
	private String domain;
	private String name;
	private Byte state;
	private Date createdAt;
	private Date modifiedAt;

	public Department() {
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
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
}
