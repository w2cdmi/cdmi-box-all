/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pw.cdmi.common.config.service.ConfigListener;

/**
 * @author q90003805
 * 
 */
@Component("configChangeListener")
public class ConfigChangeListener implements ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChangeListener.class);
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.common.config.service.ConfigListener#configChanged(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void configChanged(String key, Object value)
    {
        // TODO Auto-generated method stub
        LOGGER.info("receive a config change, key:" + key + ", value:" + value);
    }
    
}
