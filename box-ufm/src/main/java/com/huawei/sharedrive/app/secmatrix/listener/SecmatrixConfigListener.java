package com.huawei.sharedrive.app.secmatrix.listener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;

public class SecmatrixConfigListener implements ConfigListener
{
    public static final Logger LOGGER = LoggerFactory.getLogger(SecmatrixConfigListener.class);
    
    public static final String KEY = "matrix.security.check";
    
    public static final String SYSTEM_CONFIG_CHANGE = "system_config_change";
    
    private static Boolean secmatrix = false;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    public static Boolean getSecmatrix()
    {
        return secmatrix;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        
        if (SYSTEM_CONFIG_CHANGE.equals(key)&&StringUtils.isNotBlank(KEY)&&KEY.equals(value))
        {
            LOGGER.info("[SecmatrixConfigListener] receive the request to update Secmatrix configuration.");
            init();
        }
        
    }
    
    public void init()
    {
        systemConfigDAO.invalidateCache(KEY);
        SystemConfig config = systemConfigDAO.get(KEY);
        if (config != null)
        {
            setSecmatrix(Boolean.parseBoolean(config.getValue()));
        }
        else
        {
            setSecmatrix(Boolean.FALSE);
        }
        LOGGER.info("secmatrix.config  sysconfig  value is " + secmatrix);
    }
    
    private static void setSecmatrix(Boolean secmatrix)
    {
        SecmatrixConfigListener.secmatrix = secmatrix;
    }
}
