package pw.cdmi.box.isystem.convertTask.domain;

import java.sql.Timestamp;

public class TaskBean
{
	private String taskId;
	
	private Integer level;
	
	private Integer status;
	
	private String resourceGroupId;
	
	private String csIp;
	
	private Timestamp convertTime;
	
	private int bigFileFlag;
	
	private Integer retryCount;
	
	private String fileName;

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getTaskId()
	{
		return taskId;
	}

	public void setTaskId(String taskId)
	{
		this.taskId = taskId;
	}

	public Integer getLevel()
	{
		return level;
	}

	public void setLevel(Integer level)
	{
		this.level = level;
	}

	public Integer getStatus()
	{
		return status;
	}

	public void setStatus(Integer status)
	{
		this.status = status;
	}

	public String getResourceGroupId()
	{
		return resourceGroupId;
	}

	public void setResourceGroupId(String resourceGroupId)
	{
		this.resourceGroupId = resourceGroupId;
	}

	public String getCsIp()
	{
		return csIp;
	}

	public void setCsIp(String csIp)
	{
		this.csIp = csIp;
	}

	public Timestamp getConvertTime()
	{
		return convertTime;
	}

	public void setConvertTime(Timestamp convertTime)
	{
		this.convertTime = convertTime;
	}

	public int getBigFileFlag()
	{
		return bigFileFlag;
	}

	public void setBigFileFlag(int bigFileFlag)
	{
		this.bigFileFlag = bigFileFlag;
	}

	public Integer getRetryCount()
	{
		return retryCount;
	}

	public void setRetryCount(Integer retryCount)
	{
		this.retryCount = retryCount;
	}
	
	
}
