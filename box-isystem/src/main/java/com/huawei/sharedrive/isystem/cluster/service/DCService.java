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
package com.huawei.sharedrive.isystem.cluster.service;

import java.util.List;

import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;

/**
 * 
 * @author s90006125
 * 
 */
public interface DCService
{
    
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
     * 获取地域下的默认DC详情<br>
     * 包括该DC的资源组和各个节点列表
     * 
     * @param dcid
     * @return
     */
    DataCenter getSingleDataCenter(int regionId);
    
    /**
     * 获取所有DC列表，及其详细信息
     * 
     * @return
     */
    List<DataCenter> listDataCenter();
    
    /**
     * 获取所有DC列表，及其详细信息
     * 
     * @return
     */
    List<DataCenter> listDataCenterRe(int regionID);
    
    /**
     * 获取所有DC列表
     * 
     * @return
     */
    List<DataCenter> listDataCenter(int regionID);
    
    /**
     * 更新DC所属区域
     * 
     * @param dcid
     * @param regionid
     */
    void updateRegion(int dcid, int regionid);

    void setPriority(int regionid, int dcid);

    void setPriorityDefault(int regionid, int dcid);
    
}
