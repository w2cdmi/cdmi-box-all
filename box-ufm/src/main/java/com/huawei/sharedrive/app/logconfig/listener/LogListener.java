package com.huawei.sharedrive.app.logconfig.listener;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LogConstants;
import pw.cdmi.common.log.LogLanguage;

@Component("logListener")
public class LogListener implements ConfigListener
{
    private static Logger logger = LoggerFactory.getLogger(LogListener.class);
    
    private static String enable = null;
    
    private static String language = null;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @PostConstruct
    public void init()
    {
        Limit limit = new Limit();
        limit.setOffset(0L);
        limit.setLength(1);
        List<SystemConfig> enalbeConfigList = systemConfigDAO.getByPrefix(limit, LogLanguage.USER_LOG_ISCONFIG);
        if(CollectionUtils.isNotEmpty(enalbeConfigList))
        {
            setEnable(enalbeConfigList.get(0).getValue());
        }
        else
        {
            setEnable("0");
        }
        List<SystemConfig> langConfigList = systemConfigDAO.getByPrefix(limit, LogLanguage.USER_LOG_CONFIG_LANGUAGE);
        if(CollectionUtils.isNotEmpty(langConfigList))
        {
            setLanguage(langConfigList.get(0).getValue());
        }
        else
        {
            setLanguage("en");
        }
    }
    
    private static void setEnable(String enable)
    {
        LogListener.enable = enable;
    }
    
    private static void setLanguage(String language)
    {
        LogListener.language = language;
    }
    
    public static boolean isEnable()
    {
        if(null == enable)
        {
            return false;
        }
        return StringUtils.equals(enable, "1");
    }
    
    public static Locale getLanguage()
    {
        if(null == language)
        {
            return Locale.ENGLISH;
        }
        if(StringUtils.equals(language, "zh"))
        {
            return Locale.CHINESE;
        }
        return Locale.ENGLISH;
    }
    
    @Override
    public void configChanged(String key, Object valObject)
    {
        if (key.equals(LogConstants.ZK_LOGLANGUAGE_KEY))
        {
            logger.info("[logLog] receive the request to update log configuration.");
            init();
        }
    }
    
}
