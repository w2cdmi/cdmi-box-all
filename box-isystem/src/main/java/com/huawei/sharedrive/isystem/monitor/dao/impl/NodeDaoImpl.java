package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("NodeDao")
public class NodeDaoImpl extends CacheableSqlMapClientDAO implements NodeDao
{
    
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(NodeRunningInfo nodeRunningInfo)
    {
        /*
         * HashMap<String, Object> map = new HashMap<String, Object>();
         * map.put("clusterName", nodeRunningInfo.getClusterName());
         * map.put("hostName",nodeRunningInfo.getHostName()); map.put("managerIp",
         * nodeRunningInfo.getManagerIp()); map.put("reportTime",
         * nodeRunningInfo.getReportTime()); map.put("cpuInfo",
         * nodeRunningInfo.getCpuInfo());
         * map.put("privateNet",nodeRunningInfo.getPrivateNet()); map.put("manageNet",
         * nodeRunningInfo.getManageNet());
         * map.put("serviceNet",nodeRunningInfo.getServiceNet()); map.put("diskInfo",
         * nodeRunningInfo.getDiskInfo()); map.put("memoryInfo",
         * nodeRunningInfo.getMemoryInfo()); map.put("connectTotal",
         * nodeRunningInfo.getConnectTotal());
         * map.put("establishedTotal",nodeRunningInfo.getEstablishedTotal());
         * map.put("fileHandleTotal",nodeRunningInfo.getFileHandleTotal());
         * map.put("topInfo", nodeRunningInfo.getTopInfo());
         * map.put("diskIO",nodeRunningInfo.getDiskIO());
         */
        
        monitorSqlMapClientTemplate.insert("Node.insert", nodeRunningInfo);
    }
    
    @Override
    public void update(NodeRunningInfo nodeRunningInfo)
    {
        monitorSqlMapClientTemplate.update("Node.update", nodeRunningInfo);
    }
    
    @Override
    public NodeRunningInfo getOneNode(String hostName)
    {
        return (NodeRunningInfo) monitorSqlMapClientTemplate.queryForObject("Node.selectOne", hostName);
    }
    
    @Override
    public List<NodeRunningInfo> getClusterNodes(String cluserName)
    {
        // TODO Auto-generated method stub
        return (List<NodeRunningInfo>) monitorSqlMapClientTemplate.queryForList("Node.selectForCluster",
            cluserName);
    }
    
}
