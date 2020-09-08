package com.huawei.sharedrive.isystem.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.statistics.dao.StatisticsAccessKeyDAO;
import com.huawei.sharedrive.isystem.statistics.domain.StatisticsAccessKey;
import com.huawei.sharedrive.isystem.statistics.service.StatisticsAccessKeyService;
import com.huawei.sharedrive.isystem.util.RandomKeyGUID;

@Service
public class StatisticsAccessKeyServiceImpl implements StatisticsAccessKeyService
{
    
    @Autowired
    private StatisticsAccessKeyDAO accessKeyDao;
    
    private static final int MAX_TOTAL = 5;
    
    @Override
    public StatisticsAccessKey create()
    {
        List<StatisticsAccessKey> staisticsAccessKeies = accessKeyDao.queryList();
        if (staisticsAccessKeies.size() >= MAX_TOTAL)
        {
            return null;
        }
        StatisticsAccessKey statisticsAccessKey = new StatisticsAccessKey();
        statisticsAccessKey.setId(RandomKeyGUID.getSecureRandomGUID());
        statisticsAccessKey.setSecretKey(RandomKeyGUID.getSecureRandomGUID());
        accessKeyDao.create(statisticsAccessKey);
        return statisticsAccessKey;
    }
    
    @Override
    public List<StatisticsAccessKey> getList()
    {
        return accessKeyDao.queryList();
    }
    
    @Override
    public void deleteById(String accessKey)
    {
        accessKeyDao.delete(accessKey);
    }

    @Override
    public StatisticsAccessKey get(String id)
    {
        return accessKeyDao.get(id);
    }
    
}
