package com.huawei.sharedrive.app.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;

@Service
public class SystemConfigServiceImpl implements SystemConfigService
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public SystemConfig getConfig(String key)
    {
        return systemConfigDAO.get(key);
    }

    @Override
    public List<SystemConfig> getByPrefix(Limit limit, String prefix)
    {
        return systemConfigDAO.getByPrefix(limit, prefix);
    }
    
}
