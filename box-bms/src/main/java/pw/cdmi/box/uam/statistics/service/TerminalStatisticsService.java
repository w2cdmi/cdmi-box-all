package pw.cdmi.box.uam.statistics.service;

import java.util.List;

import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;

public interface TerminalStatisticsService
{
    List<TerminalStatisticsDay> getCurrentList();
    
    List<TerminalStatisticsDay> getCurrentListByDeviceType();
    
    List<TerminalStatisticsDay> getHistoryGroupByDeviceType(Integer beginDay, Integer endDay);
    
    List<TerminalStatisticsDay> getHistoryListGroupByVersion(Integer beginDay, Integer endDay,
        Integer deviceType);
}
