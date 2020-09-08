package com.huawei.sharedrive.app.plugins.cluster.dao;

import java.util.List;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceRouter;

/**
 * 外挂服务路由信息DAO
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public interface PluginServiceRouterDAO
{
    /**
     * 保存路由信息
     * 
     * @param router
     */
    void create(PluginServiceRouter router);
    
    /**
     * 删除路由信息
     * 
     * @param dssId
     * @param clusterId
     * @return
     */
    int delete(int dssId, int clusterId);
    
    /**
     * 列举路由信息
     * 
     * @param clusterId
     * @return
     */
    List<PluginServiceRouter> listByClusterId(int clusterId);
    
}
