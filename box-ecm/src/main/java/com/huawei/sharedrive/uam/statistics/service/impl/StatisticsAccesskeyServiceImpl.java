package com.huawei.sharedrive.uam.statistics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.statistics.service.StatisticsAccesskeyService;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.StatisticsConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class StatisticsAccesskeyServiceImpl implements StatisticsAccesskeyService
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Override
    public StatisticsConfig getStatisticsConfig()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(Constants.UAM_DEFAULT_APP_ID,
            null,
            StatisticsConfig.STATISTICS_CONFIG_PREFIX);
        return StatisticsConfig.buildStatisticsConfig(itemList);
    }
    
    @Override
    public void saveStatisticsConfig(StatisticsConfig statisticsConfig)
    {
        statisticsConfig.setAppId(Constants.UAM_DEFAULT_APP_ID);
        List<SystemConfig> itemList = statisticsConfig.toConfigItem();
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
    }
    
}
