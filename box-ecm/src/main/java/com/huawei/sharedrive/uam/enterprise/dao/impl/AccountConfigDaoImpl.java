package com.huawei.sharedrive.uam.enterprise.dao.impl;

import com.huawei.sharedrive.uam.enterprise.dao.AccountConfigDao;
import org.springframework.stereotype.Repository;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.common.domain.AccountConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings("deprecation")
public class AccountConfigDaoImpl extends CacheableSqlMapClientDAO implements AccountConfigDao {

    @Override
    public void create(AccountConfig accountConfig) {
        sqlMapClientTemplate.insert("AccountConfig.create", accountConfig);
    }

    @Override
    public AccountConfig get(long accountId, String name) {
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setAccountId(accountId);
        accountConfig.setName(name);
        return (AccountConfig) sqlMapClientTemplate.queryForObject("AccountConfig.get", accountConfig);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountConfig> list(long accountId) {
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setAccountId(accountId);
        return sqlMapClientTemplate.queryForList("AccountConfig.list", accountConfig);
    }

    @Override
    public int update(AccountConfig accountConfig) {
        return sqlMapClientTemplate.update("AccountConfig.update", accountConfig);
    }

    @Override
    public List<AccountConfig> listWithPrefix(long accountId, String prefix) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("accountId", accountId);
        map.put("prefix", prefix);

        return sqlMapClientTemplate.queryForList("AccountConfig.listWithPrefix", map);
    }
}
