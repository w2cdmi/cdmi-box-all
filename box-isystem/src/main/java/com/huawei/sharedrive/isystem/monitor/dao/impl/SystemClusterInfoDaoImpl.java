package com.huawei.sharedrive.isystem.monitor.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.monitor.dao.SystemClusterInfoDao;
import com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

/**
 * @author l00357199 20162016-1-5 下午4:40:19
 */
@Service("SystemClusterInfoDao")
public class SystemClusterInfoDaoImpl extends CacheableSqlMapClientDAO implements SystemClusterInfoDao
{
    @Resource(name="monitorSqlMapClientTemplate")
    private SqlMapClientTemplate monitorSqlMapClientTemplate;
    @Override
    public void insert(SystemClusterInfo systemClusterInfo)
    {
        monitorSqlMapClientTemplate.insert("SystemClusterInfo.insert", systemClusterInfo);
        
    }

    @Override
    public List<SystemClusterInfo> getAllSystemClusterInfos()
    {
        // TODO Auto-generated method stub
        return (List<SystemClusterInfo>)monitorSqlMapClientTemplate.queryForList("SystemClusterInfo.getAll");
    }

    @Override
    public SystemClusterInfo get(SystemClusterInfo systemClusterInfo)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**查看clusterName对应的systemName
     * @param clusterName
     * @return
     */
    @Override
    public SystemClusterInfo getSystemName(String clusterName )
    {
        // TODO Auto-generated method stub
        return(SystemClusterInfo)monitorSqlMapClientTemplate.queryForObject("SystemClusterInfo.getSystemName", clusterName);
    }
    @Override
    public void update(SystemClusterInfo systemClusterInfo)
    {
        // TODO Auto-generated method stub
        
    }
    
    /* 更新状态（只要改集群的节点或者服务异常，节点状态就异常 ）
     * @see com.huawei.sharedrive.isystem.monitor.dao.SystemClusterInfoDao#update(com.huawei.sharedrive.isystem.monitor.domain.SystemClusterInfo)
     */
    @Override
    public void updateStatus(SystemClusterInfo s)
    {
        // TODO Auto-generated method stub
        monitorSqlMapClientTemplate.update("SystemClusterInfo.updateStatus", s);
    }
    
}
