package com.huawei.sharedrive.uam.openapi.domain;


public class ControllerUserUpdateRequest extends RestUserUpdateRequest
{
    
    private String tagId;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    public String getTagId()
    {
        return tagId;
    }
    
    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }
    
    public Long getUploadBandWidth()
    {
        return uploadBandWidth;
    }
    
    public void setUploadBandWidth(Long uploadBandWidth)
    {
        this.uploadBandWidth = uploadBandWidth;
    }
    
    public Long getDownloadBandWidth()
    {
        return downloadBandWidth;
    }
    
    public void setDownloadBandWidth(Long downloadBandWidth)
    {
        this.downloadBandWidth = downloadBandWidth;
    }
    
}
