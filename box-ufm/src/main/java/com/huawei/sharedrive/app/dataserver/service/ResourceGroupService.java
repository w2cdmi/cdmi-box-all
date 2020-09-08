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
package com.huawei.sharedrive.app.dataserver.service;

import java.util.List;

import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroupNode;

/**
 * 
 * @author s90006125
 * 
 */
public interface ResourceGroupService
{
    /**
     * 创建新的group
     * 
     * @param dataCenter
     * @param managerIp
     * @param managerPort
     * @param serviceAddr
     * @param servicePath
     * @param natAddr
     * @param natPath
     * @return
     */
    ResourceGroup createNewGroup(DataCenter dataCenter,ResourceGroup group);
    
    /**
     * 创建一个新的节点
     * 
     * @param group
     * @param managerIp
     * @param managerPort
     * @param innerAddr
     * @param serviceAddr
     * @return
     */
    ResourceGroupNode createNewNode(ResourceGroup group, ResourceGroupNode node);
    
    /**
     * 查找
     * 
     * @param managerIP
     * @param managerPort
     * @return
     */
    ResourceGroup findGroup(String managerIP, int managerPort);
    
    /**
     * 获取资源组
     * 
     * @param groupid
     * @return
     */
    ResourceGroup getResourceGroup(int groupid);
    
    /**
     * 删除指定DC下的所有资源组
     * 
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    /**
     * 处理上报事件
     * 
     * @param newGroup
     */
    boolean handleReport(ResourceGroup newGroup);
    
    /**
     * 获取所有资源组
     * 
     * @param dcid
     * @return
     */
    List<ResourceGroup> listAllGroups();
    
    /**
     * 通过DC ID获取resourcegroup列表
     * 
     * @param dcid
     * @return
     */
    List<ResourceGroup> listGroupsByDC(int dcid);
    
    /**
     * 通过regionID获取resourcegroup列表
     * 
     * @param dcid
     * @return
     */
    List<ResourceGroup> listGroupsByRegion(int regionID);
    
    /**
     * 更新DC域名
     * 
     * @param dcid
     * @param domainName
     */
    void updateDomainNameByDc(int dcid, String domainName);
    
    /**
     * 修改运行时状态<br>
     * 根据区域ID，更新该区域下的所有资源组状态
     * 
     * @param dcid
     * @param status
     */
    void updateRuntimeStatus(int dcid, ResourceGroup.RuntimeStatus status);
    
    /**
     * 修改状态<br>
     * 根据区域ID，更新该区域下的所有资源组状态
     * 
     * @param dcid
     * @param status
     */
    void updateStatus(int dcid, ResourceGroup.Status status);
    
    /**
     * 根据节点查找节点
     * @param managerIp
     * @return
     */
    ResourceGroupNode getResourceGroupNodeByManagerIp(String managerIp);
}
