/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.domain;

import java.io.Serializable;
import java.util.Date;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

@Namingspace("RealTimeCopyTaskPart")
public class RealTimeCopyTaskPart implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -5816226370279923780L;
    
    private String taskId;
    
    private String partId;
    
    private long size;
    
    private int status;
    
    private Date createTime;
    
    private Date modifyTime;
    
    private String partRange;
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public String getPartId()
    {
        return partId;
    }
    
    public void setPartId(String partId)
    {
        this.partId = partId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public Date getCreateTime()
    {
        if (null != this.createTime)
        {
            return (Date) this.createTime.clone();
        }
        return null;
    }
    
    public void setCreateTime(Date createTime)
    {
        if (null != createTime)
        {
            this.createTime = (Date) createTime.clone();
        }
        else
        {
            this.createTime = null;
        }
    }
    
    public Date getModifyTime()
    {
        if (null != this.modifyTime)
        {
            return (Date) this.modifyTime.clone();
        }
        return null;
    }
    
    public void setModifyTime(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifyTime = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifyTime = null;
        }
    }
    
    public String getPartRange()
    {
        return partRange;
    }
    
    public void setPartRange(String partRange)
    {
        this.partRange = partRange;
    }
    
}
