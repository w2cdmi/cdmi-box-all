package pw.cdmi.box.uam.adminlog.domain;

import java.util.Date;

import pw.cdmi.box.domain.PageRequest;

public class TerminalQueryCondition
{
    private Integer deviceType = -1;
    
    private Date startTime;
    
    private Date endTime;
    
    private PageRequest pageRequest;
    
    public Integer getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(Integer deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public Date getStartTime()
    {
        return startTime == null ? null : (Date) startTime.clone();
    }
    
    public void setStartTime(Date startTime)
    {
        this.startTime = (startTime == null ? null : (Date) startTime.clone());
    }
    
    public Date getEndTime()
    {
        return endTime == null ? null : (Date) endTime.clone();
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = (endTime == null ? null : (Date) endTime.clone());
    }
    
    public PageRequest getPageRequest()
    {
        return pageRequest;
    }
    
    public void setPageRequest(PageRequest pageRequest)
    {
        this.pageRequest = pageRequest;
    }
    
}
