package com.huawei.sharedrive.app.account.service;

import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.AccountConfigRepository;

import java.util.List;

public interface AccountConfigService {
    void create(AccountConfig accountConfig);

    AccountConfig get(long accountId, String name);

    AccountConfigRepository getConfigRepository(long accountId);

    void update(AccountConfig accountConfig);
}
