package com.huawei.sharedrive.app.mirror.datamigration.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author c00287749
 * 
 */
public class UserDataMigrationTask implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -667436911006679695L;

    public final static int COMPELETE_STATUS = 3;
    
    public final static int EXECUTE_SCAN_STATUS = 1;
    
    public final static int EXECUTE_MIGRATION_STATUS = 2;
    
    public final static int FAILED_STATUS = 4;
    
    public final static int INIT_STATUS = 0;
    
    public static final String CACHE_KEY_PREFIX_ID = "user_data_migration_task_id";
    
    //如果发现对应的对象，没有数据，也记录缓存
    public static final String CACHE_KEY_PREFIX_NOT_EXISTING_ID = "user_data_migration_task_not_existing_id";
    
    //常用字符串
    public static final String CACHED_NOT_EXISTING = "not_existing";
    
    // 需要迁移资源ID
    private long cloudUserId;
    
    // 任务创建时间
    private Date createdAt;
    
    // 默认存储区域
    private int defaultRegionId;
    
    // 目标区域ID
    private int destRegionId;
    
    // 目标资源ID
    private int destResourceGroupId;
    
    // 任务修改时间
    private Date modifiedAt;
    
    // 任务状态
    private int status;
    
    // 执行Agent
    private String exeAgent;
    
    // 总文件或者版本数
    private long totalFiles;
    
    // 总文件大小数
    private long totalSizes;
    
    // 已经迁移的当前文件数
    private long curFiles;
    
    // 已经迁移的当前文件大小数
    private long curSizes;
    
    public long getTotalFiles()
    {
        return totalFiles;
    }
    
    public void setTotalFiles(long totalFiles)
    {
        this.totalFiles = totalFiles;
    }
    
    public long getTotalSizes()
    {
        return totalSizes;
    }
    
    public void setTotalSizes(long totalSizes)
    {
        this.totalSizes = totalSizes;
    }
    
    public long getCurFiles()
    {
        return curFiles;
    }
    
    public void setCurFiles(long curFiles)
    {
        this.curFiles = curFiles;
    }
    
    public long getCurSizes()
    {
        return curSizes;
    }
    
    public void setCurSizes(long curSizes)
    {
        this.curSizes = curSizes;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public int getDefaultRegionId()
    {
        return defaultRegionId;
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
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
    
    public void setDefaultRegionId(int defaultRegionId)
    {
        this.defaultRegionId = defaultRegionId;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public String getExeAgent()
    {
        return exeAgent;
    }
    
    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }
    
    public String toTaskStr()
    {
        return  "cloudUserId:"+cloudUserId+",totalFiles:"+totalFiles +",totalSizes:"+totalSizes+",curFiles:"+curFiles+",curSizes:"+curSizes;
    }
    
}
