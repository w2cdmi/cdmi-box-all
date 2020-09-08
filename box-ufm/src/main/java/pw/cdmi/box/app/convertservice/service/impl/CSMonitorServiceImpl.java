package pw.cdmi.box.app.convertservice.service.impl;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

import pw.cdmi.box.app.convertservice.dao.ResourceGroupNodeDao;
import pw.cdmi.box.app.convertservice.domain.MonitorTaskBean;
import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;
import pw.cdmi.box.app.convertservice.service.CSMonitorService;
import pw.cdmi.box.app.convertservice.util.ConvertPropertiesUtils;
import pw.cdmi.core.zk.ZookeeperServer;

@Component("cSMonitorService")
public class CSMonitorServiceImpl implements CSMonitorService
{
	private static Logger logger = LoggerFactory.getLogger(CSMonitorServiceImpl.class);

	/**
	 * 缓存根路径, 结尾不加/
	 */
	private static String convertserviceCaches = "/ISYSTEM-CONVERTSERVICE";
	
	private static String CONVERTSERVICETASKROOTPATH= "/CONVERTSERVICETASKPATH";
	
	private static String CONVERTSERVICETASKPATH= "/CONVERTSERVICETASKPATH/tasknode";
	

	@Resource(name = "zookeeperServer")
	private ZookeeperServer zookeeperServer;

	private CuratorFramework zkClient;
	
	@Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
	
	@PostConstruct
	public void init()
	{
		try
		{
			zkClient = zookeeperServer.getClient();
			Stat stat = zkClient.checkExists().forPath(convertserviceCaches);
			if (stat == null)
			{
				zkClient.create().forPath(convertserviceCaches);
			}
		} catch (Exception e)
		{
			logger.error("init CSMonitorServiceImpl fail!", e);
		}
	}

	public void setZookeeperServer(ZookeeperServer zookeeperServer)
	{
		this.zookeeperServer = zookeeperServer;
	}

	private void checkClusterOrNode(String nodeName)
	{
		boolean isExist = false;
		try
		{
			List<String> childrens = zkClient.getChildren().forPath(convertserviceCaches);
			if (childrens != null)
			{
				logger.info("the childrens size of /ISYSTEM-CONVERTSERVICE childrens :" + childrens.size());
				for (String children : childrens)
				{
					if (nodeName.equals(children))
					{
						isExist = true;
						break;
					}
				}
			}
			if (!isExist)
			{
				logger.info("create node :" + convertserviceCaches + "/" + nodeName);
				zkClient.create().forPath(convertserviceCaches + "/" + nodeName);
			}
		} catch (Exception e)
		{
			logger.error("failed to get childrens", e);
		}
	}

	

	@Override
	public void setOrUpdateNodeStatus(NodeRunningInfo nodeRunningInfo)
	{
		if (nodeRunningInfo != null)
		{
			setHostData(nodeRunningInfo);
		}
	}

	/*
	 * 獲取sysdb庫resource_group_node中對應的記錄，
	 * 并寫入zookeeper /ISYSTEM-CONVERTSERVICE/【guoz-PC_37】/目錄下
	 * 其中【guoz-PC_37】為resource_group_node表中name的值
	 */
	private void setHostData(NodeRunningInfo nodeRunningInfo)
	{
		String hostname = nodeRunningInfo.getHostName();
		int clusterID = nodeRunningInfo.getClusterID();
		String systemName = ConvertPropertiesUtils.getProperty("convertservice.clusterid." + clusterID, "convertservice-" + clusterID, ConvertPropertiesUtils.BundleName.CONVERT);
		nodeRunningInfo.setSystemName(systemName);
		int resourceGroupID = nodeRunningInfo.getResourceGroupID();
		String nodeName = new StringBuilder().append(hostname).append("_").append(resourceGroupID).toString();
		String path = convertserviceCaches + "/" + nodeName;
		logger.info("path："+ path);
		checkClusterOrNode(nodeName);

		try
		{
			zkClient.setData().forPath(path, new ObjectMapper().writeValueAsBytes(nodeRunningInfo));
		} catch (Exception e)
		{
			logger.error("failed to set data", e);
		}
	}

	/**
	 * 
	 * @Title: getNormalNodes
	 * @Description:獲取正常的dss節點
	 * dss是否正常是通過dss-thrift接口調用完成。common包中MonitorLocalCacheProducer.java-dotask方法
	 * 方法由job啟動在sysdb.system_job_def中有monitorLocalCacheProducerjob
	 * TODO:guoz 由于是job方式默認是5分鐘執行一次，所以這里有可能記錄到到已死的dss節點為正常，導致預覽轉換失敗。后期要優化
	 * 
	 */
	@Override
	public List<NodeRunningInfo> getNormalNodes(String resourceGroupID)
	{
		List<NodeRunningInfo> normalNodeList = new ArrayList<NodeRunningInfo>();
		List<NodeRunningInfo> nodeRunningInfos = getAllHostInfo();
		for (NodeRunningInfo nodeRunningInfo :nodeRunningInfos)
		{
		    int resourceID = nodeRunningInfo.getResourceGroupID();
		    int status = nodeRunningInfo.getStatus() ;
		    int dssRuntimeStatus =nodeRunningInfo.getDssRuntimeStatus();
		    
		    if (NodeRunningInfo.STATUS_NORMAL == status && resourceGroupID.equals(""+resourceID)  && dssRuntimeStatus == NodeRunningInfo.STATUS_NORMAL)
            {
                normalNodeList.add(nodeRunningInfo);
            }
		}
		logger.info("alive NodeList: " + normalNodeList);
		return normalNodeList;
	}
	
	/**
     * 从zookeeper中获取所有的节点的原始信息集合
     * 
     * @return 节点信息集合
     */
    private List<NodeRunningInfo> getAllHostInfo()
    {
        List<NodeRunningInfo> nodeRunningInfos = new ArrayList<NodeRunningInfo>();
        try
        {
            // 根目录下所有节点的集合
            List<String> hostNames = zkClient.getChildren().forPath(convertserviceCaches);
            for (String hostname : hostNames)
            {
                ObjectMapper obj = new ObjectMapper();
                byte[] writeValueAsBytes = zkClient.getData().forPath(convertserviceCaches + "/" + hostname);
                //将获取到的每个节点的byte[] 数据转换为对象
                NodeRunningInfo nodeRunningInfo = obj.readValue(writeValueAsBytes, NodeRunningInfo.class);
                nodeRunningInfos.add(nodeRunningInfo);
            }
        } catch (Exception e)
        {
            logger.error("failed to get original node list!", e);
        }
        logger.info("zookeeper /ISYSTEM-CONVERTSERVICE childrens: " + nodeRunningInfos.size());
        return nodeRunningInfos;
    }
	
	private String getClusterName(int clusterID)
	{
		return "ConvertService Cluster-" + clusterID;
	}
	
	

	@Override
	public List<NodeRunningInfo> getAllCSNodeInfo()
	{
		List<NodeRunningInfo>  nodeRunningInfos = new ArrayList<NodeRunningInfo>();
		List<ResourceGroupNode> resourceGroupNodes = resourceGroupNodeDao.getResourceGroupNodes();
		NodeRunningInfo nodeRunningInfo = null;
		for (ResourceGroupNode resourceGroupNode : resourceGroupNodes)
		{
			nodeRunningInfo = new NodeRunningInfo();
			nodeRunningInfo.setClusterID(resourceGroupNode.getDcId());
			nodeRunningInfo.setHostIP(resourceGroupNode.getManagerIp());
			nodeRunningInfo.setHostName(resourceGroupNode.getName());
			nodeRunningInfo.setClusterName(getClusterName(resourceGroupNode.getDcId()));
			nodeRunningInfo.setResourceGroupID(resourceGroupNode.getResourceGroupId());
			nodeRunningInfo.setDssRuntimeStatus(resourceGroupNode.getRuntimeStatus().getCode());//這里獲取的是dss節點狀態
			nodeRunningInfos.add(nodeRunningInfo);
		}
		logger.info("convertService nodes infos [came form sysdb.resource_group_node] :" + nodeRunningInfos);
		return nodeRunningInfos;
	}

	@Override
	public void getTask()
	{
		checkTaskNode();
		ObjectMapper om = new ObjectMapper();
		//本机IP和主机名的组合 
		String localIPAndHostName = getLocalIPAndHostName();
		//当前时间
		String currentTime = getCurrentTime();
		//本机IP
		String localIP = getLocalIP();
		//主机名
		String localHostName = getHostName();
		//配置的时间（毫秒），如果查询到的
		int configTimeMillisecond = Integer.parseInt(ConvertPropertiesUtils.getProperty("convertservice.server.task.time", "300000", ConvertPropertiesUtils.BundleName.CONVERT));
		MonitorTaskBean newMonitorTaskBean = new MonitorTaskBean();
		newMonitorTaskBean.setCurrentTime(currentTime);
		newMonitorTaskBean.setLocalIPAndHostName(localIPAndHostName);
		newMonitorTaskBean.setLocalIP(localIP);
		newMonitorTaskBean.setLocalHostName(localHostName);
		byte[] writeValueAsBytes = null;
		try
		{
			writeValueAsBytes = om.writeValueAsBytes(newMonitorTaskBean);
		} catch (JsonProcessingException e)
		{
			logger.error("failed to transport obj to byte");
		}
		
		try
		{
			ObjectMapper obj = new ObjectMapper();
			byte[] oldByte = zkClient.getData().forPath(CONVERTSERVICETASKPATH);
			logger.info("oldByte = " + new String(oldByte));
			//将获取到的每个节点的byte[] 数据转换为对象
			MonitorTaskBean oldMonitorTaskBean = obj.readValue(oldByte, MonitorTaskBean.class);
			
			logger.info("oldMonitorTaskBean = " + oldMonitorTaskBean.toString() + "newMonitorTaskBean = " + newMonitorTaskBean.toString());
			if (oldMonitorTaskBean.getLocalIPAndHostName().equals(localIPAndHostName))
			{
				zkClient.setData().forPath(CONVERTSERVICETASKPATH,writeValueAsBytes);
			}
			else
			{
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date now = df.parse(currentTime);
				Date date=df.parse(oldMonitorTaskBean.getCurrentTime());
				long Millisecond=now.getTime()-date.getTime();
				if (Millisecond > configTimeMillisecond)
				{
					zkClient.setData().forPath(CONVERTSERVICETASKPATH,writeValueAsBytes);
				}
				else
				{
					Thread.sleep(configTimeMillisecond);
					getTask();
				}
			}
		}
		catch(JsonProcessingException e)
		{
			try
			{
				zkClient.setData().forPath(CONVERTSERVICETASKPATH,writeValueAsBytes);
			} 
			catch (Exception e1)
			{
				logger.error("failed to set data." + e1);
			}
			logger.error("failed to get obj value." + e);
		}
		catch (Exception e)
		{
			logger.error("failed to get task." + e);
		}
		
	}

	/**
	 * 获取本机的IP
	 */
	private String getLocalIP()
	{
		String localip = "";
		try {
			InetAddress ia=InetAddress.getLocalHost();
			localip=ia.getHostAddress();
			logger.info("get localIP is " + localip);
		} catch (Exception e) {
			logger.error("failed to get localIP" + e);
		}
		return localip;
	}
	
	/**
	 * 获取本机的主机名
	 */
	private String getHostName()
	{
		String hostName = "";
		try {
			InetAddress ia=InetAddress.getLocalHost();
			hostName = ia.getHostName();
			logger.info("get hostName = " + hostName);
		} catch (Exception e) {
			logger.error("failed to get hostName" + e);
		}
		return hostName;
	}
	
	/**
	 * 获取本机的IP和主机名的组合
	 */
	private String getLocalIPAndHostName()
	{
		String localip = "";
		String hostName = "";
		try {
			InetAddress ia=InetAddress.getLocalHost();
			localip=ia.getHostAddress();
			hostName = ia.getHostName();
		} catch (Exception e) {
			logger.error("failed to get getLocalIPAndHostName" + e);
		}
		String localIPAndHostName = new StringBuilder().append(localip).append(".").append(hostName).toString();
		logger.info("get localIPAndHostName = " + localIPAndHostName);
		return localIPAndHostName;
	}
	
	/**
	 * 获取当前时间
	 */
	private String getCurrentTime()
	{
		String currentTime = "";
		try {
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentTime=format.format(date); 
			logger.info("get currentTime = " + currentTime);
		} catch (Exception e) {
			logger.error("failed to get localIP" + e);
		}
		return currentTime;
	}
	
	/**
	 * 检查tasknode节点是否存在，如果不存在则创建
	 */
	private void checkTaskNode()
	{
		try
		{
			Stat stat = zkClient.checkExists().forPath(CONVERTSERVICETASKROOTPATH);
			if (stat == null)
			{
				zkClient.create().forPath(CONVERTSERVICETASKROOTPATH);
				zkClient.create().forPath(CONVERTSERVICETASKPATH);
			}
			else
			{
				Stat stat1 = zkClient.checkExists().forPath(CONVERTSERVICETASKPATH);
				
				if (stat1 == null)
				{
					zkClient.create().forPath(CONVERTSERVICETASKPATH);
				}
			}
		} catch (Exception e)
		{
			logger.error("failed to create task node" + e);
		}
	}

}
