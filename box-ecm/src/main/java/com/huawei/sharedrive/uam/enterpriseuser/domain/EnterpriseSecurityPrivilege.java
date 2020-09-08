package com.huawei.sharedrive.uam.enterpriseuser.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class EnterpriseSecurityPrivilege implements Serializable
{
    public static final byte ROLE_SECURITY_MANAGER = 1;
    public static final byte ROLE_ARCHIVE_MANAGER = 2;
    public static final byte ROLE_DEPT_DIRECTOR = 3;

    public static final long COMPANY = 0;
    private long id;
    
    @NotNull
    private long enterpriseId;

    @NotNull
    private long departmentId;

    private String deptName;
    
    private String userName;
    
    private String loginName;
    
    private String phone;
    
    @NotNull
    private byte role;

    @NotNull
    private long enterpriseUserId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public byte getRole() {
        return role;
    }

    public void setRole(byte role) {
        this.role = role;
    }

    public long getEnterpriseUserId() {
        return enterpriseUserId;
    }

    public void setEnterpriseUserId(long enterpriseUserId) {
        this.enterpriseUserId = enterpriseUserId;
    }

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
    
    
}
