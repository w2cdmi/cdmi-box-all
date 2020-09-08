/**
 * 
 */
package com.huawei.sharedrive.isystem.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

/**
 * @author q90003805
 * 
 */
@Service("systemConfigDAO")
@SuppressWarnings("deprecation")
public class SystemConfigDAOImpl extends AbstractDAOImpl implements SystemConfigDAO
{
    /**
     * 集合初始化大小
     */
    private static final String DEFAULT_APP_ID = "-1";
    
    @Override
    public SystemConfig get(String id)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("id", id);
        map.put("appId", DEFAULT_APP_ID);
        return (SystemConfig) sqlMapClientTemplate.queryForObject("SystemConfig.get", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SystemConfig> getByPrefix(Limit limit, String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("prefix", prefix);
        map.put("appId", DEFAULT_APP_ID);
        map.put("limit", limit);
        return sqlMapClientTemplate.queryForList("SystemConfig.getByPrefix", map);
    }
    
    @Override
    public int getByPrefixCount(String prefix)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("prefix", prefix);
        map.put("appId", DEFAULT_APP_ID);
        return (Integer) sqlMapClientTemplate.queryForObject("SystemConfig.getByPrefixCount", map);
    }
    
    @Override
    public void create(SystemConfig systemConfig)
    {
        systemConfig.setAppId(DEFAULT_APP_ID);
        sqlMapClientTemplate.insert("SystemConfig.insert", systemConfig);
    }
    
    @Override
    public void update(SystemConfig systemConfig)
    {
        systemConfig.setAppId(DEFAULT_APP_ID);
        sqlMapClientTemplate.update("SystemConfig.update", systemConfig);
    }
    
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("SystemConfig.delete", id);
    }
}
