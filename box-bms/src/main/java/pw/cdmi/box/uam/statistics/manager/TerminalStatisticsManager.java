package pw.cdmi.box.uam.statistics.manager;

import java.text.ParseException;
import java.util.List;

import pw.cdmi.box.uam.statistics.domain.TerminalDeviceHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionCurrentView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHistoryView;

public interface TerminalStatisticsManager
{
    
    TerminalDeviceHistoryView getListGroupByDeviceType(Integer startDay, Integer endDay, String internal)
        throws ParseException;
    
    TerminalVersionHistoryView getListByDeviceType(Integer startDay, Integer endDay, String internal,
        int deviceType) throws ParseException;
    
    List<TerminalStatisticsInfo> getCurrentData();
    
    TerminalVersionCurrentView getCurrentDataByDeviceType() throws ParseException;
    
}
