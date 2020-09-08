package com.huawei.sharedrive.uam.accountrole.service;

import java.util.List;

import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;

public interface AccountRoleService
{
    void create(long accountId, String roleId);
    
    int delete(long accountId, String roleId);
    
    List<AccountRole> getList(long accountId);
}
