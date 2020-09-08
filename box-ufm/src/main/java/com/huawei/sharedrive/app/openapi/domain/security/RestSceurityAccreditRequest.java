package com.huawei.sharedrive.app.openapi.domain.security;

import org.apache.commons.lang.ArrayUtils;

import com.huawei.sharedrive.app.plugins.scan.domain.SecurityStatus;

public class RestSceurityAccreditRequest
{
    /**
     * 访问的资源ID
     */
    private long iNodeId;
    
    /**
     * 文件夹类型 "0"表示文件夹，"1"表示文件 *
     */
    private byte type;
    
    /**
     * 被访问资源的Onwer
     */
    private long onwerCloudUserId;
    
    private long targetOwnerId;
    
    /**
     * 需要检查的权限值
     */
    private String[] permissions;
    
    /**
     * enum {NotScan(0),IsKia(1),IsNotKia(3);}
     */
    private int kiaStatus;
    
    public int getKiaStatus()
    {
        return kiaStatus;
    }
    
    public void setKiaStatus(int kiaStatus)
    {
        this.kiaStatus = kiaStatus;
    }
    
    public void setKiaStatusBySecurityStatus(SecurityStatus status)
    {
        if (null == status)
        {
            this.kiaStatus = 0;
            return;
        }
        switch (status)
        {
            case KIA_UMCOMPLETED:
                this.kiaStatus = 0;
                break;
            case KIA_COMPLETED_SECURE:
                this.kiaStatus = 3;
                break;
            case KIA_COMPLETED_INSECURE:
                this.kiaStatus = 1;
                break;
            default:
                this.kiaStatus = 0;
                break;
        }
    }
    
    /**
     * 访问的资源ID
     */
    public long getiNodeId()
    {
        return iNodeId;
    }
    
    /**
     * 访问的资源ID
     */
    public void setiNodeId(long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    /**
     * 文件夹类型 "0"表示文件夹，"1"表示文件 *
     */
    public byte getType()
    {
        return type;
    }
    
    /**
     * 文件夹类型 "0"表示文件夹，"1"表示文件 *
     */
    public void setType(byte type)
    {
        this.type = type;
    }
    
    /**
     * 被访问资源的Onwer
     */
    public long getOnwerCloudUserId()
    {
        return onwerCloudUserId;
    }
    
    /**
     * 被访问资源的Onwer
     */
    public void setOnwerCloudUserId(long onwerCloudUserId)
    {
        this.onwerCloudUserId = onwerCloudUserId;
    }
    
    /**
     * 需要检查的权限值
     */
    public String[] getPermissions()
    {
        if (permissions == null)
        {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return permissions.clone();
    }
    
    /**
     * 需要检查的权限值
     */
    public void setPermissions(String[] permissions)
    {
        if (permissions == null)
        {
            this.permissions = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        else
        {
            this.permissions = permissions.clone();
        }
    }
    
    public long getTargetOwnerId()
    {
        return targetOwnerId;
    }
    
    public void setTargetOwnerId(long targetOwnerId)
    {
        this.targetOwnerId = targetOwnerId;
    }
}
