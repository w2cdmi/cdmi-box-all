/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.isystem.logfile.service;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.logfile.domain.FSEndpoint;
import com.huawei.sharedrive.isystem.logfile.domain.LogAgentNode;

/**
 * 
 * @author s90006125
 *
 */
public interface LogAgentService
{
    /**
     * 获取指定集群的日志归档存储配置
     * @param clusterId
     * @return
     */
    FSEndpoint getFSEndpointByClusterId(int clusterId);
    
    /**
     * 为集群设置日志归档存储
     * @param fsType
     * @param clusterId
     */
    void setFSEndpointForCluster(int clusterId, String fsType, String endpoint);
    
    /**
     * 获取logAgent节点列表
     * @param clusterId
     * @return
     */
    List<LogAgentNode> getLogAgentNodeList(int clusterId);
    
    /**
     * 判断该DC是否是合并部署的DC
     * @param clusterId
     * @return
     */
    boolean isMergeDC(int clusterId);
    /**
     * 列举DC列表，并剔除合布的DC
     * @return
     */
    List<DataCenter> listDataCenterWithoutMergeDC(int regionId);
}
