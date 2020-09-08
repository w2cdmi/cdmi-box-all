package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.TempUserNodeStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.TempUserNodeStatistics;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("tempUserNodeStatisticsDAO")
@SuppressWarnings("deprecation")
public class TempUserNodeStatisticsDAOImpl extends AbstractDAOImpl implements TempUserNodeStatisticsDAO
{

    
    @Override
    public void deleteAll()
    {
        sqlMapClientTemplate.delete("TempUserNodeStatistics.deleteAll");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TempUserNodeStatistics> getGatherList(long day)
    {
        return sqlMapClientTemplate.queryForList("TempUserNodeStatistics.getGroupByAppAndRegion", day);
    }

    @Override
    public void save(TempUserNodeStatistics tempUserNodeStatistics)
    {
        if(null == tempUserNodeStatistics.getAppId())
        {
            tempUserNodeStatistics.setAppId("Unknown");
        }
        sqlMapClientTemplate.insert("TempUserNodeStatistics.insert", tempUserNodeStatistics);
    }

    @Override
    public void saveList(List<TempUserNodeStatistics> tempList)
    {
        for(TempUserNodeStatistics tempStatistics: tempList)
        {
            save(tempStatistics);
        }
    }
    
}
