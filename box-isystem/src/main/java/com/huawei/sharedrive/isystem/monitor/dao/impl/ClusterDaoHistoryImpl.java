package com.huawei.sharedrive.isystem.monitor.dao.impl;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ClusterHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/**
 * @author l00357199 20162016-1-5 下午4:40:19
 */
@Service("ClusterHistoryDao")
public class ClusterDaoHistoryImpl extends CacheableSqlMapClientDAO implements ClusterHistoryDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(Cluster clusterInfo)
    {
        monitorSqlMapClientTemplate.insert("ClusterHistory.insert", clusterInfo);
    }
    
}
