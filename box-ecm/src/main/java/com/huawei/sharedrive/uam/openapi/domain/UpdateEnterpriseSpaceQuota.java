package com.huawei.sharedrive.uam.openapi.domain;

import io.swagger.annotations.ApiModelProperty;

public class UpdateEnterpriseSpaceQuota {
    @ApiModelProperty(value = "企业账号ID", readOnly = true)
    private long accountId;

    @ApiModelProperty(value = "默认个人空间配额")
    private long defaultAccountSpaceQuota;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getDefaultAccountSpaceQuota() {
        return defaultAccountSpaceQuota;
    }

    public void setDefaultAccountSpaceQuota(long defaultAccountSpaceQuota) {
        this.defaultAccountSpaceQuota = defaultAccountSpaceQuota;
    }
}
