package com.huawei.sharedrive.uam.adminlog.dao.impl;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.adminlog.service.impl.UserLogConfigServiceImpl;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.log.LogConstants;
import pw.cdmi.core.utils.SpringContextUtil;

public final class LogLanguageHelper
{
    private LogLanguageHelper()
    {
    }
    
    private static final String LOCAL_ZH = "zh";
    
    private static final String SAVE_USER_LOG = "1";
    
    public static Locale getLanguage()
    {
        UserLogConfigServiceImpl userlogConfigService = (UserLogConfigServiceImpl) SpringContextUtil.getBean("userlogConfigService");
        SystemConfig systemLanguage = userlogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_CONFIG_LANGUAGE);
        if (null == systemLanguage)
        {
            return Locale.ENGLISH;
        }
        if (StringUtils.equals(systemLanguage.getValue(), LOCAL_ZH))
        {
            return Locale.CHINESE;
        }
        return Locale.ENGLISH;
    }
    
    public static boolean isSaveUserLog()
    {
        UserLogConfigServiceImpl userlogConfigService = (UserLogConfigServiceImpl) SpringContextUtil.getBean("userlogConfigService");
        SystemConfig systemLanguage = userlogConfigService.queryConfig(LogConstants.DERAULT_APPID,
            LogConstants.USER_LOG_ISCONFIG);
        if (null == systemLanguage)
        {
            return false;
        }
        return SAVE_USER_LOG.equals(systemLanguage.getValue());
    }
}
