package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ClusterInstanceHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/**
 * @author l00357199 20162016-1-5 下午4:45:22
 */
@Service("ClusterInstanceHistoryDao")
public class ClusterInstanceDaoHistoryImpl extends CacheableSqlMapClientDAO implements
    ClusterInstanceHistoryDao
{
    
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(ClusterInstance clusterInstanceInfo)
    {
        monitorSqlMapClientTemplate.insert("ClusterInstanceHistory.insert", clusterInstanceInfo);
        
    }
    
}
