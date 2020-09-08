package com.huawei.sharedrive.app.account.dao.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.account.dao.AccountAccessKeyDao;
import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
public class AccountAccessKeyDaoImpl extends CacheableSqlMapClientDAO implements AccountAccessKeyDao
{
    private static Logger logger = LoggerFactory.getLogger(AccountAccessKeyDaoImpl.class);
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(AccountAccessKey accessKey)
    {
        if (accessKey != null)
        {
            Map<String, String> encodedSecKey = EDToolsEnhance.encode(accessKey.getSecretKey());
            accessKey.setSecretKey(encodedSecKey.get(EDToolsEnhance.ENCRYPT_CONTENT));
            accessKey.setSecretKeyEncodeKey(encodedSecKey.get(EDToolsEnhance.ENCRYPT_KEY));
            logger.info("set crypt in ufm.AccountAccessKey");
        }
        sqlMapClientTemplate.insert("AccountAccessKey.insert", accessKey);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public AccountAccessKey getById(String id)
    {
        AccountAccessKey accountAccessKey = null;
        String key = AccountAccessKey.CACHE_KEY_PREFIX_ID + id;
        if (isCacheSupported())
        {
            accountAccessKey = (AccountAccessKey) getCacheClient().getCache(key);
            if (null != accountAccessKey)
            {
                return accountAccessKey;
            }
            
        }
        accountAccessKey = (AccountAccessKey) sqlMapClientTemplate.queryForObject("AccountAccessKey.getById",
            id);
        if (null != accountAccessKey)
        {
            accountAccessKey.setSecretKey(EDToolsEnhance.decode(accountAccessKey.getSecretKey(),
                accountAccessKey.getSecretKeyEncodeKey()));
            if (isCacheSupported())
            {
                getCacheClient().setCache(key, accountAccessKey);
            }
        }
        return accountAccessKey;
    }
    
}
