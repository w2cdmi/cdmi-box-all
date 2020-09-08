package com.huawei.sharedrive.app.user.domain;

import javax.validation.constraints.NotNull;

public class EnterpriseSecurityPrivilege {

    public static final byte ROLE_SECURITY_MANAGER = 1;
    public static final byte ROLE_ARCHIVE_MANAGER = 2;
    public static final byte ROLE_DEPT_DIRECTOR = 3;

    public static final long COMPANY = 0;
    private long id;

    @NotNull
    private long enterpriseId;

    @NotNull
    private long departmentId;

    @NotNull
    private byte role = 2;

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
}
