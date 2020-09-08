package pw.cdmi.box.uam.enterpriseradminlog.domain;

import java.util.Date;

import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

public class QueryCondition
{
    
    private Long enterpriseId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    @Size(max = 1024)
    private String operatDesc;
    
    private int totalPage;
    
    private int pageNumber;
    
    private int pageSize;
    
    private PageRequest pageRequest;
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
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
    
}
