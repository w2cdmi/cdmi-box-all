package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDiskIOHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeDiskIO;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("NodeDiskIOHistoryDao")
public class NodeDiskIOHistoryDaoImpl extends CacheableSqlMapClientDAO implements NodeDiskIOHistoryDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(NodeDiskIO nodeDiskIO)
    {
        monitorSqlMapClientTemplate.insert("NodeDiskIOHistory.insert", nodeDiskIO);
        
    }
    
}
