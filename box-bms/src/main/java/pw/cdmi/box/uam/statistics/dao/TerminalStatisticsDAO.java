package pw.cdmi.box.uam.statistics.dao;

import java.util.List;

import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;

public interface TerminalStatisticsDAO
{
    
    List<TerminalStatisticsDay> getList(int day);
    
    List<TerminalStatisticsDay> getListGroupByDeviceType(int day);
    
    void insert(TerminalStatisticsDay nodeStatistics);
    
    List<TerminalStatisticsDay> getListGroupByDeviceType(Integer beginDay, Integer endDay);
    
    List<TerminalStatisticsDay> getList(Integer beginDay, Integer endDay, Integer deviceType);
    
    // List<TerminalStatisticsDay>
}
