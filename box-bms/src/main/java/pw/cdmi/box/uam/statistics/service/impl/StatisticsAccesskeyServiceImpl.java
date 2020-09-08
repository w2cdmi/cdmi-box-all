package pw.cdmi.box.uam.statistics.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.domain.StatisticsConfig;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class StatisticsAccesskeyServiceImpl implements StatisticsAccesskeyService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsAccesskeyServiceImpl.class);
    
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
        LOGGER.info("change crypt in bms.StatisticsAccesskeyServiceImpl");
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
