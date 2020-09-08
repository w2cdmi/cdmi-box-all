package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.Date;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.plugins.preview.domain.PreviewObject;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class PreviewObjectPreUploadRequest
{
    
    private Long accountId;
    
    private Long convertRealStartTime;
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public Long getConvertRealStartTime()
    {
        return convertRealStartTime;
    }
    
    public void setConvertRealStartTime(Long convertRealStartTime)
    {
        this.convertRealStartTime = convertRealStartTime;
    }
    
    public void checkParamter() throws BaseRunException
    {
        FilesCommonUtils.checkNonNegativeIntegers(accountId, convertRealStartTime);
    }
    
    public PreviewObject transToPreviewObject()
    {
        PreviewObject fileNode = new PreviewObject();
        if (accountId == null)
        {
            throw new InvalidParamException("accountId can not be null");
        }
        fileNode.setAccountId(accountId);
        if (convertRealStartTime == null)
        {
            throw new InvalidParamException("convertRealStartTime can not be null");
        }
        fileNode.setCreatedAt(new Date(convertRealStartTime));
        return fileNode;
    }
    
}
