package com.huawei.sharedrive.uam.enterprise.service.impl;

import com.huawei.sharedrive.uam.enterprise.dao.AccountConfigDao;
import com.huawei.sharedrive.uam.enterprise.service.AccountConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.AccountConfig;

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
    public List<AccountConfig> list(long accountId) {
        return accountConfigDao.list(accountId);
    }

    @Override
    public void update(AccountConfig accountConfig) {
        accountConfigDao.update(accountConfig);
    }

    @Override
    public void save(AccountConfig config) {
        AccountConfig db = get(config.getAccountId(), config.getName());
        if(db != null) {
            update(config);
        } else {
            create(config);
        }
    }
}
