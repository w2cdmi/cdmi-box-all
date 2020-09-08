package com.huawei.sharedrive.app.statistics.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.TempObjectStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.TempObjectStatisticsDay;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("tempObjectStatisticsDAO")
@SuppressWarnings("deprecation")
public class TempObjectStatisticsDAOImpl extends AbstractDAOImpl implements TempObjectStatisticsDAO
{
    @Override
    public void insert(TempObjectStatisticsDay objectStatistics)
    {
        sqlMapClientTemplate.insert("TempObjectStatistics.insert", objectStatistics);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TempObjectStatisticsDay> getSumList(long day)
    {
        return sqlMapClientTemplate.queryForList("TempObjectStatistics.getSumDayList", day);
    }

    @Override
    public void clearData()
    {
        sqlMapClientTemplate.delete("TempObjectStatistics.deleteAll");
    }
    
}
