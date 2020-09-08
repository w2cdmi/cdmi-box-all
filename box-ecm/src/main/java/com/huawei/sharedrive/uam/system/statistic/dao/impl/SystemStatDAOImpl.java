package com.huawei.sharedrive.uam.system.statistic.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.system.statistic.dao.SystemStatDAO;
import com.huawei.sharedrive.uam.system.statistic.domain.SystemStat;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("systemStatDAO")
public class SystemStatDAOImpl extends AbstractDAOImpl implements SystemStatDAO
{
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(SystemStat stat)
    {
        sqlMapClientTemplate.insert("SystemStat.insert", stat);
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public SystemStat get(Date statDate, String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("statDate", statDate);
        map.put("appId", appId);
        return (SystemStat) sqlMapClientTemplate.queryForObject("SystemStat.getByDate", map);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<SystemStat> listSystemStat(Date beginDate, Date endDate, String appId)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("beginDate", beginDate);
        map.put("endDate", endDate);
        map.put("appId", appId);
        return sqlMapClientTemplate.queryForList("SystemStat.listFilter", map);
    }
    
}
