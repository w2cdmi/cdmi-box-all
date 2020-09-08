package com.huawei.sharedrive.app.openapi.domain.node;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

/**
 * 刷新文件上传地址请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-20
 * @see
 * @since
 */
public class RefreshUploadUrlRquest
{
    
    // 上传地址根据"/"分隔产生的最小数组长度
    private static final int UPLOADURL_SPLIT_MIN_LENGTH = 6;
    
    // 预上传地址
    private String uploadUrl;
    
    public RefreshUploadUrlRquest()
    {
        
    }
    
    public RefreshUploadUrlRquest(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(uploadUrl))
        {
            throw new InvalidParamException();
        }
        String[] array = uploadUrl.split("/");
        if (array.length < UPLOADURL_SPLIT_MIN_LENGTH)
        {
            throw new InvalidParamException("Invalid upload url");
        }
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
