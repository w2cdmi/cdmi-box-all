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

import com.huawei.sharedrive.app.spacestatistics.dao.FilesAddDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.service.RecordAddedFilesTask;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingAddedFilesService;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

@Service("recordingAddedFilesService")
public class RecordingAddedFilesServiceImpl implements RecordingAddedFilesService
{
    public final static BlockingQueue<FilesAdd> ADDED_FILE_QUEUE = new LinkedBlockingQueue<FilesAdd>(10000);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordingAddedFilesServiceImpl.class);
    
    private ExecutorService taskPool;
    
    @Autowired
    private FilesAddDao filesAddDao;
    
    @PostConstruct
    public void init()
    {
        int nThreads = Integer.parseInt(PropertiesUtils.getProperty("recordAddedFile.taskNumber", "20"));
        taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(nThreads));
        RecordAddedFilesTask task;
        for (int i = 0; i < nThreads; i++)
        {
            task = new RecordAddedFilesTask(filesAddDao);
            taskPool.execute(task);
        }
    }
    
    public void put(FilesAdd addedFile)
    {
        try
        {
            ADDED_FILE_QUEUE.put(addedFile);
        }
        catch (InterruptedException e)
        {
            LOGGER.error("fail in record add file");
        }
    }
}
