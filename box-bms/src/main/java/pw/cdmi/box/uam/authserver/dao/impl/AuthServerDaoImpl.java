package pw.cdmi.box.uam.authserver.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.uam.authserver.dao.AuthServerDao;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.CacheParameterUtils;

@Service
@SuppressWarnings("deprecation")
public class AuthServerDaoImpl extends CacheableSqlMapClientDAO implements AuthServerDao
{
    
    @Override
    public AuthServer getByEnterpriseIdType(long enterpriseId, String type)
    {
        AuthServer filter = new AuthServer();
        filter.setEnterpriseId(enterpriseId);
        filter.setType(type);
        
        AuthServer authServer = (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.getByEnterpriseIdType",
            filter);
        return authServer;
    }
    
    @Override
    public void create(AuthServer authServer)
    {
        authServer.setId(getNextAvailableId());
        Date date = new Date();
        authServer.setCreatedAt(date);
        authServer.setModifiedAt(date);
        sqlMapClientTemplate.insert("AuthServer.insert", authServer);
    }
    
    @Override
    public void updateLocalAuth(AuthServer authServer)
    {
        authServer.setModifiedAt(new Date());
        sqlMapClientTemplate.update("AuthServer.updateLocalAuth", authServer);
        String key = CacheParameterUtils.AUTHSERVER_CACHE_ID + authServer.getId();
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public long getNextAvailableId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "authServerId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
}
