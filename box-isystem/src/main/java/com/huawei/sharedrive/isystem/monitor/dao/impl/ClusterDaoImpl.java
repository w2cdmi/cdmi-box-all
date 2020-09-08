package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ClusterDao;
import com.huawei.sharedrive.isystem.monitor.domain.Cluster;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/**
 * @author l00357199 20162016-1-5 下午4:40:19
 */
@Service("ClusterDao")
public class ClusterDaoImpl extends CacheableSqlMapClientDAO implements ClusterDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(Cluster clusterInfo)
    {
        monitorSqlMapClientTemplate.insert("Cluster.insert", clusterInfo);
    }
    
    @Override
    public List<Cluster> getFilterd(Cluster filter)
    {
        
        return (List<Cluster>) monitorSqlMapClientTemplate.queryForList("Cluster.getFilterd", filter);
        
    }
    
    @Override
    public Cluster getOne(Cluster filter)
    {
        // TODO Auto-generated method stub
        return (Cluster) monitorSqlMapClientTemplate.queryForObject("Cluster.selectOne", filter);
    }
    
    @Override
    public void update(Cluster cluster)
    {
        monitorSqlMapClientTemplate.update("Cluster.update", cluster);
    }
    
    @Override
    public List<Cluster> getClusterServices(String clusterName)
    {
        // TODO Auto-generated method stub
        return (List<Cluster>) monitorSqlMapClientTemplate.queryForList("Cluster.selectClusterServices",
            clusterName);
    }
    
}
