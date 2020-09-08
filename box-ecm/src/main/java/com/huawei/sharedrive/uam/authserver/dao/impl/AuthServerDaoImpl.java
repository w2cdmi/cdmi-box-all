package com.huawei.sharedrive.uam.authserver.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.authserver.dao.AuthServerDao;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.CacheParameterUtils;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class AuthServerDaoImpl extends CacheableSqlMapClientDAO implements AuthServerDao
{
    
    private static Logger logger = LoggerFactory.getLogger(AuthServerDaoImpl.class);
    
    @Override
    public int getCountByEnterpriseId(long enterpriseId)
    {
        Object count = sqlMapClientTemplate.queryForObject("AuthServer.getCountByEnterpriseId", enterpriseId);
        if (null == count)
        {
            logger.error("no search AuthServer");
            throw new InvalidParamterException();
        }
        return (int) count;
    }
    
    @Override
    public List<AuthServer> getByEnterpriseId(long enterpriseId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AuthServer.getByEnterpriseId", map);
    }
    
    @Override
    public int getCountByNoStatus(long enterpriseId)
    {
        Object count = sqlMapClientTemplate.queryForObject("AuthServer.getCountByNoStatus", enterpriseId);
        if (null == count)
        {
            logger.error("no search AuthServer");
            throw new InvalidParamterException();
        }
        return (int) count;
    }
    
    @Override
    public List<AuthServer> getByNoStatus(long enterpriseId, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("AuthServer.getByNoStatus", map);
    }
    
    @Override
    public List<AuthServer> getListByAccountId(long enterpriseId, long accountId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("enterpriseId", enterpriseId);
        map.put("accountId", accountId);
        return sqlMapClientTemplate.queryForList("AuthServer.getListByAccountId", map);
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
    public AuthServer get(Long id)
    {
        if (isCacheSupported())
        {
            String key = CacheParameterUtils.AUTHSERVER_CACHE_ID + id;
            AuthServer authServer = (AuthServer) getCacheClient().getCache(key);
            if (authServer != null)
            {
                return authServer;
            }
            authServer = (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.get", id);
            if (authServer == null)
            {
                return null;
            }
            getCacheClient().setCache(key,
                authServer,
                System.currentTimeMillis() + CacheParameterUtils.AUTHSERVER_CACHE_TIME_OUT);
            return authServer;
        }
        return (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.get", id);
    }
    
    @Override
    public AuthServer getAuthServerNoStatus(Long id)
    {
        if (isCacheSupported())
        {
            String key = CacheParameterUtils.AUTHSERVER_CACHE_ID + id;
            AuthServer authServer = (AuthServer) getCacheClient().getCache(key);
            if (authServer != null)
            {
                return authServer;
            }
            authServer = (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.getAuthServerNoStatus",
                id);
            if (authServer == null)
            {
                return null;
            }
            getCacheClient().setCache(key,
                authServer,
                System.currentTimeMillis() + CacheParameterUtils.AUTHSERVER_CACHE_TIME_OUT);
            return authServer;
        }
        return (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.getAuthServerNoStatus", id);
    }
    
    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("AuthServer.delete", id);
        String key = CacheParameterUtils.AUTHSERVER_CACHE_ID + id;
        deleteCacheAfterCommit(key);
    }
    
    @Override
    public void updateAuthServerConfig(AuthServer authServer)
    {
        authServer.setModifiedAt(new Date());
        sqlMapClientTemplate.update("AuthServer.updateAuthServerConfig", authServer);
        String key = CacheParameterUtils.AUTHSERVER_CACHE_ID + authServer.getId();
        deleteCacheAfterCommit(key);
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
    public List<AuthServer> getFilterd(AuthServer filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        List<AuthServer> serverLists = sqlMapClientTemplate.queryForList("AuthServer.getFilterd", map);
        return serverLists;
    }
    
    @Override
    public int getFilterdCount(AuthServer filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("AuthServer.getFilterdCount", map);
    }
    
    @Override
    public AuthServer getDefaultAuthServer()
    {
        AuthServer authServer = (AuthServer) sqlMapClientTemplate.queryForObject("AuthServer.getDefaultAuthServer");
        return authServer;
    }
    
    @Override
    public void updateStatus(AuthServer authServer)
    {
        authServer.setModifiedAt(new Date());
        sqlMapClientTemplate.update("AuthServer.updateStatus", authServer);
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
