package com.huawei.sharedrive.uam.accountrole.dao;

import java.util.List;

import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;

public interface AccountRoleDao
{
    void create(AccountRole accountRole);
    
    int delete(long accountId, String roleId);
    
    List<AccountRole> getList(long accountId);
}
