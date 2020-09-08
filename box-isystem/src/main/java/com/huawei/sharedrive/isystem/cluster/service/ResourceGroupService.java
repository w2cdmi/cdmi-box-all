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

import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;

/**
 * 
 * @author s90006125
 * 
 */
public interface ResourceGroupService
{
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
     * 删除指定DC下的所有资源组
     * 
     * @param dcid
     */
    void deleteByDC(int dcid);
    
    /**
     * 修改区域<br>
     * 当DC变更区域的时候，所有资源组需要同步更新
     * 
     * @param dcid
     */
    void updateRegion(int dcid, int regionID);
    
    /**
     * 修改状态<br>
     * 根据区域ID，更新该区域下的所有资源组状态
     * 
     * @param dcid
     * @param status
     */
    void updateStatus(int dcid, ResourceGroup.Status status);
    
    /**
     * 修改数据中心读写状态：0读写，1只读
     * @param dcid
     * @param rwStatus
     */
    void updateRWStatus(int dcid, ResourceGroup.RWStatus rwStatus);
}
