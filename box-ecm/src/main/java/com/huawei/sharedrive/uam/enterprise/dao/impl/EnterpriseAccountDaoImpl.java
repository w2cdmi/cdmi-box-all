package com.huawei.sharedrive.uam.enterprise.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterprise.dao.EnterpriseAccountDao;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class EnterpriseAccountDaoImpl extends CacheableSqlMapClientDAO implements EnterpriseAccountDao
{
    @Override
    public EnterpriseAccount getByAccessKeyId(String accessKeyId)
    {
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseAccountAKCacheKey(accessKeyId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccessKeyId",
                accessKeyId);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccessKeyId",
            accessKeyId);
    }
    
    @Override
    public List<EnterpriseAccount> getByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getByEnterpriseId", enterpriseId);
    }
    
    @Override
    public List<EnterpriseAccount> getAppContextByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAppContextByEnterpriseId",
            enterpriseId);
    }
    
    @Override
    public int deleteByAccountId(long accountId)
    {
        EnterpriseAccount enterpriseAccount = getByAccountId(accountId);
        if (null == enterpriseAccount)
        {
            return 0;
        }
        int delNum = sqlMapClientTemplate.delete("EnterpriseAccount.deleteByAccountId", accountId);
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseAccountAKCacheKey(enterpriseAccount.getAccessKeyId()));
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseAccountIdCacheKey(enterpriseAccount.getAccountId()));
        deleteCacheAfterCommit(EnterpriseAccount.getEnterpriseEidAppIdKey(enterpriseAccount.getEnterpriseId(),
            enterpriseAccount.getAuthAppId()));
        return delNum;
    }
    
    @Override
    public void create(EnterpriseAccount enterpriseAccount)
    {
        sqlMapClientTemplate.insert("EnterpriseAccount.insert", enterpriseAccount);
    }
    
    @Override
    public List<String> getAppByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAppByEnterpriseId", enterpriseId);
    }
    
    @Override
    public List<Long> getAccountIdByEnterpriseId(long enterpriseId)
    {
        return sqlMapClientTemplate.queryForList("EnterpriseAccount.getAccountIdByEnterpriseId", enterpriseId);
    }

    @Override
    public EnterpriseAccount getByAccountId(long accountId)
    {
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseAccountIdCacheKey(accountId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccountId",
                accountId);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByAccountId",
            accountId);
    }

    @Override
    public EnterpriseAccount getByEnterpriseApp(long enterpriseId, String authAppId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("authAppId", authAppId);
        if (isCacheSupported())
        {
            String key = EnterpriseAccount.getEnterpriseEidAppIdKey(enterpriseId, authAppId);
            EnterpriseAccount enterpriseAccount = (EnterpriseAccount) getCacheClient().getCache(key);
            if (enterpriseAccount != null)
            {
                return enterpriseAccount;
            }
            enterpriseAccount = (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByEnterpriseApp",
                map);
            if (enterpriseAccount == null)
            {
                return null;
            }
            getCacheClient().setCache(key, enterpriseAccount);
            return enterpriseAccount;
        }
        return (EnterpriseAccount) sqlMapClientTemplate.queryForObject("EnterpriseAccount.getByEnterpriseApp",
            map);
    }
    //新增按企业设置登录密码复杂度
	@Override
	public void setPwdLevelByEnterpriseId(long enterpriseId,int pwdLevel) {
		
	}

	@Override
	public void modifyPwdLevelByEnterpriseId(long enterpriseId, int pwdLevel) {
		/*String pwdLevelBefore = getPwdLevelByEnterpriseId(enterpriseId);
		Map<String,Object> map = new HashMap<String,Object>(2);
		map.put("enterpriseId",enterpriseId);
		map.put("pwdLevel",pwdLevel);//“1”高级，“2”中级，“3”初级
		sqlMapClientTemplate.update("PwdLevel.modify", map);*/
		
	}

	@Override
	public String getPwdLevelByEnterpriseId(long enterpriseId) {
		Enterprise enterprise = (Enterprise) sqlMapClientTemplate.queryForObject("Enterprise.getById",
				enterpriseId);
		String pwdLevel="1";
		if(enterprise != null){
			pwdLevel = enterprise.getPwdLevel();
		}
		return pwdLevel;
	}

	@Override
	public List<EnterpriseAccount> listAll() {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("EnterpriseAccount.listAll");
	}

	@Override
	public void update(EnterpriseAccount enterpriseAccount) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.update("EnterpriseAccount.updateEnterpriseAccount",enterpriseAccount);
	}
    
}
