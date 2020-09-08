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
import com.huawei.sharedrive.isystem.monitor.dao.ClusterHistoryDao;
import com.huawei.sharedrive.isystem.monitor.dao.ClusterInstanceDao;
import com.huawei.sharedrive.isystem.monitor.dao.ClusterInstanceHistoryDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDao;
import com.huawei.sharedrive.isystem.monitor.dao.SystemClusterInfoDao;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;
import com.huawei.sharedrive.isystem.util.ClusterInstanceValidateUtil;
import com.huawei.sharedrive.isystem.util.ClusterValidateUtil;
import com.huawei.sharedrive.thrift.cluster.ClusterRunningInfo;
import com.huawei.sharedrive.thrift.cluster.IsystemThriftService;
import com.huawei.sharedrive.thrift.cluster.ServerRunningInfo;
import com.huawei.sharedrive.thrift.cluster.TBusinessException;

/**
 * isysetm作为服务端，收集clustermonitor上报的信息
 * 
 * @author l00357199 20162016-1-6 上午10:32:55
 */
public class ClusterServiceImpl implements IsystemThriftService.Iface
{
    /**
     * 1:异常
     * 
     */
    private static final int STATUS_ABNORMAL = 1;
    
    /**
     * 2：部分异常
     * 
     */
    private static final int STATUS_PART_ABNORMAL = 2;
    
    /**
     * :0：正常
     * 
     */
    private static final int STATUS_NORMAL = 0;
    
    @Autowired
    private ClusterDao clusterDao;
    
    @Autowired
    private ClusterInstanceDao clusterInstanceDao;
    
    @Autowired
    private ClusterHistoryDao clusterHistoryDao;
    
    @Autowired
    private ClusterInstanceHistoryDao clusterInstanceHistoryDao;
    
    @Autowired
    private SystemClusterInfoDao systemClusterInfoDao;
    
    @Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private NodeDao nodeDao;
    
    private static Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportClusterServiceRunningInfo(ClusterRunningInfo arg0) throws TBusinessException,
        TException
    {
        Cluster cluster = new Cluster(arg0);
        writeClusterToDB(cluster);
        deleteClusterInstance(arg0);
        for (ServerRunningInfo tmp : arg0.getServers())
        {
            writeClusterInstanceToDB(tmp);
        }
        
        updateSystemClusterStatus(arg0);
    }
    
    /**
     * 先删除，再加入。在writeClusterInstanceToDB加入数据库
     * 
     * @param arg0
     */
    private void deleteClusterInstance(ClusterRunningInfo arg0)
    {
        List<ServerRunningInfo> list = arg0.getServers();
        ClusterInstance clusterInstance = null;
        if (list != null && !list.isEmpty())
        {
            clusterInstance = new ClusterInstance(list.get(0));
        }
        clusterInstanceDao.deleteClusterInstances(clusterInstance);
    }
    
    private void writeClusterToDB(Cluster cluster)
    {
        try
        {
            if (ClusterValidateUtil.validate(cluster))
            {
                // 服务集群信息，如CD-UAS-MYSQL集群
                Cluster temp = clusterDao.getOne(cluster);
                if (temp == null)
                {
                    clusterDao.insert(cluster);
                }
                else
                {
                    clusterDao.update(cluster);
                }
                clusterHistoryDao.insert(cluster);
                // 服务集群节点信息，如CD-UAS-MYSQL-1的2个节点
                logger.info("writeClusterToDB success");
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        catch (RuntimeException e)
        {
            logger.error(cluster.toString(), e);
            throw e;
        }
    }
    
    /**
     * 根据 服务节点的状态 更新 物理集群的状态
     * 
     * @param arg0
     * @param arg0
     */
    private void updateSystemClusterStatus(ClusterRunningInfo arg0)
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
            ss.setClusterName(arg0.getClusterName());
            ss.setStatus(status);
            systemClusterInfoDao.updateStatus(ss);
            logger.info("updateSystemClusterStatus success");
            
        }
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
    
    private void writeClusterInstanceToDB(ServerRunningInfo tmp)
    {
        ClusterInstance clusterInstance = new ClusterInstance(tmp);
        try
        {
            if (ClusterInstanceValidateUtil.validate(clusterInstance))
            {
                clusterInstanceDao.insert(clusterInstance);
                clusterInstanceHistoryDao.insert(clusterInstance);
                logger.info("writeClusterInstanceToDB success");
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        catch (RuntimeException e)
        {
            logger.error(clusterInstance.toString(), e);
            throw e;
        }
    }
    
    /**
     * system_cluster_info表缺失该映射，中根据ClusterName查SystemName 并加入表中
     * 
     * @param arg0
     */
    private void getClusterSystemName(ClusterRunningInfo arg0)
    {
        ResourceGroupNode r = null;
        // 集群 接口用innerip ，节点、进程接口用managerIp
        for (ServerRunningInfo tmp : arg0.getServers())
        {
            r = resourceGroupNodeDao.getResourceGroupNodeByInnerIP(tmp.inner);
            if (r != null)
            {
                break;
            }
        }
        
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
        logger.error("get clusterSystemName failed, resourceGroupNodeDao.getResourceGroupNodeByInnerIP() return null");
    }
    
}
