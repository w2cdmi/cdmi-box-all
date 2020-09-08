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
import java.util.List;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

@Namingspace("RealTimeCopyTask")
public class RealTimeCopyTask implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4722007570646853172L;

    private String taskId;
     
    private long nodeSize;   
    
    /**
     * 触发类型
     */
    private int triggerType;    
    
    /**
     * 执行复制状态
     */
    private int status;
    
    private Date createTime;
    
    private Date modifyTime;
    
    private String srcObjectId;
     
    private String destObjectId; 
    
    /**
     * 执行节点
     */
    private String exeAgent;
    
    private String md5;
    
    private String blockMD5;
    
    private Integer errorCode;
  
    private int retryNum;
     
    private List<RealTimeCopyTaskPart> taskPart;
    
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
       
    public String getDestObjectId()
    {
        return destObjectId;
    }
    
    public void setDestObjectId(String destObjectId)
    {
        this.destObjectId = destObjectId;
    }
       
    public Date getCreateTime()
    {
        if (null == this.createTime)
        {
            return null;
        }
        return (Date) this.createTime.clone();
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
    
    public void setModifyTime(Date modifyTime)
    {
        if (null != modifyTime)
        {
            this.modifyTime = (Date) modifyTime.clone();
        }
        else
        {
            this.modifyTime = null;
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
    
    public List<RealTimeCopyTaskPart> getTaskPart()
    {
        return taskPart;
    }
    
    public void setTaskPart(List<RealTimeCopyTaskPart> taskPart)
    {
        this.taskPart = taskPart;
    }
    
    public long getNodeSize()
    {
        return nodeSize;
    }

    public void setNodeSize(long nodeSize)
    {
        this.nodeSize = nodeSize;
    }

    public int getTriggerType()
    {
        return triggerType;
    }

    public void setTriggerType(int triggerType)
    {
        this.triggerType = triggerType;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }    
}
