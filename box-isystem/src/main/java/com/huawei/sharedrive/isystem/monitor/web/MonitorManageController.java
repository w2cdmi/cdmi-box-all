/**
 * 
 */
package com.huawei.sharedrive.isystem.monitor.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterService;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;
import com.huawei.sharedrive.isystem.monitor.service.MonitorDBservice;

import pw.cdmi.box.isystem.monitor.domain.CSNodeRunningInfo;
import pw.cdmi.box.isystem.monitor.service.CSMonitorService;

/**
 * 
 * 
 * @author d00199602
 * 
 */
@Controller
@RequestMapping(value = "/monitor/manage")
public class MonitorManageController extends AbstractCommonController
{
    public static final Logger LOGGER = LoggerFactory.getLogger(MonitorManageController.class);
    
    @Autowired
    private MonitorDBservice monitorDBservice;
    
    @Autowired
    private CSMonitorService cSManagerService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        return "monitor/monitorMain";
    }
    
    /**
     * 获取所有集群名称
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "nodelist", method = RequestMethod.GET)
    public String nodeList(Model model)
    {
        List<SystemClusterInfo> clusters = monitorDBservice.getAllClusterName();
        //查询到的convertservice下所有系统的名称和状态
        List<SystemClusterInfo> csSystems = cSManagerService.getAllCSSystemName();
        
        model.addAttribute("clusters", clusters);
        model.addAttribute("csSystems", csSystems);
        return "monitor/clusterMonitorIndex";
    }
    
    /**
     * 获取某集群的所有节点和服务
     * 
     * @param model
     * @param clusterName
     * @return
     */
    @RequestMapping(value = "nodedetail/{clusterName}", method = RequestMethod.GET)
    public String nodeDetail(Model model, @PathVariable("clusterName") String clusterName)
    {
        List<NodeRunningInfo> nodes = monitorDBservice.getClusterNodes(clusterName);// 某集群的所有节点
        List<Cluster> clusterServices = monitorDBservice.getClusterServeices(clusterName);// 目前只有
                                                                                          // Mysql集群信息
        model.addAttribute("nodes", nodes);
        model.addAttribute("clusterName", clusterName);
        model.addAttribute("clusterServices", clusterServices);
        return "monitor/clusterMonitorTable";
    }
    
    @RequestMapping(value = "viewnode/{id}", method = RequestMethod.GET)
    public String viewNode(@PathVariable("id") int id, Model model)
    {
        return "monitor/checkClusterIndex";
    }
    
    /**
     * 获取某物理集群的某一个 节点 ,如 UAS的第一个节点 CD-UAS-HOST-1
     * 
     */
    @RequestMapping(value = "viewcontent/{hostName}", method = RequestMethod.GET)
    public String viewContent(@PathVariable("hostName") String hostName, Model model)
    {
        // hostName = "CD-UAS-HOST-1";// 前台传入
        // 节点信息
        NodeRunningInfo currentNode = monitorDBservice.getOneNodeInfo(hostName);
        List<NodeRunningInfo> nodes = monitorDBservice.getClusterNodes(currentNode.getClusterName());
        model.addAttribute("nodes", nodes);
        model.addAttribute("currentNode", currentNode);
        return "monitor/checkClusterIndex";
    }
    
    @RequestMapping(value = "refreshClusterStatus/{clusterName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> refreshClusterStatus(@PathVariable("clusterName") String clusterName, Model model)
    {
        SystemClusterInfo s = monitorDBservice.getSystemName(clusterName);
        return new ResponseEntity(s.getStatus(),HttpStatus.OK);
    }
    
    @RequestMapping(value = "viewNodeContent/{hostName}", method = RequestMethod.GET)
    public String viewNodeContent(@PathVariable("hostName") String hostName, Model model)
    {
        // hostName = "CD-UAS-HOST-1";// 前台传入
        // 节点信息
        NodeRunningInfo oneNode = monitorDBservice.getOneNodeInfo(hostName);
        model.addAttribute("oneNode", oneNode);
        
        // 某节点 磁盘目录 信息
        List<NodeDisk> disks = monitorDBservice.getDisks(hostName);
        model.addAttribute("disks", disks);
        
        // 某节点端口信息
        List<NodeDiskIO> diskIOs = monitorDBservice.getDiskIOs(hostName);
        model.addAttribute("diskIOs", diskIOs);
        
        // 某节点的Mysql进程信息
        List<ProcessInfo> processInfos = monitorDBservice.getNodeProcess(hostName);
        model.addAttribute("processInfos", processInfos);
        return "monitor/checkClusterTable";
    }
    
    /**
     * 获取某服务的节点，如：CD-UAS-MYSQL-1的2个节点
     * 
     */
    @RequestMapping(value = "viewMysqlContent", method = RequestMethod.GET)
    public String viewMysqlContent(String clusterName, String clusterServiceName, Model model)
    {
        List<ClusterInstance> nodes = monitorDBservice.getServiceNodes(clusterName, clusterServiceName);
        Map<String, ClusterService> map = new HashMap<String, ClusterService>(10);
        String sName = null;
        List<ClusterInstance> instances = null;
        for (ClusterInstance cluster : nodes)
        {
            sName = cluster.getClusterServiceName();
            if (map.get(sName) == null)
            {
                instances = new ArrayList<ClusterInstance>(10);
                ClusterService service = new ClusterService();
                service.setServiceName(cluster.getClusterServiceName());
                service.setVip(cluster.getVip());
                instances.add(cluster);
                service.setInstances(instances);
                map.put(sName, service);
            }
            else
            {
                ClusterService services = map.get(sName);
                instances = services.getInstances();
                instances.add(cluster);
            }
        }
        List<ClusterService> services = new ArrayList<ClusterService>(10);
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext())
        {
            services.add(map.get(iterator.next()));
        }
        model.addAttribute("services", services);
        return "monitor/checkClusterTable1";
    }
    
    /************************************ConvertService监控功能 by栾锡宝 start****************************************/
    /**
     * 从zookeeper目录中获取convertservice系统名称信息
     * zookeeper目录结构：系统名称/集群名称/节点名称（节点下记录状态信息）
     * @param model 领域对象
     * @return 集群监控主页面
     */
    @RequestMapping(value = "cssystemnamelist", method = RequestMethod.GET)
    public String csSystemList(Model model)
    {
        List<SystemClusterInfo> csSystems = cSManagerService.getAllCSSystemName();
        model.addAttribute("csSystems", csSystems);
        return "monitor/clusterMonitorIndex";
    }
    
    /**
     * 获取当前系统的状态,点击某个系统名称时调用
     * @param model 领域对象
     * @param csSystemName  集群名称
     * @return 集群监控列表
     */
    @RequestMapping(value = "csSystemStatus/{csSystemName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> csSystemStatus(Model model, @PathVariable("csSystemName") String csSystemName)
    {
        int currentSystemStatus = cSManagerService.getCurrentSystemStatus(csSystemName);
        
        return new ResponseEntity(currentSystemStatus,HttpStatus.OK);
    }
    
    /**
     * 从zookeeper目录中获取某系统集群下的所有节点的信息，点击某个系统名称时调用
     * zookeeper目录结构：系统名称/集群名称/节点名称（节点下记录状态信息）
     * @param model 领域对象
     * @param csSystemName  集群名称
     * @return 集群监控列表
     */
    @RequestMapping(value = "csnodedetail/{csSystemName}", method = RequestMethod.GET)
    public String csNodeDetail(Model model, @PathVariable("csSystemName") String csSystemName)
    {
        List<CSNodeRunningInfo> csNodes = cSManagerService.getCSClusterNodes(csSystemName);
        String clusterName = "";
        if(csNodes != null)
        {
        	clusterName = csNodes.get(0).getClusterName(); 
        }
        model.addAttribute("clusterName",clusterName);
        model.addAttribute("csNodes", csNodes);
        return "monitor/csMonitorTable";
    }
    /************************************ConvertService监控功能 by栾锡宝 end****************************************/
}
