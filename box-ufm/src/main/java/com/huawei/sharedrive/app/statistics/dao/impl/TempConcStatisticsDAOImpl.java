package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.TempConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.TempConcStatistics;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("tempConcStatisticsDAO")
@SuppressWarnings("deprecation")
public class TempConcStatisticsDAOImpl extends AbstractDAOImpl implements TempConcStatisticsDAO
{
    @Override
    public TempConcStatistics get(int day, String host, int timeUnit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("day", day);
        map.put("host", host);
        map.put("timeUnit", timeUnit);
        Object obj = sqlMapClientTemplate.queryForObject("TempConcStatistics.get", map);
        if(obj != null)
        {
            return (TempConcStatistics)obj;
        }
        return null;
    }

    @Override
    public void insert(TempConcStatistics tempConcStatistics)
    {
        sqlMapClientTemplate.insert("TempConcStatistics.insert", tempConcStatistics);
    }


    @Override
    public void update(TempConcStatistics tempConcStatistics)
    {
        sqlMapClientTemplate.update("TempConcStatistics.update", tempConcStatistics);
        
    }

    @Override
    public int getMaxUpload(int day)
    {
        Object obj = sqlMapClientTemplate.queryForObject("TempConcStatistics.getMaxUpload", day);
        if(null != obj)
        {
            return (Integer)obj;
        }
        return 0;
    }

    @Override
    public int getMaxDownload(int day)
    {
        Object obj = sqlMapClientTemplate.queryForObject("TempConcStatistics.getMaxDownload", day);
        if(null != obj)
        {
            return (Integer)obj;
        }
        return 0;
    }
}
