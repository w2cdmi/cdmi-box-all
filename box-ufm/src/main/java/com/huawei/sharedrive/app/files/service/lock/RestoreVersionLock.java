package com.huawei.sharedrive.app.files.service.lock;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.BusinessConstants;

public final class RestoreVersionLock
{
    private RestoreVersionLock()
    {
        
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(RestoreVersionLock.class);
    
    private static Map<Long, Boolean> lockMap = new HashMap<Long, Boolean>(BusinessConstants.INITIAL_CAPACITIES);
    
    public static void unlock(long ownedBy)
    {
        synchronized (RestoreVersionLock.class)
        {
            lockMap.remove(ownedBy);
        }
    }
    
    @SuppressWarnings("static-access")
    public static void lock(long ownedBy)
    {
        synchronized (RestoreVersionLock.class)
        {
            if(lockMap.get(ownedBy) == null)
            {
                lockMap.put(ownedBy, true);
                return;
            }
        }
        try
        {
            Thread.currentThread().sleep(2 * 1000);
            lock(ownedBy);
        }
        catch(Exception throwable)
        {
            LOGGER.warn("", throwable);
        }
    }
    
    
}
