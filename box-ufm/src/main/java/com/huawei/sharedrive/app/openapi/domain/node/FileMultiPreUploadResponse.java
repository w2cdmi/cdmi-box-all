package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.List;

public class FileMultiPreUploadResponse
{
    // 文件需要对象列表
    private List<FilePreUploadResponseV2> uploadUrlList;
    
    // 文件闪传对象列表
    private List<RestFileInfo> uploadedList;
    
    // 文件预上传 失败 对象列表
    private List<FilePreUploadFailResponse> failedList;

    public List<FilePreUploadResponseV2> getUploadUrlList()
    {
        return uploadUrlList;
    }

    public void setUploadUrlList(List<FilePreUploadResponseV2> uploadUrlList)
    {
        this.uploadUrlList = uploadUrlList;
    }

    public List<RestFileInfo> getUploadedList()
    {
        return uploadedList;
    }

    public void setUploadedList(List<RestFileInfo> uploadedList)
    {
        this.uploadedList = uploadedList;
    }

    public List<FilePreUploadFailResponse> getFailedList()
    {
        return failedList;
    }

    public void setFailedList(List<FilePreUploadFailResponse> failedList)
    {
        this.failedList = failedList;
    }
    
    
}
