package com.huawei.sharedrive.app.openapi.domain.node;

public class ObjectDownloadResponse
{
    private String downloadUrl;
    
    private long size;
    
    public ObjectDownloadResponse()
    {
        
    }
    
    public ObjectDownloadResponse(String downloadUrl, long size)
    {
        this.downloadUrl = downloadUrl;
        this.size = size;
    }
    
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
}
