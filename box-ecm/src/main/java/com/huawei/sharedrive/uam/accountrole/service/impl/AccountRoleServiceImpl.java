package com.huawei.sharedrive.uam.accountrole.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountrole.dao.AccountRoleDao;
import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;
import com.huawei.sharedrive.uam.accountrole.service.AccountRoleService;

@Service
public class AccountRoleServiceImpl implements AccountRoleService
{
    @Autowired
    private AccountRoleDao accountRoleDao;
    
    @Override
    public void create(long accountId, String roleId)
    {
        AccountRole accountRole = new AccountRole();
        accountRole.setResourceRole(roleId);
        accountRole.setAccountId(accountId);
        accountRoleDao.create(accountRole);
    }
    
    @Override
    public int delete(long accountId, String roleId)
    {
        return accountRoleDao.delete(accountId, roleId);
    }
    
    @Override
    public List<AccountRole> getList(long accountId)
    {
        return accountRoleDao.getList(accountId);
    }
    
}
