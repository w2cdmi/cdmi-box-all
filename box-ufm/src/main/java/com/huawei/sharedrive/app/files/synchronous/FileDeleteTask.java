package com.huawei.sharedrive.app.files.synchronous;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDeleteTask implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDeleteTask.class);
    
    private String filePath;
    
    public FileDeleteTask(String filePath)
    {
        this.filePath = filePath;
    }
    
    @Override
    public void run()
    {
        doDelete();
    }
    
    private void doDelete()
    {
        File dFile = null;
        
        dFile = new File(filePath);
        if (!dFile.exists())
        {
            return;
        }
        
        if (!dFile.delete())
        {
            LOGGER.error("file remove failed,filePath:" + filePath);
        }
        
    }
    
}
