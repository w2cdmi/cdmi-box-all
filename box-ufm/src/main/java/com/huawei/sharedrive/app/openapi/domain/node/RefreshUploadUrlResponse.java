package com.huawei.sharedrive.app.openapi.domain.node;

/**
 * 刷新文件上传地址响应对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-20
 * @see
 * @since
 */
public class RefreshUploadUrlResponse
{
    // 上传地址
    private String uploadUrl;
    
    public RefreshUploadUrlResponse()
    {
        
    }
    
    public RefreshUploadUrlResponse(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
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
