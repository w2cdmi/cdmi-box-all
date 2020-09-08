package pw.cdmi.box.uam.log.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.uam.log.dao.SystemLoginLogDao;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.common.log.SystemLog;

@SuppressWarnings({"deprecation", "unchecked"})
@Service
public class SystemLoginLogDaoImpl extends AbstractDAOImpl implements SystemLoginLogDao
{
    
    @Override
    public void create(SystemLog eventLog)
    {
        getUserLogTemplate().insert("SystemLoginLog.insert", eventLog);
    }
    
    @Override
    public int getFilterdCount(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        return (Integer) getUserLogTemplate().queryForObject("SystemLoginLog.getFilterdCount", map);
    }
    
    @Override
    public List<SystemLog> getFilterd(QueryCondition filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", filter);
        map.put("limit", filter.getPageRequest().getLimit());
        return getUserLogTemplate().queryForList("SystemLoginLog.getFilterd", map);
    }
    
    @Override
    public void deleteById(String id)
    {
        
        getUserLogTemplate().update("SystemLoginLog.deleteById", id);
    }
    
}
