package com.huawei.sharedrive.app.openapi.domain.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilePreUploadResponseV1
{
    // 文件对象
    private RestFileInfo file;
    
    // 文件ID
    @JsonProperty(value = "fileID")
    private Long fileId;
    
    // 文件上传url
    private String url;
    
    public RestFileInfo getFile()
    {
        return file;
    }
    
    public Long getFileId()
    {
        return fileId;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setFile(RestFileInfo file)
    {
        this.file = file;
    }
    
    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
}
