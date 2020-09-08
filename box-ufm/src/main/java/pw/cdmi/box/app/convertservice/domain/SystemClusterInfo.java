package pw.cdmi.box.app.convertservice.domain;

public class SystemClusterInfo
{
	// 界面显示的系统集群名称，如Converservice-1，Converservice-2
    private String systemName;
    
    // 集群名称
    private String clusterName;
    
    // 集群状态
    private int status;
    
    public String getSystemName()
    {
        return systemName;
    }
    
    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }
    
    public String getClusterName()
    {
        return clusterName;
    }
    
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
}
