package com.huawei.sharedrive.isystem.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.system.service.DirectChainService;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.DirectChainConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component("directChainService")
public class DirectChainServiceImpl implements DirectChainService
{
    public static final String SECMATRIX_KEY = "matrix.security.check";
    
    public static final String SYSTEM_CONFIG_CHANGE = "system_config_change";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public void save(DirectChainConfig directChain, String secmatrix)
    {
        saveSecmatrix(directChain, secmatrix);
        if (secmatrix != null)
        {
            configManager.setConfig(SYSTEM_CONFIG_CHANGE, SECMATRIX_KEY); 
        }
    }
    
    @Override
    public DirectChainConfig getSecmatrixn()
    {
        SystemConfig systemConfig = systemConfigDAO.get(DirectChainServiceImpl.SECMATRIX_KEY);
        DirectChainConfig directChainConfig = new DirectChainConfig();
        if (systemConfig != null)
        {
            directChainConfig.setAppId(systemConfig.getAppId());
            directChainConfig.setPath(systemConfig.getValue());
            return directChainConfig;
        }
        return null;
    }
    
    @Override
    public DirectChainConfig getDirectChain()
    {
        SystemConfig systemConfig = systemConfigDAO.get(DirectChainConfig.DIRECT_CHAIN_CONFIG);
        DirectChainConfig directChainConfig = new DirectChainConfig();
        if (systemConfig != null)
        {
            directChainConfig.setAppId(systemConfig.getAppId());
            directChainConfig.setPath(systemConfig.getValue());
            return directChainConfig;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSecmatrix(DirectChainConfig directChain, String secmatrix)
    {

        if (directChain != null)
        {
            SystemConfig systemConfig = directChain.toConfigItem();
            SystemConfig oldSystemConfig = systemConfigDAO.get(systemConfig.getId());
            if (oldSystemConfig == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                oldSystemConfig.setValue(systemConfig.getValue());
                systemConfigDAO.update(oldSystemConfig);
            }
        }
        
        if (secmatrix != null)
        {
            SystemConfig secmatrixconfig = new SystemConfig("-1", DirectChainServiceImpl.SECMATRIX_KEY,
                secmatrix);
            SystemConfig oldSystemConfig = systemConfigDAO.get(secmatrixconfig.getId());
            if (oldSystemConfig == null)
            {
                systemConfigDAO.create(secmatrixconfig);
            }
            else
            {
                oldSystemConfig.setValue(secmatrixconfig.getValue());
                systemConfigDAO.update(oldSystemConfig);
            }
        }
    }
}
