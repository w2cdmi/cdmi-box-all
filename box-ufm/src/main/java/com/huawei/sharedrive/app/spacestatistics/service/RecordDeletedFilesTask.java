package com.huawei.sharedrive.app.spacestatistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.spacestatistics.dao.FilesDeleteDao;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.service.impl.RecordingDeletedFilesServiceImpl;

public class RecordDeletedFilesTask implements Runnable
{
    private Logger logger = LoggerFactory.getLogger(RecordDeletedFilesTask.class);
    
    private FilesDeleteDao filesDeleteDao;
    
    public RecordDeletedFilesTask(FilesDeleteDao filesDeleteDao)
    {
        this.filesDeleteDao = filesDeleteDao;
    }
    
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                FilesDelete deletedFile = RecordingDeletedFilesServiceImpl.DELETED_QUEUE.take();
                filesDeleteDao.insert(deletedFile);
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
