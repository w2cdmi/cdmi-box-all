package com.huawei.sharedrive.app.account.dao;

import pw.cdmi.common.domain.AccountConfig;

import java.util.List;

public interface AccountConfigDao {
    void create(AccountConfig accountConfig);

    AccountConfig get(long accountId, String name);

    List<AccountConfig> list(long accountId);

    int update(AccountConfig accountConfig);
}
