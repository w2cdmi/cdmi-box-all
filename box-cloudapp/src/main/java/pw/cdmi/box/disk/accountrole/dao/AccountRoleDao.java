package pw.cdmi.box.disk.accountrole.dao;

import java.util.List;

import pw.cdmi.box.disk.accountrole.domain.AccountRole;

public interface AccountRoleDao
{
    void create(AccountRole accountRole);
    
    void delete(long accountId, String roleId);
    
    List<AccountRole> getList(long accountId);
}
