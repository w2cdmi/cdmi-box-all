package com.huawei.sharedrive.uam.statistics.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.statistics.dao.StatisticsChartDao;
import com.huawei.sharedrive.uam.statistics.domain.StatisticsTempChart;

@SuppressWarnings("deprecation")
@Repository
public class StatisticsChartDaoImpl implements StatisticsChartDao
{
    
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    @Override
    public void deleteEndToCreatedAt(Date createdAt)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("createdAt", createdAt);
        sqlMapClientTemplate.delete("StatisticsTempChart.deleteByCreatedAt", map);
    }
    
    @Override
    public void insert(StatisticsTempChart statisticsTempChart)
    {
        sqlMapClientTemplate.insert("StatisticsTempChart.insert", statisticsTempChart);
    }
    
    @Override
    public StatisticsTempChart get(String id)
    {
        return (StatisticsTempChart) sqlMapClientTemplate.queryForObject("StatisticsTempChart.getTempChart",
            id);
    }
    
}
