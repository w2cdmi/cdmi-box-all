package pw.cdmi.box.isystem.monitor.domain;

import java.util.List;

/**
 * 节点监控
 * 
 * @author l00357199 20162016-1-5 下午5:36:52
 */
public class DealedCSNodeRunningInfo
{
	//集群名称
    private String clusterName;
    
    //集群ID
    private int clusterID;

	//节点名称，即主机名
    private List<CSNodeRunningInfo> cSNodeRunningInfos;
    
    //页面显示的系统集群名称，如Converservice-1，Converservice-2
    private String systemName;
    
    //系统状态
	private int status;
    
    public DealedCSNodeRunningInfo()
    {
        
    }
    
	public DealedCSNodeRunningInfo(String clusterName, int clusterID, List<CSNodeRunningInfo> cSNodeRunningInfos,
			String systemName, int status)
	{
		super();
		this.clusterName = clusterName;
		this.clusterID = clusterID;
		this.cSNodeRunningInfos = cSNodeRunningInfos;
		this.systemName = systemName;
		this.status = status;
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

	public List<CSNodeRunningInfo> getcSNodeRunningInfos()
	{
		return cSNodeRunningInfos;
	}

	public void setcSNodeRunningInfos(List<CSNodeRunningInfo> cSNodeRunningInfos)
	{
		this.cSNodeRunningInfos = cSNodeRunningInfos;
	}

	@Override
	public String toString()
	{
		return "DealedCSNodeRunningInfo [clusterName=" + clusterName + ", clusterID=" + clusterID
				+ ", cSNodeRunningInfos=" + cSNodeRunningInfos + ", systemName=" + systemName + ", status=" + status
				+ "]";
	}
}
