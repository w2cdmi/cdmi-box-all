/**
 * 
 */
package com.huawei.sharedrive.app.openapi.domain.sync;

/**
 * @author q90003805
 * 
 */
public class SyncFolderRequest
{
    private long clientSyncVersion;
    
    private long userId;
    
    public long getClientSyncVersion()
    {
        return clientSyncVersion;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setClientSyncVersion(long clientSyncVersion)
    {
        this.clientSyncVersion = clientSyncVersion;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
}
