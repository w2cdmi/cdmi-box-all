package com.huawei.sharedrive.app.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.SysConcStatisticsDAO;
import com.huawei.sharedrive.app.statistics.domain.SysConcStatisticsDay;
import com.huawei.sharedrive.app.statistics.service.ConcStatisticsService;

@Service("concStatisticsService")
public class ConcStatisticsServiceImpl implements ConcStatisticsService
{

    @Autowired
    private SysConcStatisticsDAO itemStatisticsDAO;

    @Override
    public List<SysConcStatisticsDay> getHistoryList(Integer beginDay, Integer endDay)
    {
        return itemStatisticsDAO.getSumListByRange(beginDay, endDay);
    }
    
}
