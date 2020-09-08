package com.huawei.sharedrive.uam.organization.domain;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

/**
 * Created by Rox on 2017/7/5.
 * 部门信息
 */
public class DeptInfo {
    long enterpriseId;
    long departmentId;
    String departmentName;
    DepartmentAccount departmentAccount;
    String imGroup;

    EnterpriseUser manager;
    EnterpriseUser archiveOwner;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public DepartmentAccount getDepartmentAccount() {
        return departmentAccount;
    }

    public void setDepartmentAccount(DepartmentAccount departmentAccount) {
        this.departmentAccount = departmentAccount;
    }

    public String getImGroup() {
        return imGroup;
    }

    public void setImGroup(String imGroup) {
        this.imGroup = imGroup;
    }

    public EnterpriseUser getManager() {
        return manager;
    }

    public void setManager(EnterpriseUser manager) {
        this.manager = manager;
    }

    public EnterpriseUser getArchiveOwner() {
        return archiveOwner;
    }

    public void setArchiveOwner(EnterpriseUser archiveOwner) {
        this.archiveOwner = archiveOwner;
    }
}
