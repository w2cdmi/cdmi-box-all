package com.huawei.sharedrive.app.spacestatistics.service.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.ClearRecycleBinDao;
import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;
import com.huawei.sharedrive.app.spacestatistics.service.RecordClearTrashTask;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingClearTrashService;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Service("recordingClearTrashService")
public class RecordingClearTrashServiceImpl implements RecordingClearTrashService
{
    public final static BlockingQueue<ClearRecycleBinRecord> RECORD_QUEUE = new LinkedBlockingQueue<ClearRecycleBinRecord>(
        10000);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingClearTrashServiceImpl.class);
    
    private ExecutorService taskPool;
    
    @Autowired
    private ClearRecycleBinDao clearRecycleBinDao;
    
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    @PostConstruct
    public void init()
    {
        int nThreads = Integer.parseInt(PropertiesUtils.getProperty("recordClearRecycleBin.taskNumber", "20"));
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        RecordClearTrashTask task;
        for (int i = 0; i < nThreads; i++)
        {
            task = new RecordClearTrashTask(clearRecycleBinDao, spaceStatisticsService);
            taskPool.execute(task);
        }
    }
    
    public void put(ClearRecycleBinRecord clearRecycleBinRecord)
    {
        try
        {
            RECORD_QUEUE.put(clearRecycleBinRecord);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("fail in record clear recycleBin task");
        }
    }
}
