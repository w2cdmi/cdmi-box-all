package pw.cdmi.file.engine.mirro.domain;

import java.io.Serializable;
import java.util.Date;

import pw.cdmi.file.engine.core.ibatis.Namingspace;

@Namingspace("CopyExeTime")
public class CopyExeTime  implements Serializable
{
   
    private static final long serialVersionUID = 6104004776677534666L;

    private String copyTaskId;
    
    private long size;
    
    private String exeAgent;
    
    private Date startTime;
    
    private Date endTime;
    
    private Date beginDown;
    
    private Date beginWrite;
    
    private Date endWrite;

    public String getCopyTaskId()
    {
        return copyTaskId;
    }

    public void setCopyTaskId(String copyTaskId)
    {
        this.copyTaskId = copyTaskId;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String getExeAgent()
    {
        return exeAgent;
    }

    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }

    public Date getStartTime()
    {
        if(null == this.startTime)
        {
            return null;
        }
        return (Date)this.startTime.clone();
    }

    public void setStartTime(Date startTime)
    {
        if(null != startTime)
        {
            this.startTime = (Date)startTime.clone();
        }
        else
        {
            this.startTime = null;
        }
    }

    public Date getEndTime()
    {
        if(null == this.endTime)
        {
            return null;
        }
        return (Date)this.endTime.clone();
    }

    public void setEndTime(Date endTime)
    {
        if(null != endTime)
        {
            this.endTime = (Date)endTime.clone();
        }
        else
        {
            this.endTime = null;
        }
    }

    public Date getBeginDown()
    {
        if(null == this.beginDown)
        {
            return null;
        }
        return (Date)this.beginDown.clone();
    }

    public void setBeginDown(Date beginDown)
    {
        if(null != beginDown)
        {
            this.beginDown = (Date)beginDown.clone();
        }
        else
        {
            this.beginDown = null;
        }
    }

    public Date getBeginWrite()
    {
        if(null == this.beginWrite)
        {
            return null;
        }
        return (Date)this.beginWrite.clone();
    }

    public void setBeginWrite(Date beginWrite)
    {
        if(null != beginWrite)
        {
            this.beginWrite = (Date)beginWrite.clone();
        }
        else
        {
            this.beginWrite = null;
        }
    }

    public Date getEndWrite()
    {
        if(null == this.endWrite)
        {
            return null;
        }
        return (Date)this.endWrite.clone();
    }

    public void setEndWrite(Date endWrite)
    {
        if(null != endWrite)
        {
            this.endWrite = (Date)endWrite.clone();
        }
        else
        {
            this.endWrite = null;
        }
    }
    
  
    
}
