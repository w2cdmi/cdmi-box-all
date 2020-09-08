/**
 * 
 */
package pw.cdmi.file.engine.mirro.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

/**
 * @author w00186884
 * 
 */
@Namingspace("CopyTask")
public class CopyTask implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -4753593838527396627L;
    
    private String taskId;
    
    private String srcObjectId;
    
    private long size;
    
    private String destObjectId;
    
    private int priority;
    
    /**
     * 执行复制状态
     */
    private int copyStatus;
    
    /**
     * 执行节点
     */
    private String exeAgent;
    
    /**
     * 备注信息
     */
    private String remark;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private int retryNum;
    
    private String md5;
    
    private String blockMD5;
    
    private Integer errorCode;
    
    private List<CopyTaskPart> taskPart;
    
    public String getTaskId()
    {
        return taskId;
    }
    
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
    
    public String getSrcObjectId()
    {
        return srcObjectId;
    }
    
    public void setSrcObjectId(String srcObjectId)
    {
        this.srcObjectId = srcObjectId;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String getDestObjectId()
    {
        return destObjectId;
    }
    
    public void setDestObjectId(String destObjectId)
    {
        this.destObjectId = destObjectId;
    }
    
    public int getPriority()
    {
        return priority;
    }
    
    public void setPriority(int priority)
    {
        this.priority = priority;
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
        if(null == this.createdAt)
        {
            return null;
        }
        return (Date)this.createdAt.clone();
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
    
    public String getExeAgent()
    {
        return exeAgent;
    }
    
    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }
    
    public int getRetryNum()
    {
        return retryNum;
    }
    
    public void setRetryNum(int retryNum)
    {
        this.retryNum = retryNum;
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
    
    public Integer getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode)
    {
        this.errorCode = errorCode;
    }

    public List<CopyTaskPart> getTaskPart()
    {
        return taskPart;
    }
    
    public void setTaskPart(List<CopyTaskPart> taskPart)
    {
        this.taskPart = taskPart;
    }

    @Override
    public String toString()
    {
        return "CopyTask [taskId=" + taskId + ", srcObjectId=" + srcObjectId + ", size=" + size + ", destObjectId=" + destObjectId + ", priority=" + priority + ", copyStatus=" + copyStatus + ", exeAgent=" + exeAgent + ", remark=" + remark + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", retryNum="
            + retryNum + ", md5=" + md5 + ", blockMD5=" + blockMD5 + ", errorCode=" + errorCode + "]";
    }
    
    
}
