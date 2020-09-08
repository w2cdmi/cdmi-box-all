package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.NodeHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
@Service("NodeHistoryDao")
public class NodeHistoryDaoImpl extends CacheableSqlMapClientDAO implements NodeHistoryDao
{
    @Resource(name="monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    @Override
    public void insert(NodeRunningInfo nodeRunningInfo)
    {
        
        monitorSqlMapClientTemplate.insert("NodeHistory.insert",nodeRunningInfo);
    }

   
}
