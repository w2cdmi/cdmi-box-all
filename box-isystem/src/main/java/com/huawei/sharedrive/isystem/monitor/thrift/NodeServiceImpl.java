package com.huawei.sharedrive.isystem.monitor.thrift;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.huawei.sharedrive.isystem.monitor.dao.ClusterDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskHistoryDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskIODao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskIOHistoryDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeHistoryDao;
import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoDao;
import com.huawei.sharedrive.isystem.monitor.dao.SystemClusterInfoDao;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;
import com.huawei.sharedrive.isystem.util.NodeDiskIOValidateUtil;
import com.huawei.sharedrive.isystem.util.NodeDiskValidateUtil;
import com.huawei.sharedrive.isystem.util.NodeValidateUtil;
import com.huawei.sharedrive.thrift.systemNode.DiskIO;
import com.huawei.sharedrive.thrift.systemNode.DiskInfo;
import com.huawei.sharedrive.thrift.systemNode.SystemNodeRunningInfo;
import com.huawei.sharedrive.thrift.systemNode.SystemNodeRunningThriftService;
import com.huawei.sharedrive.thrift.systemNode.TBusinessException;

public class NodeServiceImpl implements SystemNodeRunningThriftService.Iface
{
    private static Logger logger = LoggerFactory.getLogger(NodeServiceImpl.class);
    
    /**
     * 1:异常
     * 
     */
    private static final int STATUS_ABNORMAL = 1;
    
    /**
     * :0：正常
     * 
     */
    private static final int STATUS_NORMAL = 0;
    
    /**
     * 2：部分异常
     * 
     */
    private static final int STATUS_PART_ABNORMAL = 2;
    
    @Autowired
    private ClusterDao clusterDao;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private NodeDao nodeDao;
    
    @Autowired
    private NodeDiskDao nodeDiskDao;
    
    @Autowired
    private NodeDiskHistoryDao nodeDiskHistoryDao;
    
    @Autowired
    private NodeDiskIODao nodeDiskIODao;
    
    @Autowired
    private NodeDiskIOHistoryDao nodeDiskIOHistoryDao;
    
    @Autowired
    private NodeHistoryDao nodeHistoryDao;
    
    @Autowired
    private ProcessInfoDao processInfoDao;
    
    @Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
    
    // @Autowired
    // protected Validator validator;
    @Autowired
    private SystemClusterInfoDao systemClusterInfoDao;
    
    // @Autowired
    // private ProcessInfoDao processInfoDao;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportSystemNodeRunningInfo(SystemNodeRunningInfo arg0) throws TBusinessException, TException
    {
        writeNodeToDB(arg0);
        
        writeDiskInfoToDB(arg0);
        
        writeDiskIOToDB(arg0);
        
        updateSystemClusterStatus(arg0);
    }
    
    private int computeClusterServicesStatus(String clusterName)
    {
        List<Cluster> clusterServices = clusterDao.getClusterServices(clusterName);
        int clustersize = clusterServices.size();
        int clusterstatus = 0;
        int tmpstatus = 0;
        for (Cluster cluster : clusterServices)
        {
            tmpstatus += cluster.getStatus();
        }
        if (tmpstatus == STATUS_NORMAL)
        {
            clusterstatus = STATUS_NORMAL;
        }
        else if (tmpstatus == clustersize)
        {
            clusterstatus = STATUS_ABNORMAL;
        }
        else
        {
            clusterstatus = STATUS_PART_ABNORMAL;
        }
        return clusterstatus;
    }
    
    private int computeFinalStatus(String clusterName)
    {
        int status;
        int nodestatus = computeNodesStatus(clusterName);
        
        if (nodestatus == STATUS_PART_ABNORMAL)
        {
            return STATUS_PART_ABNORMAL;
        }
        
        int clusterstatus = computeClusterServicesStatus(clusterName);
        
        if (nodestatus == STATUS_NORMAL && clusterstatus == STATUS_NORMAL)
        {
            status = STATUS_NORMAL;
        }
        else if (nodestatus == STATUS_ABNORMAL && clusterstatus == STATUS_ABNORMAL)
        {
            status = STATUS_ABNORMAL;
        }
        else
        {
            status = STATUS_PART_ABNORMAL;
        }
        return status;
    }
    
    private int computeNodesStatus(String clusterName)
    {
        // 情况复杂，需要根据所有节点和服务集群的状态 决定 物理集群的状态
        List<NodeRunningInfo> nodes = nodeDao.getClusterNodes(clusterName);
        int nodesize = nodes.size();
        int nodestatus = 0;
        int sumstatus = 0;
        for (NodeRunningInfo node : nodes)
        {
            sumstatus += node.getStatus();
        }
        
        if (sumstatus == STATUS_NORMAL)
        {
            nodestatus = STATUS_NORMAL;
        }
        else if (sumstatus == nodesize)
        {
            nodestatus = STATUS_ABNORMAL;
        }
        else
        {
            nodestatus = STATUS_PART_ABNORMAL;
        }
        return nodestatus;
    }
    
    /**
     * system_cluster_info表缺失该映射，中根据ClusterName查SystemName 并加入表中
     * 
     * @param arg0
     * @param tmp
     */
    @SuppressWarnings("boxing")
    private void getClusterSystemName(SystemNodeRunningInfo arg0)
    {
        // 节点的managerIP 相当于 innerIP
        ResourceGroupNode r = resourceGroupNodeDao.getResourceGroupNodeByManagerIP(arg0.getManagerIp());
        if (r != null)
        {
            DataCenter d = dcDao.get(r.getDcId());
            if (d != null)
            {
                SystemClusterInfo s = new SystemClusterInfo();
                s.setSystemName(d.getName());
                s.setResourceGrounpId(r.getResourceGroupID());
                s.setClusterName(arg0.getClusterName());
                s.setType(1);
                s.setStatus(computeFinalStatus(arg0.getClusterName()));
                systemClusterInfoDao.insert(s);
                logger.info("get clusterSystemName success");
            }
            logger.error("get clusterSystemName failed, dcDao.getByID() return null");
        }
        logger.error("get clusterSystemName failed, resourceGroupNodeDao.getResourceGroupNodeByManagerIP() return null");
    }
    
    /**
     * 根据节点状态更新物理集群的 状态
     * 
     * @param arg0
     */
    private void updateSystemClusterStatus(SystemNodeRunningInfo arg0)
    {
        int status = 0;// 最终写入的状态
        
        // 查询clusterName对应的systemName(没有，则 构造systemName与clusterName的对应关系 )
        SystemClusterInfo systemClusterInfo = systemClusterInfoDao.getSystemName(arg0.getClusterName());
        if (systemClusterInfo == null)
        {
            getClusterSystemName(arg0);
            
        }
        else
        {
            // 0:正常，1：全部异常，2：部分异常
            
            status = computeFinalStatus(arg0.getClusterName());
            
            // 根据服务集群节点状态 更新 集群状态
            SystemClusterInfo ss = new SystemClusterInfo();
            ss.setClusterName(systemClusterInfo.getClusterName());
            ss.setStatus(status);
            systemClusterInfoDao.updateStatus(ss);
            logger.info("updateSystemClusterStatus success");
        }
    }
    
    private void writeDiskInfoToDB(SystemNodeRunningInfo arg0)
    {
        NodeDisk disk = null;
        NodeDisk temp = null;
        for (DiskInfo diskInfo : arg0.getDiskInfo())
        {
            disk = new NodeDisk(arg0.getClusterName(), arg0.getHostName(), arg0.getReportTime(), diskInfo);
            try
            {
                if (NodeDiskValidateUtil.validate(disk))
                {
                    temp = nodeDiskDao.getOneDisk(disk);
                    if (temp == null)
                    {
                        nodeDiskDao.insert(disk);
                    }
                    else
                    {
                        nodeDiskDao.update(disk);
                    }
                    
                    nodeDiskHistoryDao.insert(disk);
                    logger.info("writeDiskInfoToDB success");
                    
                }
                else
                {
                    throw new InvalidParameterException();
                }
            }
            catch (RuntimeException e)
            {
                logger.error(disk.toString(), e);
                throw e;
            }
        }
    }
    
    private void writeDiskIOToDB(SystemNodeRunningInfo arg0)
    {
        NodeDiskIO nodeDiskIO = null;
        NodeDiskIO temp = null;
        
        for (DiskIO diskIO : arg0.getDiskIO())
        {
            nodeDiskIO = new NodeDiskIO(arg0.getClusterName(), arg0.getHostName(), arg0.getReportTime(),
                diskIO);
            try
            {
                if (NodeDiskIOValidateUtil.validate(nodeDiskIO))
                {
                    temp = nodeDiskIODao.getOneDiskIO(nodeDiskIO);
                    if (temp == null)
                    {
                        nodeDiskIODao.insert(nodeDiskIO);
                    }
                    else
                    {
                        nodeDiskIODao.update(nodeDiskIO);
                    }
                    
                    nodeDiskIOHistoryDao.insert(nodeDiskIO);
                    logger.info("writeDiskIOToDB success");
                }
                else
                {
                    throw new InvalidParameterException();
                }
            }
            catch (RuntimeException e)
            {
                logger.error(nodeDiskIO.toString(), e);
                throw e;
            }
            
        }
    }
    
    /**
     * 根据进程状态更新节点状态
     * 
     * @param arg0
     */
    private void writeNodeToDB(SystemNodeRunningInfo arg0)
    {
        NodeRunningInfo nodeRunningInfo = new NodeRunningInfo(arg0);
        try
        {
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.setHostName(arg0.hostName);
            processInfo.setProcessName("Mysql");
            setNodeStatus(arg0, nodeRunningInfo, processInfo);
            if (!NodeValidateUtil.validate(nodeRunningInfo))
            {
                throw new InvalidParameterException();
            }
            saveOrUpdateNode(nodeRunningInfo);
            nodeHistoryDao.insert(nodeRunningInfo);
            logger.info("writeNodeToDB success");
        }
        catch (RuntimeException e)
        {
            logger.error(nodeRunningInfo.toString(), e);
            throw e;
        }
    }
    
    private void saveOrUpdateNode(NodeRunningInfo nodeRunningInfo)
    {
        NodeRunningInfo tmp = nodeDao.getOneNode(nodeRunningInfo.getHostName());
        if (tmp == null)
        {
            nodeDao.insert(nodeRunningInfo);
        }
        else
        {
            nodeDao.update(nodeRunningInfo);
        }
    }
    
    private void setNodeStatus(SystemNodeRunningInfo arg0, NodeRunningInfo nodeRunningInfo,
        ProcessInfo processInfo)
    {
        ProcessInfo pInfo = processInfoDao.get(processInfo);
        if (pInfo != null)
        {
            if (pInfo.getStatus() == STATUS_ABNORMAL || arg0.status == STATUS_ABNORMAL)
            {
                nodeRunningInfo.setStatus(STATUS_ABNORMAL);
            }
            else
            {
                nodeRunningInfo.setStatus(STATUS_NORMAL);
            }
        }
    }
}
