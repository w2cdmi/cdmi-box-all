package com.huawei.sharedrive.app.plugins.preview.domain;

import java.util.Date;

public class PreviewObject
{
    /**
     * 正常状态,转换成功
     */
    public static final byte STATUS_NORMAIL = 0;
    
    /**
     * 创建状态,转换中
     */
    public static final byte STATUS_CREATING = 1;
    
    /**
     * 失败状态,转换失败
     */
    public static final byte STATUS_FAILED = 2;
    
    /**
     * 原始对象Id，关联object_reference表
     */
    private String sourceObjectId;
    
    /**
     * 预览对象对应accountId
     */
    private long accountId;
    
    /**
     * 预览转换任务开始时间,从ufm发起开始计算，用于任务超时比较
     */
    private Date convertStartTime;
    
    /**
     * 预览对象创建时间，为预览对象在worker上的实际转换开始时间，用于与水印设置时间比较
     */
    private Date createdAt;
    
    /**
     * 预览对象转换状态
     */
    private byte status;
    
    /**
     * 预览对象真实Id，关联dss存储的对象Id
     */
    private String storageObjectId;
    
    /**
     * 资源组ID
     */
    private int resourceGroupId;
    
    /**
     * 预览对象大小
     */
    private long size;
    
    /**
     * 预览对象md5
     */
    private String md5;
    
    /**
     * 预览对象block md5
     */
    private String blockMD5;
    
    /**
     * 分表信息
     */
    private int tableSuffix;
    
    public String getSourceObjectId()
    {
        return sourceObjectId;
    }
    
    public void setSourceObjectId(String sourceObjectId)
    {
        this.sourceObjectId = sourceObjectId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public Date getConvertStartTime()
    {
        if (convertStartTime == null)
        {
            return null;
        }
        return (Date) convertStartTime.clone();
    }
    
    public void setConvertStartTime(Date convertStartTime)
    {
        if (convertStartTime == null)
        {
            this.convertStartTime = null;
        }
        else
        {
            this.convertStartTime = (Date) convertStartTime.clone();
        }
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public String getStorageObjectId()
    {
        return storageObjectId;
    }
    
    public void setStorageObjectId(String storageObjectId)
    {
        this.storageObjectId = storageObjectId;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public void setResourceGroupId(int resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String getMd5()
    {
        return md5;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public String getBlockMD5()
    {
        return blockMD5;
    }
    
    public void setBlockMD5(String blockMD5)
    {
        this.blockMD5 = blockMD5;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
}
