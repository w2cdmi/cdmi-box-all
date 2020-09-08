package pw.cdmi.box.disk.system.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.service.AppBasicConfigService;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class AppBasicConfigServiceImpl implements AppBasicConfigService, ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppBasicConfigServiceImpl.class);
    
    private AppBasicConfig localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public AppBasicConfig getAppBasicConfig(String appId)
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(appId,
                null,
                AppBasicConfig.BASIC_CONFIG_PREFIX);
            localCache = AppBasicConfig.buildAppBasicConfig(itemList);
        }
        return localCache;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(AppBasicConfig.class.getSimpleName()))
        {
            LOGGER.info("Change AppBasicConfig By Cluseter Notify.");
            localCache = (AppBasicConfig) value;
        }
    }
}