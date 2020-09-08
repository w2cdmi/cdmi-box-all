package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.SysConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("itemStatisticsDAO")
@SuppressWarnings("deprecation")
public class SysConcStatisticsDAOImpl extends AbstractDAOImpl implements SysConcStatisticsDAO
{
    @Override
    public SysConcStatisticsDay get(int day)
    {
        Object obj = sqlMapClientTemplate.queryForObject("SysConcStatistics.get", day);
        if(obj != null)
        {
            return (SysConcStatisticsDay)obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysConcStatisticsDay> getSumListByRange(Integer beginDay, Integer endDay)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("beginDay", beginDay);
        map.put("endDay", endDay);
        return sqlMapClientTemplate.queryForList("SysConcStatistics.getByRange", map);
    }

    @Override
    public void insert(SysConcStatisticsDay itemStatisticsDay)
    {
        sqlMapClientTemplate.insert("SysConcStatistics.insert", itemStatisticsDay);
    }

    @Override
    public void update(SysConcStatisticsDay itemStatisticsDay)
    {
        sqlMapClientTemplate.update("SysConcStatistics.update", itemStatisticsDay);
    }
}
