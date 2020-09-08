package pw.cdmi.box.uam.log.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

public class UserLogQueryCondition
{
    private int firstType = -1;
    
    private int secondType = -1;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    private String admin;
    
    private String operater;
    
    private String detail;
    
    private PageRequest pageRequest;
    
    private long adminId;
    
    private String appId;
    
    private String firstCategory;
    
    private String secondCategory;
    
    private int status = -1;
    
    public int getFirstType()
    {
        return firstType;
    }
    
    public void setFirstType(int firstType)
    {
        this.firstType = firstType;
    }
    
    public int getSecondType()
    {
        return secondType;
    }
    
    public void setSecondType(int secondType)
    {
        this.secondType = secondType;
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
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getFirstCategory()
    {
        return firstCategory;
    }
    
    public void setFirstCategory(String firstCategory)
    {
        this.firstCategory = firstCategory;
    }
    
    public String getSecondCategory()
    {
        return secondCategory;
    }
    
    public void setSecondCategory(String secondCategory)
    {
        this.secondCategory = secondCategory;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public String getDetail()
    {
        return detail;
    }
    
    public void setDetail(String detail)
    {
        this.detail = detail;
    }
    
    public void resetTypeValue()
    {
        UserLogCategory firstLogCategory = UserLogCategory.valueOf(firstCategory);
        UserLogCategory secondLogCategory = UserLogCategory.valueOf(secondCategory);
        this.firstType = firstLogCategory.getSelfId();
        this.secondType = secondLogCategory.getSelfId();
    }
}
