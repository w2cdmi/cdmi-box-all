package com.huawei.sharedrive.app.openapi.domain.user;

import java.io.Serializable;

/**
 * 更新账户配额请求
 */
public class RestUpdateAccountSpaceQuotaRequest implements Serializable {
    private long accountId;
    private long from;
    private long to;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }
}
