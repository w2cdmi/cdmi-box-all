package pw.cdmi.box.uam.adminlog.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

public class QueryCondition
{
    private String appId;
    
    private int operateType = -1;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    private String admin;
    
    private String operater;
    
    private PageRequest pageRequest;
    
    private long adminId;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public int getOperateType()
    {
        return operateType;
    }
    
    public void setOperateType(int operateType)
    {
        this.operateType = operateType;
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
    
    public String getOperater()
    {
        return operater;
    }
    
    public void setOperater(String operater)
    {
        this.operater = operater;
    }
    
    public long getAdminId()
    {
        return adminId;
    }
    
    public void setAdminId(long adminId)
    {
        this.adminId = adminId;
    }
    
}
