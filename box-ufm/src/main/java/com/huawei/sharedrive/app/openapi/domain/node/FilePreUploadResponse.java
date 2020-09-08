package com.huawei.sharedrive.app.openapi.domain.node;

public class FilePreUploadResponse
{
    // 文件ID
    private Long fileId;
    
    // 文件上传url
    private String uploadUrl;
    
    public Long getFileId()
    {
        return fileId;
    }
    
    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }
    
    public String getUploadUrl()
    {
        return uploadUrl;
    }
    
    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
}
