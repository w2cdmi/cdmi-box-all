package com.huawei.sharedrive.uam.accountrole.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountrole.dao.AccountRoleDao;
import com.huawei.sharedrive.uam.accountrole.domain.AccountRole;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class AccountRoleDaoImpl extends CacheableSqlMapClientDAO implements AccountRoleDao
{
    
    @Override
    public void create(AccountRole accountRole)
    {
        sqlMapClientTemplate.insert("AccountRole.insert", accountRole);
    }
    
    @Override
    public int delete(long accountId, String roleId)
    {
        AccountRole accountRole = new AccountRole();
        accountRole.setResourceRole(roleId);
        accountRole.setAccountId(accountId);
        return sqlMapClientTemplate.delete("AccountRole.delete", accountRole);
    }
    
    @Override
    public List<AccountRole> getList(long accountId)
    {
        return sqlMapClientTemplate.queryForList("AccountRole.getList", accountId);
    }
    
}
