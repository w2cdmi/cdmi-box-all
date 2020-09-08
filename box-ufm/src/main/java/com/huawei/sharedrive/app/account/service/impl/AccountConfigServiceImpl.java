package com.huawei.sharedrive.app.account.service.impl;

import com.huawei.sharedrive.app.account.dao.AccountConfigDao;
import com.huawei.sharedrive.app.account.service.AccountConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.AccountConfigRepository;

import java.util.List;

@Service
public class AccountConfigServiceImpl implements AccountConfigService {
    @Autowired
    private AccountConfigDao accountConfigDao;

    @Override
    public void create(AccountConfig accountConfig) {
        accountConfigDao.create(accountConfig);
    }

    @Override
    public AccountConfig get(long accountId, String name) {
        return accountConfigDao.get(accountId, name);
    }

    @Override
    public AccountConfigRepository getConfigRepository(long accountId) {
        return new AccountConfigRepository(accountConfigDao.list(accountId));
    }

    @Override
    public void update(AccountConfig accountConfig) {
        accountConfigDao.update(accountConfig);
    }

}
