package com.huawei.sharedrive.uam.openapi.domain.user;

import java.io.Serializable;
import java.util.List;

/**
 * 更新账户配额请求
 */
public class RestUpdateUserSpaceQuotaRequest implements Serializable {
    private long accountId;
    private List<Long> userIdList;
    private long spaceQuota;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

    public long getSpaceQuota() {
        return spaceQuota;
    }

    public void setSpaceQuota(long spaceQuota) {
        this.spaceQuota = spaceQuota;
    }
}
