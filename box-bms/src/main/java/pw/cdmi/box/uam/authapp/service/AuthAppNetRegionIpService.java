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
package pw.cdmi.box.uam.authapp.service;

import java.util.List;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authapp.domain.AuthAppNetRegionIp;


public interface AuthAppNetRegionIpService
{
    AuthAppNetRegionIp getAuthAppNetRegionIpConfig(long configId);
    
    AuthAppNetRegionIp getAuthAppNetRegionIpConfig(String appId, String ip);
    
    void deleteById(long configId);
    
    void deleteByAuthAppId(String authAppId);
    
    int queryTotalCountByAuthAppId(String authAppId);
    
    List<AuthAppNetRegionIp> listAllNetworkRegion(String appId);
    
    Page<AuthAppNetRegionIp> listNetworkRegion(String appId, Integer regionId, PageRequest pageRequest);
    
    void createAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp);
    
    void updateAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp);
}
