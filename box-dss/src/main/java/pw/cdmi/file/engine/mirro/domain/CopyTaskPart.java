/**
 * 
 */
package pw.cdmi.file.engine.mirro.domain;

import java.io.Serializable;
import java.util.Date;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * @author w00186884
 * 
 */
@Namingspace("CopyTaskPart")
public class CopyTaskPart implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -4101186259689626249L;
    
    private String taskId;
    
    private String partId;
    
    private long size;
    
    private int copyStatus;
    
    private String remark;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private int retryNum;
    
    private String partRange;
    
    public CopyTaskPart(){
    	
    }
    
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
    
    public int getCopyStatus()
    {
        return copyStatus;
    }
    
    public void setCopyStatus(int copyStatus)
    {
        this.copyStatus = copyStatus;
    }
    
    public String getRemark()
    {
        return remark;
    }
    
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    
    public Date getCreatedAt()
    {
        if(null != this.createdAt)
        {
            return (Date)this.createdAt.clone();
        }
        return null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if(null != createdAt)
        {
            this.createdAt = (Date)createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public Date getModifiedAt()
    {
        if(null != this.modifiedAt)
        {
            return (Date)this.modifiedAt.clone();
        }
        return null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if(null != modifiedAt)
        {
            this.modifiedAt = (Date)modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
    public int getRetryNum()
    {
        return retryNum;
    }
    
    public void setRetryNum(int retryNum)
    {
        this.retryNum = retryNum;
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
