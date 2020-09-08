package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;
/**
 * 扫描表停止时断点记录；只记录到获取批次，不精确到具体哪一行
 * @author cWX348274
 *
 */
public class TableScanBreakInfo
{
    private String sysTaskId;
    
    private Long limitOffset;
    
    private String model;
    
    private Integer length;
    
    private Date breakTime;
    
    private String outPut;
    

    
    public String getSysTaskId()
    {
        return sysTaskId;
    }
    
    public void setSysTaskId(String sysTaskId)
    {
        this.sysTaskId = sysTaskId;
    }
    
    public Long getLimitOffset()
    {
        return limitOffset;
    }
    
    public void setLimitOffset(Long limitOffset)
    {
        this.limitOffset = limitOffset;
    }
    
    public String getModel()
    {
        return model;
    }
    
    public void setModel(String model)
    {
        this.model = model;
    }
    
    public Integer getLength()
    {
        return length;
    }
    
    public void setLength(Integer length)
    {
        this.length = length;
    }
    
    public Date getBreakTime()
    {
        if (breakTime == null)
        {
            return null;
        }
        return (Date) breakTime.clone();
    }
    
    public void setBreakTime(Date breakTime)
    {
        if (breakTime == null)
        {
            this.breakTime = null;
        }
        else
        {
            this.breakTime = (Date) breakTime.clone();
        }
    }
    
    public String getOutPut()
    {
        return outPut;
    }
    
    public void setOutPut(String outPut)
    {
        this.outPut = outPut;
    }
    
}
