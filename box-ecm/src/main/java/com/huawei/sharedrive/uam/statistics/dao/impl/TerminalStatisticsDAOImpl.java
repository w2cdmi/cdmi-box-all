package com.huawei.sharedrive.uam.statistics.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.statistics.dao.TerminalStatisticsDAO;
import com.huawei.sharedrive.uam.statistics.domain.TerminalStatisticsDay;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("terminalStatisticsDAO")
@SuppressWarnings({"deprecation", "unchecked"})
public class TerminalStatisticsDAOImpl extends AbstractDAOImpl implements TerminalStatisticsDAO
{
    
    @Override
    public List<TerminalStatisticsDay> getList(int day)
    {
        return sqlMapClientTemplate.queryForList("TerminalStatistics.getByDay", day);
    }
    
    @Override
    public List<TerminalStatisticsDay> getListGroupByDeviceType(int day)
    {
        return sqlMapClientTemplate.queryForList("TerminalStatistics.getListGroupByDevice", day);
    }
    
    @Override
    public void insert(TerminalStatisticsDay terminalStatistics)
    {
        sqlMapClientTemplate.insert("TerminalStatistics.insert", terminalStatistics);
    }
    
    @Override
    public List<TerminalStatisticsDay> getListGroupByDeviceType(Integer beginDay, Integer endDay)
    {
        Map<String, Object> map = new HashMap<String, Object>(10);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        return sqlMapClientTemplate.queryForList("TerminalStatistics.getRangeListGroupByDevice", map);
    }
    
    @Override
    public List<TerminalStatisticsDay> getList(Integer beginDay, Integer endDay, Integer deviceType)
    {
        Map<String, Object> map = new HashMap<String, Object>(10);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        map.put("deviceType", deviceType);
        return sqlMapClientTemplate.queryForList("TerminalStatistics.getRangeList", map);
    }
}
