package com.huawei.sharedrive.app.mirror.appdatamigration.domain;

import java.util.Date;

/**
 * 记录每次数据迁移任务的当前进度 APP数据迁移 包含 1、源DSS只读
 * 2、计算需要迁移的数据，HistoryDataMigrationProcessInfo就是来计算每次扫描需要迁移的数据
 * 3、逐一扫描数据，逐一文件迁移，迁移失败的数据放在NeverCopyContent中 4、迁移一文件，就更新一个进度 5、这次迁移任务完成后，进行下轮扫描迁移
 * 6、多次扫描迁移后，无数据迁移则认为迁移完成
 * 
 * @author c00287749
 *        
 */
public class MigrationProcessInfo
{
    
    // 结束状态
    public static final int STATUS_COMPLETED = 1;
    
    // 运行状态
    public static final int STATUS_RUNNING = 0;
    
    private String id;
    
    private Date createdAt;
    
    private int policyId;
    
    private int status;
    
    private long totalFiles;
    
    private long totalSizes;
    
    private long curFiles;
    
    private long curSizes;
    
    private long failedFiles;
    
    private long failedSizes;
    
    private Date modifiedAt;
    
    private Date endTime;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getCreatedAt()
    {
        if(this.createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }

    public void setCreatedAt(Date createdAt)
    {
        if(createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }

    public int getPolicyId()
    {
        return policyId;
    }

    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

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

    public long getFailedFiles()
    {
        return failedFiles;
    }

    public void setFailedFiles(long failedFiles)
    {
        this.failedFiles = failedFiles;
    }

    public long getFailedSizes()
    {
        return failedSizes;
    }

    public void setFailedSizes(long failedSizes)
    {
        this.failedSizes = failedSizes;
    }

    public Date getModifiedAt()
    {
        if(this.modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }

    public void setModifiedAt(Date modifiedAt)
    {
        if(modifiedAt == null)
        {
            this.modifiedAt=null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }

    public Date getEndTime()
    {
        if(this.endTime == null)
        {
            return null;
        }
        return (Date) endTime.clone();
    }

    public void setEndTime(Date endTime)
    {
        if(endTime == null)
        {
            this.endTime = null;
        }
        else
        {
            this.endTime = (Date) endTime.clone();
        }
    }
}
