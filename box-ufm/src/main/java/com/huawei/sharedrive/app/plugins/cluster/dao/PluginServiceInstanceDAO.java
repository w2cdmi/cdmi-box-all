package com.huawei.sharedrive.app.plugins.cluster.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.plugins.cluster.domain.PluginServiceInstance;

/**
 * 外挂服务实例DAO
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-10
 * @see
 * @since
 */
public interface PluginServiceInstanceDAO
{
    /**
     * 保存外挂服务实例
     * 
     * @param instance
     */
    void create(PluginServiceInstance instance);
    
    /**
     * 列举集群下的服务实例
     * 
     * @param clusterId
     * @param type 实例类型
     * @return
     */
    List<PluginServiceInstance> listByClusterId(int clusterId, byte type);
    
    /**
     * 更新实例状态和最后监控时间
     * 
     * @param status
     * @param lastMonitorTime
     * @param ip
     * @param clusterId
     * @return
     */
    int updateStatusAndLastMonitorTime(byte status, Date lastMonitorTime, String ip, int clusterId);

    int delete(PluginServiceInstance instance);

    int updateInstance(PluginServiceInstance instance);
}
