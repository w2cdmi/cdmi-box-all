package com.huawei.sharedrive.app.openapi.domain.node;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.core.utils.TrimUtils;

public class RenameAndSetSyncRequest implements Serializable
{
    
    private static final long serialVersionUID = -2972789619446431458L;
    
    private String name;
    
    // true - 设置为同步状态 false - 取消同步设置
    private Boolean syncStatus;
    
    private String extraType;
    
    public void checkParameter() throws InvalidParamException
    {
        if (name != null)
        {
            FilesCommonUtils.checkNodeNameVaild(name);
        }
    }
    
    public String getName()
    {
        return name;
    }
    
    public Boolean getSyncStatus()
    {
        return syncStatus;
    }
    
    public void setName(String name)
    {
        this.name = StringUtils.isNotBlank(name) ? TrimUtils.trimToEmpty(name) : name;
    }
    
    public void setSyncStatus(Boolean syncStatus)
    {
        this.syncStatus = syncStatus;
    }
    
    public String getExtraType()
    {
        return extraType;
    }
    
    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }
    
}
