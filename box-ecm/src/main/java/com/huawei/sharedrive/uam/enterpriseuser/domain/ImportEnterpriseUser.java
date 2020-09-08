package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;

public class ImportEnterpriseUser implements Serializable
{
    //导入字段
    private String name;
    private String alias;
    private String staffNo;
    private String email;
    private String mobile;
    private String description;
    private String appId;
    private Long spaceQuota;
    private Integer maxVersions;
    private Integer teamSpaceMaxNum;
    private String departmentName;

    //中间的转换字段
    private long enterpriseId;
    private long departmentId;

    //导入过程中的控制字段
    private boolean parseSuccess;
    private String errorCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getSpaceQuota() {
        return spaceQuota;
    }

    public void setSpaceQuota(Long spaceQuota) {
        this.spaceQuota = spaceQuota;
    }

    public Integer getMaxVersions() {
        return maxVersions;
    }

    public void setMaxVersions(Integer maxVersions) {
        this.maxVersions = maxVersions;
    }

    public Integer getTeamSpaceMaxNum() {
        return teamSpaceMaxNum;
    }

    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum) {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
