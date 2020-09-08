package pw.cdmi.box.uam.accountrole.dao;

import java.util.List;

import pw.cdmi.box.uam.accountrole.domain.AccountRole;

public interface AccountRoleDao
{
    void create(AccountRole accountRole);
    
    int delete(long accountId, String roleId);
    
    List<AccountRole> getList(long accountId);
}
