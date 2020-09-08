package pw.cdmi.box.app.convertservice.service;

import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;
import pw.cdmi.box.app.convertservice.service.impl.DealNodeStatusTread;
import pw.cdmi.box.app.system.service.CSAlarmService;
import pw.cdmi.common.monitor.domain.ServiceNode;
import pw.cdmi.common.monitor.service.ServiceNodeService;
import pw.cdmi.core.zk.ZookeeperServer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;


public class MonitorNodeStatusTask implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(MonitorNodeStatusTask.class);

    /**
     * 缓存根路徑, 结尾不加
     */
	@Autowired
	private static String      convertserviceCaches       = "/ISYSTEM-CONVERTSERVICE";
	private static String      CONVERTSERVICETASKROOTPATH = "/CONVERTSERVICETASKPATH";
	private static String      CONVERTSERVICETASKPATH     = "/CONVERTSERVICETASKPATH/tasknode";
	@Autowired
	private CSMonitorService   cSMonitorService;
	@Autowired
	private CSAlarmService     cSAlarmService;
	@Autowired
	private ConvertService     convertService;
	@Autowired
	private ServiceNodeService serviceNodeService;
	@Resource(name = "zookeeperServer")
	private ZookeeperServer    zookeeperServer;
	
	private CuratorFramework   zkClient;
	private NodeListWatcher    nodeListWatcher            = new NodeListWatcher();
	private List<String>       ufmIPList                  = null;
	private List<String>       localIPList;

    public void init() {
        new Thread(this).start();
        try {
            zkClient = zookeeperServer.getClient();

            Stat stat = zkClient.checkExists().forPath(convertserviceCaches);

            if (stat == null) {
                zkClient.create().forPath(convertserviceCaches);
            }
        } catch (Exception e) {
            logger.error("init MonitorNodeStatusTask fail!", e);
        }

        localIPList = getLocalIPList();
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
        checkTaskNode();

        if (CollectionUtils.isEmpty(ufmIPList)) {
            refreshZKNodeList();
        }

        logger.info("ufmIPLists: "+ ufmIPList);
        logger.info("localIPList: "+ localIPList);
        while (true) {
            if (CollectionUtils.isNotEmpty(ufmIPList)) {
            	/*
            	 *  這里的排序是在AC,DC使用同一zookeeper情況下只選取其中一個活的ufm節點
            	 *  當分布時ufmIPList中只有一條記錄，就是本機ip。
            	 */
                Collections.sort(ufmIPList);
                String ip = ufmIPList.get(0);
                String[] split = ip.split(":");

                /*
                 * 此邏輯只適用于合布的情況
                 * 由于上面只選取了一個可用的ufm節點
                 * 不管啟動幾個預覽服務器【convertService】，只會實際使用一個ufm讀取sysdb.convert_task任務表
                 * 再啟動DealNodeStatusTread.run方法
                 * TODO: guoz 分布需要優化，雖然分布也不會報錯。但是會執行多次。預覽服務器有幾個節點就會執行幾次。
                 */
                if (localIPList.contains(split[0])) {
                	// 獲取sysdb庫resource_group_node中的值
                    List<NodeRunningInfo> nodeRunningInfos = cSMonitorService.getAllCSNodeInfo();

                    for (NodeRunningInfo nodeRunningInfo : nodeRunningInfos) {
                    	// 這里需要判斷resource_group_node中runtimestate的狀態為正常的才會執行
                    	if(NodeRunningInfo.STATUS_NORMAL == nodeRunningInfo.getDssRuntimeStatus()) {
                    		 DealNodeStatusTread dealNodeStatusTread = new DealNodeStatusTread(nodeRunningInfo);
                             dealNodeStatusTread.setConvertService(this.convertService);
                             dealNodeStatusTread.setcSAlarmService(this.cSAlarmService);
                             dealNodeStatusTread.setcSMonitorService(this.cSMonitorService);
                             scheduledThreadPool.scheduleAtFixedRate(dealNodeStatusTread,
                                 0, 90, TimeUnit.SECONDS); //周期為秒，調用DealNodeStatusTread.run方法
                    	}
                    }
                   
                    deleteNoUseHostNode(nodeRunningInfos);
                }
            }

            try {  
            	Thread.sleep(60000);  
            } catch (InterruptedException e) {
                logger.error("Thread error", e);
            }

            convertService.resetNodeRunningStatus();
            scheduledThreadPool.shutdown();
            scheduledThreadPool = Executors.newScheduledThreadPool(10);
        }
    }

    private void deleteNoUseHostNode(List<NodeRunningInfo> nodeRunningInfos) {

        try {
            List<String> hostChildrens = zkClient.getChildren()
                                                 .forPath(convertserviceCaches);

            for (String node : hostChildrens) {
                boolean isExist = false;

                for (NodeRunningInfo nodeRunningInfo : nodeRunningInfos) {
                    String nodeName = new StringBuilder().append(nodeRunningInfo.getHostName())
                                                         .append("_")
                                                         .append(nodeRunningInfo.getResourceGroupID())
                                                         .toString();

                    if (node.equals(nodeName)) {
                        isExist = true;

                        break;
                    }
                }

                if (!isExist) {
                    logger.info("the host is not exist,will delete the node :" + convertserviceCaches + "/" + node);
                    zkClient.delete().forPath(convertserviceCaches + "/" +
                        node);
                }
            }
        } catch (Exception e) {
            logger.error("failed to get childrens or delete node", e);
        }
    }

    private void checkTaskNode() {

        try {
            Stat stat = zkClient.checkExists()
                                .forPath(CONVERTSERVICETASKROOTPATH);

            if (stat == null) {
                zkClient.create().forPath(CONVERTSERVICETASKROOTPATH);
                zkClient.create().forPath(CONVERTSERVICETASKPATH);
            } else {
                Stat stat1 = zkClient.checkExists()
                                     .forPath(CONVERTSERVICETASKPATH);

                if (stat1 == null) {
                    zkClient.create().forPath(CONVERTSERVICETASKPATH);
                }
            }
        } catch (Exception e) {
            logger.error("failed to create task node" + e);
        }
    }

    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<String>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;

            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();

                    if ((inetAddress != null) &&
                            inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ipList;
    }

    private void refreshZKNodeList() {
        if (zookeeperServer.getClient().getState()
                               .equals(CuratorFrameworkState.STOPPED)) {
            return;
        }

        List<String> childList = new ArrayList<String>();

        try {
        	/*
        	 * add by: guoz
        	 * 此方法只用于AC和DC使用同一zookeeper,
        	 * 其實這個方式都可以不要，只需要catch中的邏輯就可以了。只是catch中需要查詢一次數據庫。
        	 */
            childList = zookeeperServer.getClient().getChildren()
                                       .usingWatcher(nodeListWatcher)
                                       .forPath("/thrift-nodes/ufm-for-dss");
        } catch (Exception e) {
        	/*
        	 * add by: guoz
        	 * 如果在AC和DC分布情況下zookeeper也是分布的。AC中是取不到DC-zookeeper中【/thrift-nodes/ufm-for-dss】對應的路徑一定會報錯。
        	 * 原因是childList中查詢到的值也是dss獲取的ip是ds_sysdb.service_node鏈接活的ufm的的ip及端口。
        	 * 在分布的情況下ds_sysdb.service_node表中的數據也是ufm的實ip。
        	 * 那么就可用自身ufm就是活的所有可以直接用本地ip
        	 * 存在ds_sysdb.service_node中的ip是從sysdb.service_node表中以clusterType=ufm為條件的查詢記錄。那么此處的本地ip也就是從這里獲取
        	 * 
        	 * 注意：
        	 * 【個人懷疑在不管在分布還是合布的時候，所使用的對應ip應該都應該是ufm工程application.properties配置項中thrift.dataserver.report.addr對應的虛ip及thrift.dataserver.port對應的端口，即可解決問題】
        	 * 這個問題=以后優化的時候再處理吧。
        	 * 
        	 */
        	List<ServiceNode> serviceNodesLst = serviceNodeService.getAllByClusterType(ServiceNode.CLUSTER_TYPE_UFM);
        	for(ServiceNode sn : serviceNodesLst ) {
        		if(localIPList.contains(sn.getManagerIp())) {
        			childList.add(sn.getManagerIp()+":" +sn.getManagerPort()); //存放的就是ip:port這種格式。沒有其它特殊的原因。
        		}
        	}
        	if(serviceNodesLst == null || serviceNodesLst.size() <= 0) {
        		logger.error("refreshZKNodeList has no data ！ previewer is not work! check this!");	
        	}
        }

        Set<String> ufmSet = new HashSet<String>();
        List<String> ufmList = new ArrayList<String>();

        for (String name : childList) {
            ufmSet.add(name);
            ufmList.addAll(ufmSet);
        }

        ufmIPList = ufmList;
    }

    private class NodeListWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Watcher.Event.EventType.None) {
                logger.warn("receive a none type event, content = " +
                    event.toString());

                return;
            }

            try {
                refreshZKNodeList();
            } catch (Exception e) {
                logger.error("error occur when get children for " +
                    "/thrift-nodes/ufm-for-dss", e);
            }
        }
    }
}
