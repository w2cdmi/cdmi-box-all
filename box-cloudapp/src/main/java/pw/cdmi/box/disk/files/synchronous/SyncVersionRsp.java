package pw.cdmi.box.disk.files.synchronous;

public class SyncVersionRsp
{
    private long currentSyncVersion;
    
    private String metadataSqlPath;
    
    public long getCurrentSyncVersion()
    {
        return currentSyncVersion;
    }
    
    public String getMetadataSqlPath()
    {
        return metadataSqlPath;
    }
    
    public void setCurrentSyncVersion(long currentSyncVersion)
    {
        this.currentSyncVersion = currentSyncVersion;
    }
    
    public void setMetadataSqlPath(String metadataSqlPath)
    {
        this.metadataSqlPath = metadataSqlPath;
    }
}
