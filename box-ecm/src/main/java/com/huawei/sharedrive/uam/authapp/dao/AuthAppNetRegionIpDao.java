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
package com.huawei.sharedrive.uam.authapp.dao;

import java.util.List;

import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIp;

import pw.cdmi.box.domain.Limit;

public interface AuthAppNetRegionIpDao
{
    AuthAppNetRegionIp queryByConfigId(long id);
    
    AuthAppNetRegionIp queryByIp(String appId, long ip);
    
    int queryTotalCountByAuthAppId(String appId);
    
    void deleteById(long configId);
    
    void deleteByAuthAppId(String authAppId);
    
    int getCount(String appId, Integer regionId);
    
    List<AuthAppNetRegionIp> listAllNetworkRegion(String appId);
    
    List<AuthAppNetRegionIp> listNetworkRegion(String appId, Integer regionId, Limit limit);
    
    long getMaxId();
    
    void create(AuthAppNetRegionIp authAppNetRegionIp);
    
    void update(AuthAppNetRegionIp authAppNetRegionIp);
}
