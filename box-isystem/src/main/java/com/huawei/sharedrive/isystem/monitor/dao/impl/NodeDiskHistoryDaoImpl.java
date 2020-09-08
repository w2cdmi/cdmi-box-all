package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDisk;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("NodeDiskHistoryDao")
public class NodeDiskHistoryDaoImpl extends CacheableSqlMapClientDAO implements NodeDiskHistoryDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(NodeDisk nodeDisk)
    {
        monitorSqlMapClientTemplate.insert("NodeDiskHistory.insert", nodeDisk);
        
    }
    
}
