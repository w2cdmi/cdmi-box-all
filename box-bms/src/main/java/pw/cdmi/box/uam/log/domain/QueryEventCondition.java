package pw.cdmi.box.uam.log.domain;

import java.util.Date;

import pw.cdmi.box.domain.PageRequest;

public class QueryEventCondition
{
    private String type;
    
    private Date startTime;
    
    private Date endTime;
    
    private String admin;
    
    private PageRequest pageRequest;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
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
    
    public String getAdmin()
    {
        return admin;
    }
    
    public void setAdmin(String admin)
    {
        this.admin = admin;
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
