package com.huawei.sharedrive.app.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.account.service.AccountService;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;

@Component
public class AccountServiceImpl implements AccountService
{
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Override
    public void create(Account account)
    {
        accountDao.create(account);
    }
    
    @Override
    public Account getById(long accountId)
    {
        return accountDao.getById(accountId);
    }
    
    @Override
    public Account getByDomain(String domain)
    {
        return accountDao.getByDomain(domain);
    }
    
    @Override
    public int update(Account account)
    {
        return accountDao.update(account);
    }
    
    @Override
    public Account getOneAccountOrderByACS(String appId, long id)
    {
        
        return accountDao.getOneAccountOrderByACS(appId, id);
    }
    
    @Override
    public Account updateCurrentMember(Account account)
    {
        int currentMember = userReverseDAO.countActiveUserByAccountId(account.getId());
        account.setCurrentMember(currentMember);
        update(account);
        return account;
    }
    
    @Override
    public Boolean isUserExceed(Account account)
    {
        Integer maxMember = account.getMaxMember();
        int currentMember = userReverseDAO.countActiveUserByAccountId(account.getId());

        if (maxMember == null || maxMember == -1)
        {
            //为空或-1时，表示不限制，取最大值
            maxMember = AccountConstants.UNLIMIT_NUM_RESTORE;
        }

        if (maxMember - currentMember >= 1)
        {
            return false;
        }
        return true;
    }
    
    @Override
    public Boolean isTeamspaceExceed(Account account)
    {
        Integer maxTeamspace = account.getMaxTeamspace();
        int currentTeamspace = userReverseDAO.countActiveTeamspaceByAccountId(account.getId());
        if (maxTeamspace == null || maxTeamspace == -1)
        {
            maxTeamspace = AccountConstants.UNLIMIT_NUM_RESTORE;
        }
        
        if (maxTeamspace - currentTeamspace >= 1)
        {
            return false;
        }
        return true;
    }
    
    @Override
    public Account updateCurrentTeamspace(Account account)
    {
        int currentTeamspace = userReverseDAO.countActiveTeamspaceByAccountId(account.getId());
        account.setCurrentTeamspace(currentTeamspace);
        update(account);
        return account;
    }
}
