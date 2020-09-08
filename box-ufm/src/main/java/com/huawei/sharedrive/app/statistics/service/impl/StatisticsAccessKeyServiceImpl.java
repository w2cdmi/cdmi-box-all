package com.huawei.sharedrive.app.statistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.statistics.dao.StatisticsAccessKeyDAO;
import com.huawei.sharedrive.app.statistics.domain.StatisticsAccessKey;
import com.huawei.sharedrive.app.statistics.service.StatisticsAccessKeyService;

@Service("statisticsAccessKeyService")
public class StatisticsAccessKeyServiceImpl implements StatisticsAccessKeyService
{
    
    @Autowired
    private StatisticsAccessKeyDAO accessKeyDao;
    
    @Override
    public StatisticsAccessKey get(String accessKey)
    {
        return accessKeyDao.get(accessKey);
    }
}
