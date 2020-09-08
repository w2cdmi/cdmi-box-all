package pw.cdmi.box.isystem.monitor.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;

import pw.cdmi.box.isystem.monitor.domain.CSNodeRunningInfo;
import pw.cdmi.box.isystem.monitor.domain.DealedCSNodeRunningInfo;
import pw.cdmi.box.isystem.monitor.service.CSMonitorService;
import pw.cdmi.core.zk.ZookeeperServer;

@Component("cSMonitorService")
public class CSMonitorServiceImpl implements CSMonitorService
{
	private static Logger logger = LoggerFactory.getLogger(CSMonitorServiceImpl.class);

	// 系统状态部分异常
	private static final int SYSTEM_STATUS_PART_ABNORMAL = 2;

	/**
	 * 缓存根路径, 结尾不加/
	 */
	private String convertserviceCaches = "/ISYSTEM-CONVERTSERVICE";

	@Resource(name = "zookeeperServer")
	private ZookeeperServer zookeeperServer;

	private CuratorFramework zkClient;

	@PostConstruct
	public void init()
	{
		logger.debug("CSMonitorServiceImpl.init() start");
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
			logger.error("init ZKShiroSessionDAO fail!", e);
		}
		logger.debug("CSMonitorServiceImpl.init() end");
	}

	public void setZookeeperServer(ZookeeperServer zookeeperServer)
	{
		this.zookeeperServer = zookeeperServer;
	}

	/**
	 * 从zookeeper中获取所有的节点的原始信息集合
	 * 
	 * @return 节点信息集合
	 */
	private List<CSNodeRunningInfo> getAllHostInfo()
	{
		logger.debug("CSMonitorServiceImpl.getAllHostInfo() start");
		List<CSNodeRunningInfo> cSNodeRunningInfos = new ArrayList<CSNodeRunningInfo>();
		try
		{
			//根目录下所有节点的集合
			List<String> hostNames = zkClient.getChildren().forPath(convertserviceCaches);
			for (String hostname : hostNames)
			{
				ObjectMapper obj = new ObjectMapper();
				byte[] writeValueAsBytes = zkClient.getData().forPath(convertserviceCaches + "/" + hostname);
				//将获取到的每个节点的byte[] 数据转换为对象
				CSNodeRunningInfo cSNodeRunningInfo = obj.readValue(writeValueAsBytes, CSNodeRunningInfo.class);
				cSNodeRunningInfos.add(cSNodeRunningInfo);
			}
		} catch (Exception e)
		{
			logger.error("failed to get original node list!", e);
		}
		logger.debug("CSMonitorServiceImpl.getAllHostInfo() end");
		return cSNodeRunningInfos;
	}

	/**
	 * 对于从zookeeper中获取的信息进行分类处理，建立一个map，key值为system名称，
	 * value值为构造的一个对象，对象中包含着属于同一个system的host集合
	 * 
	 * @return 处理后的map
	 */
	private Map<String, DealedCSNodeRunningInfo> dealNodeRunningInfo()
	{
		logger.debug("CSMonitorServiceImpl.dealNodeRunningInfo() start");
		Map<String, DealedCSNodeRunningInfo> map = new HashMap<String, DealedCSNodeRunningInfo>();
		DealedCSNodeRunningInfo dealedCSNodeRunningInfo = null;
		List<CSNodeRunningInfo> cSNodeRunningInfos = getAllHostInfo();

		for (int i = 0; i < cSNodeRunningInfos.size(); i++)
		{
			dealedCSNodeRunningInfo = new DealedCSNodeRunningInfo();
			//如果map中找不到对应的系统名称，则添加该系统和系统下的节点信息到处理后的domain中
			//如果map中找到对应的系统名称，则先获取到原来系统中的节点集合，然后添加新增的节点，重新放回map中
			if (map.get(cSNodeRunningInfos.get(i).getSystemName()) == null)
			{
				List<CSNodeRunningInfo> dealedCSNodeRunningInfoss = new ArrayList<CSNodeRunningInfo>();
				dealedCSNodeRunningInfoss.add(cSNodeRunningInfos.get(i));

				dealedCSNodeRunningInfo.setClusterID(cSNodeRunningInfos.get(i).getClusterID());
				dealedCSNodeRunningInfo.setClusterName(cSNodeRunningInfos.get(i).getClusterName());
				dealedCSNodeRunningInfo.setSystemName(cSNodeRunningInfos.get(i).getSystemName());
				//第一次添加系统时，系统状态和节点状态一致
				dealedCSNodeRunningInfo.setStatus(cSNodeRunningInfos.get(i).getStatus());

				dealedCSNodeRunningInfo.setcSNodeRunningInfos(dealedCSNodeRunningInfoss);

				map.put(cSNodeRunningInfos.get(i).getSystemName(), dealedCSNodeRunningInfo);
			} 
			else
			{
				List<CSNodeRunningInfo> dealedCSNodeRunningInfoss = map.get(cSNodeRunningInfos.get(i).getSystemName())
						.getcSNodeRunningInfos();
				dealedCSNodeRunningInfoss.add(cSNodeRunningInfos.get(i));

				dealedCSNodeRunningInfo.setClusterID(cSNodeRunningInfos.get(i).getClusterID());
				dealedCSNodeRunningInfo.setClusterName(cSNodeRunningInfos.get(i).getClusterName());
				dealedCSNodeRunningInfo.setSystemName(cSNodeRunningInfos.get(i).getSystemName());
				//添加多个节点时，每次添加的节点状态和系统的节点状态比较，如果一致则状态不变，如果不一致，节点状态则为部分异常
				if (cSNodeRunningInfos.get(i).getStatus() == map.get(cSNodeRunningInfos.get(i).getSystemName()).getStatus()){
					dealedCSNodeRunningInfo.setStatus(cSNodeRunningInfos.get(i).getStatus());
				}else{
					dealedCSNodeRunningInfo.setStatus(SYSTEM_STATUS_PART_ABNORMAL);
				}

				dealedCSNodeRunningInfo.setcSNodeRunningInfos(dealedCSNodeRunningInfoss);

				map.put(cSNodeRunningInfos.get(i).getSystemName(), dealedCSNodeRunningInfo);
			}
		}
		logger.debug("CSMonitorServiceImpl.dealNodeRunningInfo() end");
		return map;
	}
	
	@Override
	public List<SystemClusterInfo> getAllCSSystemName()
	{
		logger.debug("CSMonitorServiceImpl.getAllCSSystemName() start");
		//返回的系统信息列表，包括系统名称和状态
		List<SystemClusterInfo> systemClusterInfos = new ArrayList<SystemClusterInfo>();
		SystemClusterInfo systemClusterInfo = null;
		//获取处理后的所有节点信息，key为systemname，value为节点对象，包含此系统下所有节点信息
		Map<String, DealedCSNodeRunningInfo> map = dealNodeRunningInfo();
		Set<String> systemnames = map.keySet();
		for (String system : systemnames)
		{
			systemClusterInfo = new SystemClusterInfo();
			DealedCSNodeRunningInfo dealedCSNodeRunningInfo = map.get(system);
			systemClusterInfo.setSystemName(system);
			systemClusterInfo.setStatus(dealedCSNodeRunningInfo.getStatus());
			systemClusterInfos.add(systemClusterInfo);
		}	
		logger.debug("CSMonitorServiceImpl.getAllCSSystemName() end");
		return systemClusterInfos;
	}
	
	@Override
	public List<CSNodeRunningInfo> getCSClusterNodes(String csSystemName)
	{
		logger.debug("CSMonitorServiceImpl.getCSClusterNodes() start");
		//获取处理后的所有节点信息，key为systemname，value为节点对象，包含此系统下所有节点信息
		Map<String, DealedCSNodeRunningInfo> map = dealNodeRunningInfo();
		DealedCSNodeRunningInfo dealedCSNodeRunningInfo = map.get(csSystemName);
		List<CSNodeRunningInfo> cSNodeRunningInfos = dealedCSNodeRunningInfo.getcSNodeRunningInfos();
		logger.debug("CSMonitorServiceImpl.getCSClusterNodes() end");
		return cSNodeRunningInfos;
	}

	@Override
	public int getCurrentSystemStatus(String csSystemName)
	{
		logger.debug("CSMonitorServiceImpl.getCurrentSystemStatus() start");
		//获取处理后的所有节点信息，key为systemname，value为节点对象，包含此系统下所有节点信息
		Map<String, DealedCSNodeRunningInfo> map = dealNodeRunningInfo();
		int status = map.get(csSystemName).getStatus();
		logger.debug("CSMonitorServiceImpl.getCurrentSystemStatus() end");
		return status;
	}

}
