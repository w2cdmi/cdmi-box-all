package com.huawei.sharedrive.uam.log.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.log.dao.SystemLogDao;
import com.huawei.sharedrive.uam.log.domain.QueryCondition;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.log.SystemLog;

@SuppressWarnings({"deprecation", "unchecked"})
@Service
public class SystemLogDaoImpl extends AbstractDAOImpl implements SystemLogDao
{
    
    @Override
    public void create(SystemLog eventLog)
    {
        getUserLogTemplate().insert("SystemLog.insert", eventLog);
    }
    
    @Override
    public int getFilterdCount(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        return (Integer) getUserLogTemplate().queryForObject("SystemLog.getFilterdCount", map);
    }
    
    @Override
    public List<SystemLog> getFilterd(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        map.put("limit", filter.getPageRequest().getLimit());
        return getUserLogTemplate().queryForList("SystemLog.getFilterd", map);
    }
    
    @Override
    public int getCountByLoginName(SystemLog systemLog)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", systemLog);
        return (Integer) getUserLogTemplate().queryForObject("SystemLog.getCountByLoginName", map);
    }
    
    @Override
    public List<SystemLog> getByLoginName(SystemLog systemLog, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("filter", systemLog);
        map.put("limit", limit);
        return getUserLogTemplate().queryForList("SystemLog.getByLoginName", map);
    }
    
    @Override
    public void updateSuccess(String id)
    {
        
        getUserLogTemplate().update("SystemLog.updateSuccess", id);
    }
    
    @Override
    public void deleteById(String id)
    {
        
        getUserLogTemplate().update("SystemLog.deleteById", id);
    }
    
}
