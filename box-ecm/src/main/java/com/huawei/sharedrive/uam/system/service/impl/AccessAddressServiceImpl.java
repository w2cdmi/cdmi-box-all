package com.huawei.sharedrive.uam.system.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.system.service.AccessAddressService;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class AccessAddressServiceImpl implements AccessAddressService, ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessAddressServiceImpl.class);
    
    private AccessAddressConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.service.AccessAddressService#getAccessAddress()
     */
    @Override
    public AccessAddressConfig getAccessAddress()
    {
        if (localCache == null || StringUtils.isEmpty(localCache.getUfmInnerAddress())
            || StringUtils.isEmpty(localCache.getUamOuterAddress())
            || StringUtils.isEmpty(localCache.getUfmOuterAddress()))
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(Constants.UAM_DEFAULT_APP_ID,
                null,
                AccessAddressConfig.ACCESS_ADDRESS_CONFIG_PREFIX);
            localCache = AccessAddressConfig.buildAccessAddressConfig(itemList);
        }
        return localCache;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.service.AccessAddressService#saveAccessAddress
     * (com.huawei.sharedrive.common.domain.AccessAddressConfig)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAccessAddress(AccessAddressConfig accessAddressConfig)
    {
        accessAddressConfig.setAppId(Constants.UAM_DEFAULT_APP_ID);
        List<SystemConfig> itemList = accessAddressConfig.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.getByPriKey(Constants.UAM_DEFAULT_APP_ID, systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        localCache = accessAddressConfig;
        configManager.setConfig(AccessAddressConfig.class.getSimpleName(), accessAddressConfig);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(AccessAddressConfig.class.getSimpleName()))
        {
            LOGGER.info("Change AccessAddressConfig By Cluseter Notify.");
            localCache = (AccessAddressConfig) value;
        }
    }
}
