package com.huawei.sharedrive.uam.system.service.impl;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.system.service.LogSecurityService;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.SystemConfig;

@Component
public class LogSecurityServiceImpl implements LogSecurityService
{
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    private static final String LOG_SECURITY_KEY = "logSecurity.user.log.visible";
    
    @Override
    public boolean isUserLogVisible()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(Constants.UAM_DEFAULT_APP_ID,
            null,
            LOG_SECURITY_KEY);
        if (itemList == null || itemList.size() <= 0)
        {
            return false;
        }
        return BooleanUtils.toBoolean(itemList.get(0).getValue());
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveLogSecurityConfig(String logSecurity)
    {
        SystemConfig systemConfig = new SystemConfig(Constants.UAM_DEFAULT_APP_ID, LOG_SECURITY_KEY,
            logSecurity);
        if (systemConfigDAO.getByPriKey(Constants.UAM_DEFAULT_APP_ID, systemConfig.getId()) == null)
        {
            systemConfigDAO.create(systemConfig);
        }
        else
        {
            systemConfigDAO.update(systemConfig);
        }
    }
    
}
