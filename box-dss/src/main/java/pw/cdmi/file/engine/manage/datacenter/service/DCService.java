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
package pw.cdmi.file.engine.manage.datacenter.service;

import java.util.List;

import pw.cdmi.file.engine.manage.datacenter.domain.ClusterNode;
import pw.cdmi.file.engine.manage.datacenter.domain.ResourceGroup;

/**
 * DC服务类
 * 
 * @author s90006125
 * 
 */
public interface DCService
{
    /**
     * 初始化DC，允许重复调用
     * 
     * @param dcid 新分配的DC 的ID
     * @param newkey 新的访问密钥
     * @param oldkey 原始的访问密钥
     * @param reportip 状态上报地址
     * @param reportport 状态上报端口
     */
    ResourceGroup init(int dcid, String reportip, int reportport, String getProtocol, String putProtocol);
    
    /**
     * 重置DC
     * 
     */
    void reset();
    
    /**
     * 激活DC，激活前需要先检查是否有已启用的存储，如果没有，则无法激活
     * 
     */
    void active();
    
    /**
     * 获取当前集群信息，包括该集群下的节点信息
     * 
     * @return
     */
    ResourceGroup getResourceGroup();
    
    /**
     * 获取当前集群信息，包括该集群下的节点信息
     * 
     * @return
     */
    ResourceGroup getAndRefreshStatus();
    
    /**
     * 获取所有集群节点
     * 
     * @return
     */
    List<ClusterNode> getClusterNodeList();
    
    /**
     * 更新节点
     * 
     * @param node
     */
    void updateNodeStatus(ClusterNode node);
    
    /**
     * 获取当前DC的ID
     * 
     * @return
     */
    Integer getDCID();
    
    /**
     * 获取指定node的信息
     * 
     * @param name
     * @return
     */
    ClusterNode getClusterNode(String name);
    
    /**
     * 保存node
     * 
     * @param node
     */
    void saveClusterNode(ClusterNode node);
    
    /**
     * 获取上报地址
     * 
     * @return
     */
    String getReportAddr();
    
    /**
     * 获取上报端口
     * 
     * @return
     */
    int getReportPort();
}
