package pw.cdmi.box.uam.statistics.service;

import pw.cdmi.common.domain.StatisticsConfig;

public interface StatisticsAccesskeyService
{
    StatisticsConfig getStatisticsConfig();
    
    /**
     * 
     * @param statisticsConfig
     */
    void saveStatisticsConfig(StatisticsConfig statisticsConfig);
}
