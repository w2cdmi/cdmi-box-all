package com.huawei.sharedrive.app.spacestatistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.spacestatistics.dao.ClearRecycleBinDao;
import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;
import com.huawei.sharedrive.app.spacestatistics.service.impl.RecordingClearTrashServiceImpl;

public class RecordClearTrashTask implements Runnable
{
    private Logger logger = LoggerFactory.getLogger(RecordClearTrashTask.class);
    
    private ClearRecycleBinDao clearRecycleBinDao;
    
    private SpaceStatisticsService spaceStatisticsService;
    
    public RecordClearTrashTask(ClearRecycleBinDao clearRecycleBinDao,
        SpaceStatisticsService spaceStatisticsService)
    {
        this.clearRecycleBinDao = clearRecycleBinDao;
        this.spaceStatisticsService = spaceStatisticsService;
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                ClearRecycleBinRecord record = RecordingClearTrashServiceImpl.RECORD_QUEUE.take();
                clearRecycleBinDao.insert(record);
                spaceStatisticsService.updateUserWithoutCacheInfo(record.getOwnedBy(), record.getAccountId());
                clearRecycleBinDao.delete(record);
            }
            catch (InterruptedException e)
            {
                logger.error("Fail in clear recycleBin", e);
            }
            catch (Exception e)
            {
                logger.error("Fail in clear recycleBin", e);
            }
        }
    }
    
}
