package pw.cdmi.box.disk.authserver.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.authserver.dao.AuthServerDao;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AuthServer;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AuthServerDaoImpl extends CacheableSqlMapClientDAO implements AuthServerDao
{
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AuthServer.getByEnterpriseId", map);
    }
    
    @Override
    public AuthServer get(Long id)
    {
        AuthServer authServer = (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.get", id);
        return authServer;
    }
    
    @Override
    public CacheClient getCacheClient()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
