package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.ClusterInstanceDao;
import com.huawei.sharedrive.isystem.monitor.domain.ClusterInstance;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/**
 * @author l00357199 20162016-1-5 下午4:45:22
 */
@Service("ClusterInstanceDao")
public class ClusterInstanceDaoImpl extends CacheableSqlMapClientDAO implements ClusterInstanceDao
{
    @Resource(name = "monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    
    @Override
    public void insert(ClusterInstance clusterInstanceInfo)
    {
        monitorSqlMapClientTemplate.insert("ClusterInstance.insert", clusterInstanceInfo);
        
    }
    
    @Override
    public ClusterInstance get(ClusterInstance filter)
    {
        // TODO Auto-generated method stub
        return (ClusterInstance) monitorSqlMapClientTemplate.queryForObject("ClusterInstance.selectOne",
            filter);
    }
    
    @Override
    public void update(ClusterInstance clusterInstance)
    {
        monitorSqlMapClientTemplate.update("ClusterInstance.update", clusterInstance);
        
    }
    
    @Override
    public List<ClusterInstance> getMysqlNodes(ClusterInstance clusterInstance)
    {
        return (List<ClusterInstance>) monitorSqlMapClientTemplate.queryForList("ClusterInstance.selectNodes",
            clusterInstance);
    }
    @Override
    public void deleteClusterInstances(ClusterInstance clusterInstance)
    {
        monitorSqlMapClientTemplate.delete("ClusterInstance.deleteNodes",
            clusterInstance);
    }
}
