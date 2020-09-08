package pw.cdmi.box.disk.authserver.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.authserver.dao.AccountAuthserverDao;
import pw.cdmi.box.disk.authserver.domain.AccountAuthserver;
import pw.cdmi.common.cache.CacheClient;

@SuppressWarnings("deprecation")
@Service
public class AccountAuthserverDaoImpl extends CacheableSqlMapClientDAO implements AccountAuthserverDao
{
    
    @Override
    public AccountAuthserver getByAccountAuthId(Long accountId, Long authserverId)
    {
        Map<String, Long> map = new HashMap<String, Long>(2);
        map.put("accountId", accountId);
        map.put("authserverId", authserverId);
        return (AccountAuthserver) sqlMapClientTemplate.queryForObject("AccountAuthserver.getByAccountAuthId",
            map);
    }
    
    @Override
    public CacheClient getCacheClient()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
