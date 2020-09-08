package com.huawei.sharedrive.uam.enterpriseadminlog.domain;

import java.util.Date;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

public class QueryCondition
{
    
    private Long enterpriseId;
    
    private Long authServerId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    @Size(max = 1024)
    private String operatDesc;
    
    private Integer operateType;
    
    private int totalPage;
    
    private int pageNumber;
    
    private int pageSize;
    
    private PageRequest pageRequest;
    
    public Date getStartTime()
    {
        if (null != startTime)
        {
            return (Date) startTime.clone();
        }
        return null;
    }
    
    public void setStartTime(Date startTime)
    {
        if (null != startTime)
        {
            this.startTime = (Date) startTime.clone();
        }
        else
        {
            this.startTime = null;
        }
    }
    
    public Date getEndTime()
    {
        if (null != endTime)
        {
            return (Date) endTime.clone();
        }
        return null;
    }
    
    public void setEndTime(Date endTime)
    {
        if (null != endTime)
        {
            this.endTime = (Date) endTime.clone();
        }
        else
        {
            this.endTime = null;
        }
    }
    
    public int getTotalPage()
    {
        return totalPage;
    }
    
    public void setTotalPage(int totalPage)
    {
        if (totalPage % pageSize == 0)
        {
            this.totalPage = totalPage / pageSize;
        }
        else
        {
            this.totalPage = totalPage / pageSize + 1;
        }
        
    }
    
    public int getPageSize()
    {
        return pageSize;
    }
    
    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
    
    public int getPageNumber()
    {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }
    
    public PageRequest getPageRequest()
    {
        return pageRequest;
    }
    
    public void setPageRequest(PageRequest pageRequest)
    {
        this.pageRequest = pageRequest;
    }
    
    public String getOperatDesc()
    {
        return operatDesc;
    }
    
    public void setOperatDesc(String operatDesc)
    {
        this.operatDesc = operatDesc;
    }
    
    public Integer getOperateType()
    {
        return operateType;
    }
    
    public void setOperateType(Integer operateType)
    {
        this.operateType = operateType;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public Long getAuthServerId()
    {
        return authServerId;
    }
    
    public void setAuthServerId(Long authServerId)
    {
        this.authServerId = authServerId;
    }
    
}
