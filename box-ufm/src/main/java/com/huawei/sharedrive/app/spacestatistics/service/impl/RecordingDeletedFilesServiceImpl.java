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

import com.huawei.sharedrive.app.spacestatistics.dao.FilesDeleteDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.service.RecordDeletedFilesTask;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingDeletedFilesService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Service("recordingDeletedFilesService")
public class RecordingDeletedFilesServiceImpl implements RecordingDeletedFilesService
{
    public final static BlockingQueue<FilesDelete> DELETED_QUEUE = new LinkedBlockingQueue<FilesDelete>(10000);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingDeletedFilesServiceImpl.class);
    
    private ExecutorService taskPool;
    
    @Autowired
    private FilesDeleteDao filesDeleteDao;
    
    @PostConstruct
    public void init()
    {
        int nThreads = Integer.parseInt(PropertiesUtils.getProperty("recordDeletedFile.taskNumber", "20"));
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        RecordDeletedFilesTask task;
        for (int i = 0; i < nThreads; i++)
        {
            task = new RecordDeletedFilesTask(filesDeleteDao);
            taskPool.execute(task);
        }
    }
    
    public void put(FilesDelete deletedFile)
    {
        try
        {
            DELETED_QUEUE.put(deletedFile);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("fail in record deleted file");
        }
    }
}
