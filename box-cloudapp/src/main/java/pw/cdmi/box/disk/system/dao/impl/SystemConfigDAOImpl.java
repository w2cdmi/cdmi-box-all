package pw.cdmi.box.disk.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

@Service("SystemConfigDAO")
@SuppressWarnings("deprecation")
public class SystemConfigDAOImpl extends AbstractDAOImpl implements SystemConfigDAO
{
    @Override
    public SystemConfig getByPriKey(String appId, String id)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("id", id);
        return (SystemConfig) sqlMapClientTemplate.queryForObject("SystemConfig.get", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SystemConfig> getByPrefix(String appId, Limit limit, String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("prefix", prefix);
        map.put("appId", appId);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("SystemConfig.getByPrefix", map);
    }
    
    @Override
    public int getByPrefixCount(String appId, String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("prefix", prefix);
        map.put("appId", appId);
        return (Integer) sqlMapClientTemplate.queryForObject("SystemConfig.getByPrefixCount", map);
    }
    
    @Override
    public void create(SystemConfig systemConfig)
    {
        sqlMapClientTemplate.insert("SystemConfig.insert", systemConfig);
    }
    
    @Override
    public void update(SystemConfig systemConfig)
    {
        sqlMapClientTemplate.update("SystemConfig.update", systemConfig);
    }
    
    @Override
    public void deleteByPriKey(String appId, String id)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("appId", appId);
        map.put("id", id);
        sqlMapClientTemplate.delete("SystemConfig.delete", map);
    }
    
    @Deprecated
    @Override
    public void delete(String id)
    {
    }
    
    @Deprecated
    @Override
    public SystemConfig get(String id)
    {
        return null;
    }
    
}
