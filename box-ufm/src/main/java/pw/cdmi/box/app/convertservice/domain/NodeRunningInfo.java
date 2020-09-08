package pw.cdmi.box.app.convertservice.domain;

/**
 * 节点监控
 * 
 * @author l00357199 20162016-1-5 下午5:36:52
 */
public class NodeRunningInfo
{
	public static  final int STATUS_NORMAL = 0; 
	
	public static  final int STATUS_ERROR = 1; 
	//集群名称
    private String clusterName;
    
    //集群ID
    private int clusterID;

	//节点名称，即主机名
    private String hostName;
    
    //节点IP地址，用于访问convertservice
    private String hostIP;

    // 页面显示的系统集群名称，如Converservice-1，Converservice-2
    private String systemName;
    
    /*
     * 特別注意這兩個狀態的值，都是以0做為正常，1為異常。
     */
    // 节点状态：預覽服務器節點狀態
	private int status  = STATUS_ERROR;
	
	// dss節點狀態
	private int dssRuntimeStatus = STATUS_ERROR;
	
	// 资源组ID，和hostname一起为联合主键
	private int resourceGroupID;
	
	private boolean sendAlarm = false;
    
    public NodeRunningInfo() {
    }
    
	public NodeRunningInfo(String clusterName, int clusterID, String hostName, String hostIP, String systemName,
			int status,int resourceGroupID) {
		super();
		this.clusterName = clusterName;
		this.clusterID = clusterID;
		this.hostName = hostName;
		this.hostIP = hostIP;
		this.systemName = systemName;
		this.status = status;
		this.resourceGroupID = resourceGroupID;
	}

	public String getSystemName() {
		return systemName;
	}


	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public int getClusterID() {
  		return clusterID;
  	}

  	public void setClusterID(int clusterID) {
  		this.clusterID = clusterID;
  	}
  	
    public String getClusterName()
    {
        return clusterName;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }
    
    public String getHostName()
    {
        return hostName;
    }
    
    
    public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
	}

	public int getResourceGroupID() {
		return resourceGroupID;
	}

	public void setResourceGroupID(int resourceGroupID) {
		this.resourceGroupID = resourceGroupID;
	}

	@Override
	public String toString() {
		return "NodeRunningInfo [clusterName=" + clusterName + ", clusterID=" + clusterID + ", hostName=" + hostName
				+ ", hostIP=" + hostIP + ", systemName=" + systemName + ", status=" + status + ", resourceGroupID="
				+ resourceGroupID + "]";
	}

	public int getDssRuntimeStatus() {
		return dssRuntimeStatus;
	}

	public void setDssRuntimeStatus(int dssRuntimeStatus) {
		this.dssRuntimeStatus = dssRuntimeStatus;
	}
	
    
    public boolean isSendAlarm()
    {
        return sendAlarm;
    }

    public void setSendAlarm(boolean isSendAlarm)
    {
        this.sendAlarm = isSendAlarm;
    }
}
