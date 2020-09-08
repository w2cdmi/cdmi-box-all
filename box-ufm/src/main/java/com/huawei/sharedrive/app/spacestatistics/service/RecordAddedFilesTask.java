package com.huawei.sharedrive.app.spacestatistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.spacestatistics.dao.FilesAddDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.service.impl.RecordingAddedFilesServiceImpl;

public class RecordAddedFilesTask implements Runnable
{
    private Logger logger = LoggerFactory.getLogger(RecordAddedFilesTask.class);
    
    private FilesAddDao filesAddDao;
    
    public RecordAddedFilesTask(FilesAddDao filesAddDao)
    {
        this.filesAddDao = filesAddDao;
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                FilesAdd addedFile = RecordingAddedFilesServiceImpl.ADDED_FILE_QUEUE.take();
                filesAddDao.insert(addedFile);
            }
            catch (InterruptedException e)
            {
                logger.error("Fail in record deletedFile", e);
            }
            catch (Exception e)
            {
                logger.error("Fail in record deletedFile", e);
            }
        }
    }
    
}
