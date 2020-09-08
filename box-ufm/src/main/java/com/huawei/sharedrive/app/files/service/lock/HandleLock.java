package com.huawei.sharedrive.app.files.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleLock
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleLock.class);
    
    private String name;
    
    private int maxConcurrent = 15;
    
    public HandleLock(String name, int maxConcurrent)
    {
        this.name = name;
        this.maxConcurrent = maxConcurrent;
    }
    
    public void unlock()
    {
        LOGGER.debug("unlock");
    }
    
    public void lock() throws InterruptedException
    {
        LOGGER.debug("unlock");
    }
    
    public boolean tryLock()
    {
        LOGGER.debug("trylock");
        return true;
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getMaxConcurrent()
    {
        return maxConcurrent;
    }
}
