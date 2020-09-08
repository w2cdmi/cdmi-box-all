package com.huawei.sharedrive.uam.adminlog.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.adminlog.dao.UserLogConfigDao;
import com.huawei.sharedrive.uam.adminlog.service.UserLogConfigService;

import pw.cdmi.common.domain.SystemConfig;

@Service("userlogConfigService")
public class UserLogConfigServiceImpl implements UserLogConfigService
{
    @Autowired
    private UserLogConfigDao userLogConfigDao;

    @Override
    public SystemConfig queryConfig(String appId, String id)
    {
        return userLogConfigDao.getByPriKey(appId, id);
    }
    
}
