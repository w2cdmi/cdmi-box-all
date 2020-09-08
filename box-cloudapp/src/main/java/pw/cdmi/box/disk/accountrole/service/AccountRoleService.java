package pw.cdmi.box.disk.accountrole.service;

import java.util.List;

import pw.cdmi.box.disk.accountrole.domain.AccountRole;
import pw.cdmi.box.disk.accountrole.domain.PageNodeRoleInfo;

public interface AccountRoleService
{
    void create(long accountId, String roleId);
    
    void delete(long accountId, String roleId);
    
    List<AccountRole> getList(long accountId);
    
    List<PageNodeRoleInfo> listAccountRoles(long accountId);
}
