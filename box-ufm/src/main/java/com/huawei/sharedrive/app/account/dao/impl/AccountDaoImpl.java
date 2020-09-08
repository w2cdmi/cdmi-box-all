package com.huawei.sharedrive.app.account.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service
public class AccountDaoImpl extends CacheableSqlMapClientDAO implements AccountDao
{
    private static final long BASE_ACCOUNT_ID = 0;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(Account account)
    {
        sqlMapClientTemplate.insert("Account.insert", account);
    }
    
    @Override
    public void delete(long accountId)
    {
        String key = Account.CACHE_KEY_PREFIX_ID + accountId;
        deleteCacheAfterCommit(key);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxAccountId()
    {
        Object accountId = sqlMapClientTemplate.queryForObject("Account.getMaxAccountId");
        if (accountId == null)
        {
            return BASE_ACCOUNT_ID;
        }
        return (long) accountId;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Account getById(long id)
    {
        String key = Account.CACHE_KEY_PREFIX_ID + id;
        Account account;
        if (isCacheSupported())
        {
            account = (Account) getCacheClient().getCache(key);
            if (account != null)
            {
                return account;
            }
            
        }
        account = (Account) sqlMapClientTemplate.queryForObject("Account.getById", id);
        if (account == null)
        {
            return null;
        }
        
        /*Integer currentMember = account.getCurrentMember();
        if (currentMember == null)
        {
            currentMember = userReverseDAO.countActiveUserByAccountId(id);
            account.setCurrentMember(currentMember);
        }
        Integer currentTeamspace = account.getCurrentTeamspace();
        if (currentTeamspace == null)
        {
            currentTeamspace = userReverseDAO.countActiveTeamspaceByAccountId(id);
            account.setCurrentTeamspace(currentTeamspace);
        }
        if (currentMember == null || currentTeamspace == null)
        {
            update(account);
        }*/
        Long spaceUsed = account.getCurrentSpace();
        Long currentFiles = account.getCurrentFile();
        AccountStatisticsInfo accountInfo = null;
        if (spaceUsed == null || currentFiles == null)
        {
            accountInfo = userReverseDAO.getAccountInfoById(id);
            if (accountInfo != null)
            {
                spaceUsed = accountInfo.getCurrentSpace();
                currentFiles = accountInfo.getCurrentFiles();
                account.setCurrentFile(currentFiles);
                account.setCurrentSpace(spaceUsed);
            }
        }
        if (isCacheSupported())
        {
            getCacheClient().setCache(key, account);
            if (accountInfo != null)
            {
                getCacheClient().setCache(AccountStatisticsInfo.CACHE_KEY_PRIFIX_ACCOUNTSPACE + id,
                    accountInfo);
            }
        }
        return account;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Account getByDomain(String domain)
    {
        return (Account) sqlMapClientTemplate.queryForObject("Account.getByDomain", domain);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int update(Account account)
    {
        int result = sqlMapClientTemplate.update("Account.update", account);
        String key = Account.CACHE_KEY_PREFIX_ID + account.getId();
        if (isCacheSupported())
        {
            deleteCacheAfterCommit(key);
        }
        return result;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Account getOneAccountOrderByACS(String appId, long id)
    {
        Account account = new Account();
        account.setAppId(appId);
        account.setId(id);
        return (Account) sqlMapClientTemplate.queryForObject("Account.getOneAccountOrderByACS", account);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<Account> getListByAppId(String appId)
    {
        return sqlMapClientTemplate.queryForList("Account.getByAppId", appId);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void updateStatisticsInfo(long accountId, AccountStatisticsInfo accountInfo)
    {
        Account account = new Account();
        account.setId(accountId);
        account.setCurrentSpace(accountInfo.getCurrentSpace());
        account.setCurrentFile(accountInfo.getCurrentFiles());
        sqlMapClientTemplate.update("Account.updateStatisticsInfo", account);
        String key = Account.CACHE_KEY_PREFIX_ID + account.getId();
        if (isCacheSupported())
        {
            deleteCacheAfterCommit(key);
        }
    }
    
    @Override
    public long getMaxSpace(long accountId)
    {
        Account account;
        String key = Account.CACHE_KEY_PREFIX_ID + accountId;
        if (isCacheSupported())
        {
            account = (Account) getCacheClient().getCache(key);
            if (account != null)
            {
                if(account.getMaxSpace() == null) {
                    return AccountConstants.UNLIMIT_NUM;
                }

                return account.getMaxSpace();
            }
        }
        return getMaxSpaceForDB(accountId);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxSpaceForDB(long accountId)
    {
        Object maxSpace = sqlMapClientTemplate.queryForObject("Account.getMaxSpace", accountId);
        if (null == maxSpace)
        {
            return -1L;
        }
        if ((long) maxSpace == AccountConstants.UNLIMIT_NUM_RESTORE_SPACE)
        {
            return AccountConstants.UNLIMIT_NUM;
        }
        return (long) maxSpace;
    }
}
