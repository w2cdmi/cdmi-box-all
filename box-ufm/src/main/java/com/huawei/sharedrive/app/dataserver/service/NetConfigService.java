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

import com.huawei.sharedrive.app.dataserver.domain.NetSegment;

/**
 * 
 * @author s90006125
 * 
 */
public interface NetConfigService
{
    /**
     * 添加网络配置
     * 
     * @param netSegments
     */
    void addNetSegment(List<NetSegment> netSegments);
    
    /**
     * 修改网络配置<br>
     * 目前只能修改网段的起始网段，不能修改所属区域
     * 
     * @param netSegments
     */
    void changeNetSegment(List<NetSegment> netSegments);
    
    /**
     * 删除指定网络配置
     * 
     * @param netSegments
     */
    void deleteNetSegment(List<NetSegment> netSegments);
    
    /**
     * 根据IP，查找所属网段
     * 
     * @param ip
     * @return
     */
    NetSegment findNetSegment(String ip);
    
    /**
     * 获取所有的网络配置
     * 
     * @return
     */
    List<NetSegment> getAllNetSegment();
    
    /**
     * 获取指定区域的网络配置
     * 
     * @param regionID
     * @return
     */
    List<NetSegment> getAllNetSegmentByRegion(int regionID);
}
