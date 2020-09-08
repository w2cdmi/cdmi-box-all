package com.huawei.sharedrive.uam.accountuser.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountuser.dao.UserAccountDao;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.idgenerate.service.UserAccountGenerateService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class UserAccountDaoImpl extends CacheableSqlMapClientDAO implements UserAccountDao
{
    private static Logger logger = LoggerFactory.getLogger(UserAccountDaoImpl.class);
    
    private static final int TABLE_COUNT = 100;
    
    @Autowired
    private UserAccountGenerateService userAccountGenerateService;
    
    @Override
    public long getMaxUserId()
    {
        long maxUserId = 1L;
        long selMaxUserId;
        String tableSuffix;
        Object maxUserIdObject;
        for (int i = 0; i < TABLE_COUNT; i++)
        {
            tableSuffix = "_" + i;
            maxUserIdObject = sqlMapClientTemplate.queryForObject("UserAccount.getMaxUserId",
                tableSuffix);
            selMaxUserId = maxUserIdObject == null ? 0L : (long) maxUserIdObject;
            if (maxUserId < selMaxUserId)
            {
                maxUserId = selMaxUserId;
            }
        }
        return maxUserId;
    }
    
    @Override
    public void create(UserAccount userAccount)
    {
        long id = userAccountGenerateService.getNextUserId();
        userAccount.setCreatedAt(new Date());
        userAccount.setId(id);
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        sqlMapClientTemplate.insert("UserAccount.insert", userAccount);
    }
    
    @Override
    public UserAccount getById(long id, long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("tableSuffix", getTableSuffix(accountId));
        map.put("id", id);
        UserAccount userAccount = (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.getById",
            map);
        return userAccount;
    }
    
    @Override
    public UserAccount get(long userId, long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(accountId));
        map.put("userId", userId);
        map.put("accountId", accountId);
        UserAccount userAccount = (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.get", map);
        return userAccount;
    }

    @Override
    public List<UserAccount> getByEnterpriseId(long enterpriseId, long accountId) {

        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(accountId));
        map.put("enterpriseId", enterpriseId);
        map.put("accountId", accountId);
        List<UserAccount> userAccounts = (List<UserAccount>) sqlMapClientTemplate.queryForList("UserAccount.getByEnterpriseId", map);
        return userAccounts;
    }

    @Override
    public UserAccount getByImAccount(String imAccount, long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("tableSuffix", getTableSuffix(accountId));
        map.put("imAccount", imAccount);
        map.put("accountId", accountId);
        UserAccount userAccount = (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.get", map);
        return userAccount;
    }

    @Override
    public void delByUserAccountId(UserAccount userAccount)
    {
        long accountId = userAccount.getAccountId();
        userAccount.setTableSuffix(getTableSuffix(accountId));
        sqlMapClientTemplate.delete("UserAccount.delByUserAccountId", userAccount);
    }
    
    @Override
    public UserAccount getBycloudUserAccountId(UserAccount userAccount)
    {
        long accountId = userAccount.getAccountId();
        userAccount.setTableSuffix(getTableSuffix(accountId));
        return (UserAccount) sqlMapClientTemplate.queryForObject("UserAccount.getBycloudUserAccountId",
            userAccount);
    }
    
    @Override
    public void update(UserAccount userAccount)
    {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        sqlMapClientTemplate.update("UserAccount.update", userAccount);
    }
    
    @Override
    public int getFilterdCount(long accountId, long enterpriseId, long userSource, String filter,
        Integer status)
    {
        
        Map<String, Object> map = new HashMap<String, Object>(7);
        map.put("accountTableSuffix", getTableSuffix(accountId));
        map.put("enterpriseTableSuffix", getTableSuffix(enterpriseId));
        map.put("accountId", accountId);
        map.put("enterpriseId", enterpriseId);
        map.put("userSource", userSource);
        map.put("status", status);
        if (StringUtils.isNotBlank(filter))
        {
            map.put("filter", filter);
        }
        
        Object count = sqlMapClientTemplate.queryForObject("UserAccount.getFilterdCount", map);
        if (null == count)
        {
            logger.error("no search UserAccount");
            throw new InvalidParamterException();
        }
        return (int) count;
        
    }
    
    @Override
    public List<UserAccount> getFilterd(UserAccount userAccount, long userSource, Limit limit, String filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("accountTableSuffix", getTableSuffix(userAccount.getAccountId()));
        map.put("enterpriseTableSuffix", getTableSuffix(userAccount.getEnterpriseId()));
        map.put("accountId", userAccount.getAccountId());
        map.put("enterpriseId", userAccount.getEnterpriseId());
        map.put("status", userAccount.getStatus());
        map.put("userSource", userSource);
        map.put("limit", limit);
        if (StringUtils.isNotBlank(filter))
        {
            map.put("filter", filter);
        }
        return sqlMapClientTemplate.queryForList("UserAccount.getFilterd", map);
    }
    
    @Override
    public void updateStatus(UserAccount userAccount, String ids)
    {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        userAccount.setModifiedAt(new Date());
        
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("ids", ids);
        map.put("filter", userAccount);
        sqlMapClientTemplate.update("UserAccount.updateStatus", map);
    }
    
    @Override
    public void updateRole(UserAccount userAccount, String ids)
    {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        userAccount.setModifiedAt(new Date());
        
        Map<String, Object> map = new HashMap<String, Object>(8);
        map.put("ids", ids);
        map.put("filter", userAccount);
        sqlMapClientTemplate.update("UserAccount.updateRole", map);
    }
    
    @Override
    public void updateLoginTime(UserAccount userAccount)
    {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        userAccount.setLastLoginAt(new Date());
        sqlMapClientTemplate.update("UserAccount.updateLoginTime", userAccount);
    }

    @Override
    public void updateSpaceQuota(UserAccount userAccount) {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        sqlMapClientTemplate.update("UserAccount.updateSpaceQuota",userAccount);
    }

    public static String getTableSuffix(long accountId)
    {
        String tableSuffix = null;
        int table = (int) (HashTool.apply(String.valueOf(accountId)) % TABLE_COUNT);
        tableSuffix = "_" + table;
        return tableSuffix;
    }
    
    @Override
    public void updateFirstLogin(UserAccount userAccount)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", userAccount.getAccountId());
        map.put("userId", userAccount.getUserId());
        map.put("firstLogin", userAccount.getFirstLogin());
        map.put("tableSuffix", getTableSuffix(userAccount.getAccountId()));
        sqlMapClientTemplate.update("UserAccount.updateFirstLogin", map);
    }
    
    @Override
    public void updateUserIdById(UserAccount userAccount) {
        userAccount.setTableSuffix(getTableSuffix(userAccount.getAccountId()));
        userAccount.setModifiedAt(new Date());
       
        sqlMapClientTemplate.update("UserAccount.updateUserIdById", userAccount);
    }

    @Override
    public int countByAccountId(long accountId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("tableSuffix", getTableSuffix(accountId));

        return (int)sqlMapClientTemplate.queryForObject("UserAccount.countByAccountId", map);
    }

    @Override
    public long sumSpaceQuotaByAccountId(long accountId) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("tableSuffix", getTableSuffix(accountId));

        return (long)sqlMapClientTemplate.queryForObject("UserAccount.sumSpaceQuotaByAccountId", map);
    }

    @Override
    public int countByAccountIdAndSpaceQuota(long accountId, long quota) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("spaceQuota", quota);
        map.put("tableSuffix", getTableSuffix(accountId));

        return (int)sqlMapClientTemplate.queryForObject("UserAccount.countByAccountIdAndSpaceQuota", map);
    }

    @Override
    public void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("oldValue", oldValue);
        map.put("newValue", newValue);
        map.put("tableSuffix", getTableSuffix(accountId));

        sqlMapClientTemplate.update("UserAccount.compareAndSwapSpaceQuotaByAccountId", map);
    }

    @Override
    public long sumSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("userIds", userIds);
        map.put("tableSuffix", getTableSuffix(accountId));

        return (long)sqlMapClientTemplate.queryForObject("UserAccount.sumSpaceQuotaByAccountIdAndUserIds", map);
    }

    @Override
    public void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long spaceQuota) {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("accountId", accountId);
        map.put("userIds", userIds);
        map.put("spaceQuota", spaceQuota);
        map.put("tableSuffix", getTableSuffix(accountId));

        sqlMapClientTemplate.update("UserAccount.updateSpaceQuotaByAccountIdAndUserIds", map);
    }
}
