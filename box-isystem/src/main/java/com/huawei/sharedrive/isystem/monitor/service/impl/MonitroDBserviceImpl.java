package com.huawei.sharedrive.isystem.monitor.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ClusterDao;
import com.huawei.sharedrive.isystem.monitor.dao.ClusterInstanceDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskDao;
import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskIODao;
import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoDao;
import com.huawei.sharedrive.isystem.monitor.dao.SystemClusterInfoDao;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;
import com.huawei.sharedrive.isystem.monitor.domain.MonitorType;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;
import com.huawei.sharedrive.isystem.monitor.service.MonitorDBservice;

@Service("monitorDBservice")
public class MonitroDBserviceImpl implements MonitorDBservice
{
    @Autowired
    private SystemClusterInfoDao systemClusterInfoDao;
    
    @Autowired
    private ClusterDao clusterDao;
    
    @Autowired
    private ClusterInstanceDao clusterInstanceDao;
    
    @Autowired
    private NodeDiskDao nodeDiskDao;
    
    @Autowired
    private NodeDiskIODao nodeDiskIODao;
    
    @Autowired
    private NodeDao nodeDao;
    
    @Autowired
    private ProcessInfoDao processInfoDao;
    
    @Override
    public List<ClusterInstance> getServiceNodes(String cluserName, String clusterServiceName)
    {
        ClusterInstance clusterInstance = new ClusterInstance();
        clusterInstance.setClusterName(cluserName);
        clusterInstance.setClusterServiceName(clusterServiceName);
        return clusterInstanceDao.getMysqlNodes(clusterInstance);
    }
    
    @Override
    public NodeRunningInfo getOneNodeInfo(String hostName)
    {
        return nodeDao.getOneNode(hostName);
    }
    
    @Override
    public List<NodeDisk> getDisks(String hostName)
    {
        return nodeDiskDao.getNodeDisks(hostName);
    }
    
    @Override
    public List<NodeDiskIO> getDiskIOs(String hostName)
    {
        return nodeDiskIODao.getNodeDiskIOs(hostName);
    }
    
    @Override
    public List<ProcessInfo> getNodeProcess(String hostName)
    {
        List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>(10);
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setHostName(hostName);
        ProcessInfo pInfo = null;
        for (MonitorType type : MonitorType.values())// 目前只有MYSQL
        {
            processInfo.setProcessName(type.name());
            pInfo = processInfoDao.get(processInfo);
            if (pInfo != null)
            {
                processInfos.add(pInfo);
            }
        }
        return processInfos;
    }
    
    @Override
    public List<SystemClusterInfo> getAllClusterName()
    {
        return systemClusterInfoDao.getAllSystemClusterInfos();
    }
    
    @Override
    public SystemClusterInfo getSystemName(String clusterName )
    {
        return systemClusterInfoDao.getSystemName(clusterName);
    }
    
    @Override
    public List<NodeRunningInfo> getClusterNodes(String clusterName)
    {
        return nodeDao.getClusterNodes(clusterName);
    }
    
    @Override
    public List<Cluster> getClusterServeices(String clusterName)
    {
        
        return clusterDao.getClusterServices(clusterName);
    }
}
