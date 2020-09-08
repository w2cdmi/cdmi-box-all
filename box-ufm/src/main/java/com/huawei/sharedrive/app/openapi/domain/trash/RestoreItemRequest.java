package com.huawei.sharedrive.app.openapi.domain.trash;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

/**
 * 恢复回收站资源请求对象
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-17
 * @see  
 * @since  
 */
public class RestoreItemRequest
{
    
    private Boolean autoRename;
    
    private Long destFolderId;
    
    public RestoreItemRequest()
    {
        this.autoRename = true;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (destFolderId != null)
        {
            FilesCommonUtils.checkNonNegativeIntegers(destFolderId);
        }
    }
    
    public Boolean getAutoRename()
    {
        return autoRename;
    }
    
    public Long getDestFolderId()
    {
        return destFolderId;
    }
    
    public void setAutoRename(Boolean autoRename)
    {
        this.autoRename = autoRename;
    }
    
    public void setDestFolderId(Long destFolderId)
    {
        this.destFolderId = destFolderId;
    }
    
}
