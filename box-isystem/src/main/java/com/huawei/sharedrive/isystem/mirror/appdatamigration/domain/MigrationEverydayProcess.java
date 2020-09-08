package com.huawei.sharedrive.isystem.mirror.appdatamigration.domain;

import java.util.Date;

/**
 * 每天的每天的数据迁移进度
 * 
 * @author c00287749
 *        
 */
public class MigrationEverydayProcess
{
    private String id;
    
    private String parentId;
    
    private int policyId;
    
    private Date startTime;
    
    private Date endTime;
    
    private long newAddFiles;
    
    private long newAddSizes;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public int getPolicyId()
    {
        return policyId;
    }

    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }

    public Date getStartTime()
    {
        if(this.startTime == null)
        {
            return null;
        }
        return (Date) startTime.clone();
    }

    public void setStartTime(Date startTime)
    {
        if(startTime == null)
        {
            this.startTime= null;
        }
        else
        {
            this.startTime = (Date) startTime.clone();
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
            this.endTime= null;
        }
        else
        {
            this.endTime = (Date) endTime.clone();
        }
    }

    public long getNewAddFiles()
    {
        return newAddFiles;
    }

    public void setNewAddFiles(long newAddFiles)
    {
        this.newAddFiles = newAddFiles;
    }

    public long getNewAddSizes()
    {
        return newAddSizes;
    }

    public void setNewAddSizes(long newAddSizes)
    {
        this.newAddSizes = newAddSizes;
    }
    
}
