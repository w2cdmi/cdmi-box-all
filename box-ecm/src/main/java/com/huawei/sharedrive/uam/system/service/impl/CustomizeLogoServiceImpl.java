package com.huawei.sharedrive.uam.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.system.dao.CustomizeLogoDAO;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class CustomizeLogoServiceImpl implements CustomizeLogoService, ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(CustomizeLogoServiceImpl.class);
    
    private CustomizeLogo localCache;
    
    @Autowired
    private CustomizeLogoDAO customizeLogoDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Override
    public CustomizeLogo getCustomize()
    {
        if (localCache == null)
        {
            CustomizeLogo tmpLogo = customizeLogoDAO.get(Constants.LOGO_DEFAULT_ID);
            localCache = tmpLogo;
        }
        return localCache;
    }
    
    @Override
    public String getAppEmailTitle()
    {
        if (localCache == null)
        {
            CustomizeLogo tmpLogo = customizeLogoDAO.get(Constants.LOGO_DEFAULT_ID);
            localCache = tmpLogo;
        }
        else
        {
            String appEmailTitle = localCache.getAppEmailTitle();
            return appEmailTitle;
        }
        return "";
    }
    
    @Override
    public void updateCustomize(CustomizeLogo customize)
    {
        customize.setId(Constants.LOGO_DEFAULT_ID);
        CustomizeLogo tmpLogo = customizeLogoDAO.get(Constants.LOGO_DEFAULT_ID);
        if (null == tmpLogo)
        {
            customizeLogoDAO.insert(customize);
        }
        else
        {
            customizeLogoDAO.update(customize);
        }
        localCache = customize;
        AuthApp authApp = authAppService.getByAuthAppID(customize.getAppId());
        if (authApp.getType() == Constants.DEFAULT_WEB_APP_TYPE)
        {
            configManager.setConfig(CustomizeLogo.class.getSimpleName(), customize);
        }
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(CustomizeLogo.class.getSimpleName()))
        {
            logger.info("Reload Icon By Cluster Notify.");
            localCache = (CustomizeLogo) value;
        }
    }
}
