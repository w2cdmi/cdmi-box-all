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
package com.huawei.sharedrive.uam.authapp.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.authapp.dao.AuthAppNetRegionIpDao;
import com.huawei.sharedrive.uam.authapp.domain.AuthAppNetRegionIp;
import com.huawei.sharedrive.uam.authapp.service.AppNetRegionIpIdGenerateService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppNetRegionIpService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.IpUtils;

@Service("authAppNetRegionIpService")
public class AuthAppNetRegionIpServiceImpl implements AuthAppNetRegionIpService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAppNetRegionIpServiceImpl.class);
    
    @Autowired
    private AuthAppNetRegionIpDao authAppNetRegionIpDao;
    
    @Autowired
    private AppNetRegionIpIdGenerateService appNetRegionIpIdGenerateService;
    
    @Override
    public AuthAppNetRegionIp getAuthAppNetRegionIpConfig(long configId)
    {
        return authAppNetRegionIpDao.queryByConfigId(configId);
    }
    
    @Override
    public AuthAppNetRegionIp getAuthAppNetRegionIpConfig(String appId, String ip)
    {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(ip))
        {
            LOGGER.warn("appId or ip is null. appId : {}; ip: {}", appId, ip);
            return null;
        }
        
        return authAppNetRegionIpDao.queryByIp(appId, IpUtils.toLong(ip));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(long configId)
    {
        authAppNetRegionIpDao.deleteById(configId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByAuthAppId(String authAppId)
    {
        authAppNetRegionIpDao.deleteByAuthAppId(authAppId);
    }
    
    @Override
    public int queryTotalCountByAuthAppId(String authAppId)
    {
        return authAppNetRegionIpDao.queryTotalCountByAuthAppId(authAppId);
    }
    
    @Override
    public List<AuthAppNetRegionIp> listAllNetworkRegion(String appId)
    {
        return authAppNetRegionIpDao.listAllNetworkRegion(appId);
    }
    
    @Override
    public Page<AuthAppNetRegionIp> listNetworkRegion(String appId, Integer regionId, PageRequest pageRequest)
    {
        int total = authAppNetRegionIpDao.getCount(appId, regionId);
        List<AuthAppNetRegionIp> list = authAppNetRegionIpDao.listNetworkRegion(appId,
            regionId,
            null != pageRequest ? pageRequest.getLimit() : null);
        return new PageImpl<AuthAppNetRegionIp>(list, pageRequest, total);
    }
    
    @Override
    public void createAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        Date now = new Date();
        authAppNetRegionIp.setCreatedAt(now);
        authAppNetRegionIp.setModifiedAt(now);
        
        authAppNetRegionIp.setId(appNetRegionIpIdGenerateService.getNextId());
        
        authAppNetRegionIpDao.create(authAppNetRegionIp);
    }
    
    @Override
    public void updateAuthAppNetRegionIp(AuthAppNetRegionIp authAppNetRegionIp)
    {
        authAppNetRegionIp.setModifiedAt(new Date());
        
        authAppNetRegionIpDao.update(authAppNetRegionIp);
    }
}
