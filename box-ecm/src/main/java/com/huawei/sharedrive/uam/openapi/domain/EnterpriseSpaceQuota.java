package com.huawei.sharedrive.uam.openapi.domain;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class EnterpriseSpaceQuota implements Serializable {
    @ApiModelProperty(value = "企业ID", readOnly = true)
    private long enterpriseId;

    @ApiModelProperty(value = "企业账号ID", readOnly = true)
    private long accountId;

    //套餐规格（套餐名称）
    @ApiModelProperty(value = "套餐名称", readOnly = true)
    private String packageName;

    //套餐规格（个人配额）
    @ApiModelProperty(value = "套餐规格（个人配额）", readOnly = true)
    private long packageAccountQuota;

    //套餐规格（账号数量）
    @ApiModelProperty(value = "套餐规格（账号数量）", readOnly = true)
    private long packageAccountNumber;

    //套餐规格（团队空间数量）
    @ApiModelProperty(value = "套餐规格（团队空间数量）", readOnly = true)
    private long packageTeamNumber;

    //套餐规格（团队空间限额）
    @ApiModelProperty(value = "套餐规格（团队空间限额）", readOnly = true)
    private long packageTeamQuota;

    @ApiModelProperty(value = "总空间", readOnly = true)
    private long maxSpace;

    @ApiModelProperty(value = "已用空间", readOnly = true)
    private long usedSpace;

    @ApiModelProperty(value = "共享空间", readOnly = true)
    private long maxShareSpace;

    @ApiModelProperty(value = "已用共享空间", readOnly = true)
    private long usedShareSpace;

    @ApiModelProperty(value = "最大帐号数量", readOnly = true)
    private long maxAccountNumber;

    @ApiModelProperty(value = "已用帐号数量", readOnly = true)
    private long usedAccountNumber;

    @ApiModelProperty(value = "最大团队空间数量", readOnly = true)
    private long maxTeamNumber;

    @ApiModelProperty(value = "已用团队空间数量", readOnly = true)
    private long usedTeamNumber;

    @ApiModelProperty(value = "默认基础个人空间")
    private long defaultAccountSpaceQuota;

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getPackageAccountQuota() {
        return packageAccountQuota;
    }

    public void setPackageAccountQuota(long packageAccountQuota) {
        this.packageAccountQuota = packageAccountQuota;
    }

    public long getPackageAccountNumber() {
        return packageAccountNumber;
    }

    public void setPackageAccountNumber(long packageAccountNumber) {
        this.packageAccountNumber = packageAccountNumber;
    }

    public long getPackageTeamNumber() {
        return packageTeamNumber;
    }

    public void setPackageTeamNumber(long packageTeamNumber) {
        this.packageTeamNumber = packageTeamNumber;
    }

    public long getPackageTeamQuota() {
        return packageTeamQuota;
    }

    public void setPackageTeamQuota(long packageTeamQuota) {
        this.packageTeamQuota = packageTeamQuota;
    }

    public long getMaxSpace() {
        return maxSpace;
    }

    public void setMaxSpace(long maxSpace) {
        this.maxSpace = maxSpace;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public long getMaxShareSpace() {
        return maxShareSpace;
    }

    public void setMaxShareSpace(long maxShareSpace) {
        this.maxShareSpace = maxShareSpace;
    }

    public long getUsedShareSpace() {
        return usedShareSpace;
    }

    public void setUsedShareSpace(long usedShareSpace) {
        this.usedShareSpace = usedShareSpace;
    }

    public long getMaxAccountNumber() {
        return maxAccountNumber;
    }

    public void setMaxAccountNumber(long maxAccountNumber) {
        this.maxAccountNumber = maxAccountNumber;
    }

    public long getUsedAccountNumber() {
        return usedAccountNumber;
    }

    public void setUsedAccountNumber(long usedAccountNumber) {
        this.usedAccountNumber = usedAccountNumber;
    }

    public long getMaxTeamNumber() {
        return maxTeamNumber;
    }

    public void setMaxTeamNumber(long maxTeamNumber) {
        this.maxTeamNumber = maxTeamNumber;
    }

    public long getUsedTeamNumber() {
        return usedTeamNumber;
    }

    public void setUsedTeamNumber(long usedTeamNumber) {
        this.usedTeamNumber = usedTeamNumber;
    }

    public long getDefaultAccountSpaceQuota() {
        return defaultAccountSpaceQuota;
    }

    public void setDefaultAccountSpaceQuota(long defaultAccountSpaceQuota) {
        this.defaultAccountSpaceQuota = defaultAccountSpaceQuota;
    }
}
