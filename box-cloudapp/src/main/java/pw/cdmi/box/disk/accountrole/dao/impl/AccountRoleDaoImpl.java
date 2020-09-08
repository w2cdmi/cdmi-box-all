package pw.cdmi.box.disk.accountrole.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.accountrole.dao.AccountRoleDao;
import pw.cdmi.box.disk.accountrole.domain.AccountRole;
import pw.cdmi.common.cache.CacheClient;

@SuppressWarnings({"unchecked", "deprecation"})
@Service
public class AccountRoleDaoImpl extends CacheableSqlMapClientDAO implements AccountRoleDao
{
    @Autowired(required = false)
    @Qualifier("uamCacheClient")
    private CacheClient cacheClient;
    
    @Override
    public void create(AccountRole accountRole)
    {
        sqlMapClientTemplate.insert("AccountRole.insert", accountRole);
    }
    
    @Override
    public void delete(long accountId, String roleId)
    {
        AccountRole accountRole = new AccountRole();
        accountRole.setResourceRole(roleId);
        accountRole.setAccountId(accountId);
        sqlMapClientTemplate.delete("AccountRole.delete", accountRole);
    }
    
    @Override
    public List<AccountRole> getList(long accountId)
    {
        return sqlMapClientTemplate.queryForList("AccountRole.getList", accountId);
    }
    
    @Override
    public CacheClient getCacheClient()
    {
        return cacheClient;
    }
    
}
