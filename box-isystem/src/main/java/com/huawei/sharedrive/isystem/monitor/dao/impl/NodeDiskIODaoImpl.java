package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskIODao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("NodeDiskIODao")
public class NodeDiskIODaoImpl extends CacheableSqlMapClientDAO implements NodeDiskIODao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(NodeDiskIO nodeDiskIO)
    {
        monitorSqlMapClientTemplate.insert("NodeDiskIO.insert", nodeDiskIO);
        
    }
    
    @Override
    public void update(NodeDiskIO nodeDiskIO)
    {
        monitorSqlMapClientTemplate.insert("NodeDiskIO.update", nodeDiskIO);
        
    }
    
    @Override
    public NodeDiskIO getOneDiskIO(NodeDiskIO nodeDiskIO)
    {
        // TODO Auto-generated method stub
        return (NodeDiskIO) monitorSqlMapClientTemplate.queryForObject("NodeDiskIO.selectOne", nodeDiskIO);
    }
    
    @Override
    public List<NodeDiskIO> getNodeDiskIOs(String hostName)
    {
        // TODO Auto-generated method stub
        return (List<NodeDiskIO>) monitorSqlMapClientTemplate.queryForList("NodeDiskIO.selectNodeDiskIOs",
            hostName);
    }
    
}
