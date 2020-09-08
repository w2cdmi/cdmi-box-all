package com.huawei.sharedrive.app.plugins.cluster.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceCluster;

/**
 * 外挂服务集群DAO
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public interface PluginServiceClusterDAO
{
    /**
     * 保存外挂服务集群信息
     * 
     * @param cluster
     */
    int create(PluginServiceCluster cluster);
    
    /**
     * 根据id删除服务集群
     * 
     * @param clusterId
     * @return
     */
    int delete(int clusterId);
    
    /** 
     * 根据ID查询
     * 
     * @param clusterId 
     */
    PluginServiceCluster get(int clusterId);
    
    /**
     * 查询处理dss数据的外挂服务集群
     * 
     * @param dssId
     * @param appId
     * @return
     */
    PluginServiceCluster getByAppIdAndRouteInfo(int dssId, String appId);
    
    /**
     * 根据外挂应用id列举外挂服务集群
     * 
     * @param appId
     * @return
     */
    List<PluginServiceCluster> listByAppId(String appId);
    
    /**
     * 更新服务集群信息
     * 
     * @param cluster
     * @return
     */
    int update(PluginServiceCluster cluster);
    
    /**
     * 更新服务集群状态和最后监控时间
     * 
     * @param status
     * @param lastMonitorTime
     * @param clusterId
     * @return
     */
    int updateStatusAndLastMonitorTime(byte status, Date lastMonitorTime, int clusterId);
}
