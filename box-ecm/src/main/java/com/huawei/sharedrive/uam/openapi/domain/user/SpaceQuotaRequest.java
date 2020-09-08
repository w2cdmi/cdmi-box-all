package com.huawei.sharedrive.uam.openapi.domain.user;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class SpaceQuotaRequest {
    @ApiModelProperty(value = "企业账户ID", required = true)
    private long accountId;

    @ApiModelProperty(value = "修改员工enterpriseUser列表", required = true)
    private List<Long> enterpriseUserIds;

    @ApiModelProperty(value = "分配空间大小", required = true)
    private long quota;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public List<Long> getEnterpriseUserIds() {
        return enterpriseUserIds;
    }

    public void setEnterpriseUserIds(List<Long> enterpriseUserIds) {
        this.enterpriseUserIds = enterpriseUserIds;
    }

    public long getQuota() {
        return quota;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }
}