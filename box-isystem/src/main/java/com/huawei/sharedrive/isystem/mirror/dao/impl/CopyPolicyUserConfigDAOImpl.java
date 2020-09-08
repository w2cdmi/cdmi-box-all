package com.huawei.sharedrive.isystem.mirror.dao.impl;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.isystem.mirror.dao.CopyPolicyUserConfigDAO;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicyUserConfig;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Component
public class CopyPolicyUserConfigDAOImpl extends CacheableSqlMapClientDAO implements CopyPolicyUserConfigDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(CopyPolicyUserConfig config)
    {
        sqlMapClientTemplate.insert("CopyPolicyUserConfig.insert", config);
    }

    @SuppressWarnings("deprecation")
    @Override
    public CopyPolicyUserConfig getCopyPolicyUserConfig(int policy, int userType, long userId)
    {
        
        CopyPolicyUserConfig config = new CopyPolicyUserConfig();
        config.setPolicyId(policy);
        config.setUserType(userType);
        config.setUserId(userId);
        
        if (isCacheSupported())
        {
            String key = config.getKey();
            CopyPolicyUserConfig reConfig = (CopyPolicyUserConfig) getCacheClient().getCache(key);
            if (reConfig != null)
            {
                return reConfig;
            }
            reConfig = (CopyPolicyUserConfig) sqlMapClientTemplate.queryForObject("CopyPolicyUserConfig.getByPolicyAndUser",
                config);
            if (reConfig == null)
            {
                return null;
            }
            return reConfig;
        }
        return (CopyPolicyUserConfig) sqlMapClientTemplate.queryForObject("CopyPolicyUserConfig.getByPolicyAndUser",
            config);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int delete(CopyPolicyUserConfig config)
    {
        if (isCacheSupported())
        {
            getCacheClient().deleteCache(config.getKey());
        }
        return sqlMapClientTemplate.delete("CopyPolicyUserConfig.delete", config);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteByPolicy(int policy)
    {
        CopyPolicyUserConfig config = new CopyPolicyUserConfig();
        config.setPolicyId(policy);
        return sqlMapClientTemplate.delete("CopyPolicyUserConfig.deleteByPolicy", config);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteByPolicyAndUserType(int policy, int userType)
    {
        CopyPolicyUserConfig config = new CopyPolicyUserConfig();
        config.setPolicyId(policy);
        config.setUserType(userType);
        
        if (isCacheSupported())
        {
            getCacheClient().deleteCache(config.getKey());
        }
        return sqlMapClientTemplate.delete("CopyPolicyUserConfig.deleteByPolicyAndUserType", config);
    }
    
}
