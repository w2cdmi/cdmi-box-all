package pw.cdmi.box.uam.statistics.dao;

import java.util.Date;

import pw.cdmi.box.uam.statistics.domain.StatisticsTempChart;

public interface StatisticsChartDao
{
    void deleteEndToCreatedAt(Date date);
    
    StatisticsTempChart get(String id);
    
    void insert(StatisticsTempChart statisticsTempChart);
}
