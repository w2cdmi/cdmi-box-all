package com.huawei.sharedrive.app.spacestatistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.ClearRecycleBinDao;
import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;
import com.huawei.sharedrive.app.spacestatistics.service.ClearRecycleBinService;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;

@Service("clearRecycleBinService")
public class ClearRecycleBinServiceImpl implements ClearRecycleBinService
{
    
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    @Autowired
    private ClearRecycleBinDao clearRecycleBinDao;
    
    @Override
    public List<ClearRecycleBinRecord> getRecords()
    {
        return clearRecycleBinDao.getRecords();
    }
    
    @Override
    public List<Long> getAccountIds()
    {
        return clearRecycleBinDao.getAccountIds();
    }
    
    @Override
    public void deleteRecycleBinRecord(List<ClearRecycleBinRecord> records)
    {
        for (ClearRecycleBinRecord record : records)
        {
            clearRecycleBinDao.delete(record);
        }
    }
    
    @Override
    public void updateUserSpace(List<ClearRecycleBinRecord> records)
    {
        for (ClearRecycleBinRecord record : records)
        {
            spaceStatisticsService.updateUserWithoutCacheInfo(record.getOwnedBy(), record.getAccountId());
        }
        
    }
    
    @Override
    public void updateAccountSpace(List<Long> accountIds)
    {
        for (long accountId : accountIds)
        {
            spaceStatisticsService.updateAccountWithoutCacheInfo(accountId);
        }
        
    }
    
}
