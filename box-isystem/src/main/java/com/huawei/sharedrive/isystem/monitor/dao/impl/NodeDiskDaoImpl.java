package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("NodeDiskDao")
public class NodeDiskDaoImpl extends CacheableSqlMapClientDAO implements NodeDiskDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(NodeDisk nodeDisk)
    {
        monitorSqlMapClientTemplate.insert("NodeDisk.insert", nodeDisk);
        
    }
    
    @Override
    public void update(NodeDisk nodeDisk)
    {
        monitorSqlMapClientTemplate.insert("NodeDisk.update", nodeDisk);
        
    }
    
    @Override
    public NodeDisk getOneDisk(NodeDisk nodeDisk)
    {
        // TODO Auto-generated method stub
        return (NodeDisk) monitorSqlMapClientTemplate.queryForObject("NodeDisk.selectOne", nodeDisk);
    }
    
    @Override
    public List<NodeDisk> getNodeDisks(String hostName)
    {
        // TODO Auto-generated method stub
        return (List<NodeDisk>) monitorSqlMapClientTemplate.queryForList("NodeDisk.selectNodeDisks", hostName);
    }
}
