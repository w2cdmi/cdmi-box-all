package pw.cdmi.box.app.convertservice.domain;

public class MonitorTaskBean
{
	private String localIP;
	
	private String localHostName;
	
	private String localIPAndHostName;
	
	private String currentTime;
	
	

	public MonitorTaskBean()
	{
		super();
	}
	

	public MonitorTaskBean(String localIP, String localHostName, String localIPAndHostName, String currentTime)
	{
		super();
		this.localIP = localIP;
		this.localHostName = localHostName;
		this.localIPAndHostName = localIPAndHostName;
		this.currentTime = currentTime;
	}



	public String getLocalIP()
	{
		return localIP;
	}

	public void setLocalIP(String localIP)
	{
		this.localIP = localIP;
	}

	public String getLocalHostName()
	{
		return localHostName;
	}

	public void setLocalHostName(String localHostName)
	{
		this.localHostName = localHostName;
	}

	public String getLocalIPAndHostName()
	{
		return localIPAndHostName;
	}

	public void setLocalIPAndHostName(String localIPAndHostName)
	{
		this.localIPAndHostName = localIPAndHostName;
	}

	public String getCurrentTime()
	{
		return currentTime;
	}

	public void setCurrentTime(String currentTime)
	{
		this.currentTime = currentTime;
	}


	@Override
	public String toString()
	{
		return "MonitorTaskBean [localIP=" + localIP + ", localHostName=" + localHostName + ", localIPAndHostName="
				+ localIPAndHostName + ", currentTime=" + currentTime + "]";
	}
	
	
	
}
