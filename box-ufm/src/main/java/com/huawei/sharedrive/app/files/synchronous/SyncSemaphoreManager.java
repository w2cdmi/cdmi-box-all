package com.huawei.sharedrive.app.files.synchronous;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.huawei.sharedrive.app.utils.PropertiesUtils;

public class SyncSemaphoreManager
{
    private static final int TIME_OUT = Integer.parseInt(PropertiesUtils.getProperty("sync.files.metadate.timeout.senconds",
        "30"));
    
    private Semaphore rootSyncSemaphore = null;
    
    private Semaphore folderSyncSemaphore = null;
    
    // 客户端下载请求超时时间
    
    public SyncSemaphoreManager()
    {
        int rootSyncSemaphoreNum = Integer.parseInt(PropertiesUtils.getProperty("sync.files.root.semaphore.num",
            "40"));
        rootSyncSemaphore = new Semaphore(rootSyncSemaphoreNum);
        
        int folderSyncSemaphoreNum = Integer.parseInt(PropertiesUtils.getProperty("sync.files.folder.semaphore.num",
            "40"));
        folderSyncSemaphore = new Semaphore(folderSyncSemaphoreNum);
    }
    
    public static synchronized SyncSemaphoreManager getInstance()
    {
        return new SyncSemaphoreManager();
    }
    
    public boolean tryBorrowRootSyncSemaphore() throws InterruptedException
    {
        return rootSyncSemaphore.tryAcquire(TIME_OUT, TimeUnit.SECONDS);
    }
    
    public void returnRootSyncSemaphore()
    {
        rootSyncSemaphore.release();
    }
    
    public boolean tryBorrowFolderSyncSemaphore() throws InterruptedException
    {
        return folderSyncSemaphore.tryAcquire(TIME_OUT, TimeUnit.SECONDS);
    }
    
    public void returnFolderSyncSemaphore()
    {
        folderSyncSemaphore.release();
    }
    
}
