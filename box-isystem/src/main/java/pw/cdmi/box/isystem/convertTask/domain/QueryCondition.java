package pw.cdmi.box.isystem.convertTask.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import pw.cdmi.box.domain.PageRequest;

/**
 * 查询条件
 */
public class QueryCondition
{
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    
    /**文件名称*/
    private String fileName;
    
    /** 分页参数 */
    private PageRequest pageRequest;
    
    public Date getStartTime()
    {
        if (this.startTime != null)
        {
            return new Date(this.startTime.getTime());
        }
        return null;
    }
    
    public void setStartTime(Date startTime)
    {
        if(startTime != null)
        {
            this.startTime = new Date(startTime.getTime());
        }
        else
        {
            this.startTime = null;
        }
    }
    
    public Date getEndTime()
    {
        if (this.endTime != null)
        {
            return new Date(this.endTime.getTime());
        }
        return null;
    }
    
    public void setEndTime(Date endTime)
    {
        if(endTime != null)
        {
            this.endTime = new Date(endTime.getTime());
        }
        else
        {
            this.endTime = null;
        }
    }

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
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
