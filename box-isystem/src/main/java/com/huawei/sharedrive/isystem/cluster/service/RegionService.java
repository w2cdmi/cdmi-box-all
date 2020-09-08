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

import com.huawei.sharedrive.isystem.cluster.domain.Region;

/**
 * 
 * @author s90006125
 * 
 */
public interface RegionService
{
    /**
     * 获取指定的区域信息
     * 
     * @param regionid
     * @return
     */
    Region getRegion(int regionid);
    
    /**
     * 根据区域代码获取指定的区域信息
     * 
     * @param code
     * @return
     */
    Region getRegionByCode(String code);
    
    /**
     * 获取默认区域
     * 
     * @param regionid
     * @return
     */
    Region getDefaultRegion();
    
    /**
     * 获取区域列表
     * 
     * @return
     */
    List<Region> listRegion();
    
    /**
     * 添加区域
     * 
     * @param name
     * @param code
     * @param description
     */
    void addRegion(String name, String code, String description);
    
    /**
     * 修改region
     * 
     * @param region
     */
    void changeRegion(int id, String name, String code, String description);
    
    /**
     * 删除region<br>
     * 如果区域下已经对接了DC，则不允许删除
     * 
     * @param region
     */
    void deleteRegion(int regionID);
    
    /**
     * 设置指定区域为默认区域
     * 
     * @param regionID
     */
    void setDefaultRegion(int regionID);
}
