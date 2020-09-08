package com.huawei.sharedrive.app.openapi.domain.node;

public class DownloadResponse
{
    private String downloadUrl;
    
    public DownloadResponse()
    {
        
    }
    
    public DownloadResponse(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    
}
