package com.huawei.sharedrive.app.plugins.cluster.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.app.plugins.cluster.dao.PluginServiceRouterDAO;
import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository
@SuppressWarnings("deprecation")
public class PluginServiceRouterDAOImpl extends AbstractDAOImpl implements PluginServiceRouterDAO
{
    
    @Override
    public void create(PluginServiceRouter router)
    {
        sqlMapClientTemplate.insert("PluginServiceRouter.create", router);
    }
    
    @Override
    public int delete(int dssId, int clusterId)
    {
        PluginServiceRouter router = new PluginServiceRouter();
        router.setClusterId(clusterId);
        router.setDssId(dssId);
        return sqlMapClientTemplate.delete("PluginServiceRouter.delete", router);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<PluginServiceRouter> listByClusterId(int clusterId)
    {
        PluginServiceRouter router = new PluginServiceRouter();
        router.setClusterId(clusterId);
        return sqlMapClientTemplate.queryForList("PluginServiceRouter.listByClusterId", router);
    }
    
}
