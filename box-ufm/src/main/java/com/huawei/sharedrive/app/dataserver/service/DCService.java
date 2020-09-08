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
public interface DCService
{
    /**
     * 启用DC
     * 
     * @param dcid
     */
    void activeDataCenter(int dcid);
    
    /**
     * 添加DC
     * 
     * @param name DC名称
     * @param domainName 域名
     * @param managerip 管理IP
     * @param 添加DC 管理端口
     * @param regionid 区域ID
     * @return
     */
    DataCenter addDataCenter(String name, ResourceGroup resourceGroup);
    
    /**
     * 删除DC，只有在未启用前才能删除
     * 
     * @param dcid
     */
    void deleteDataCenter(int dcid);
    
    /**
     * 通过名称查找
     * 
     * @param name
     * @return
     */
    DataCenter findByName(String name);
    
    /**
     * 获取DC详情<br>
     * 包括该DC的资源组和各个节点列表
     * 
     * @param dcid
     * @return
     */
    DataCenter getDataCenter(int dcid);
    
    /**
     * 获取所有DC列表，及其详细信息
     * 
     * @return
     */
    List<DataCenter> listDataCenter();
    
    /**
     * 获取所有DC列表
     * 
     * @return
     */
    List<DataCenter> listDataCenter(int regionID);
    
    /**
     * 生成一个新的DC ID
     * 
     * @return
     */
    int newDCId();
    
    List<ResourceGroupNode> getNodeList(int resourceGroupId);
    
    /**
     * 获取被设为优先下载的datacenter
     * 
     * @return
     */
    List<DataCenter> listPriorityDataCenter();
}
