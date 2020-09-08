package pw.cdmi.box.uam.statistics.manager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.statistics.domain.TerminalDeviceHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsInfo;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionCurrentView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHistoryView;
import pw.cdmi.box.uam.statistics.manager.TerminalStatisticsManager;
import pw.cdmi.box.uam.statistics.service.TerminalStatisticsService;

@Component
public class TerminalStatisticsManagerImpl implements TerminalStatisticsManager
{
    @Autowired
    private TerminalStatisticsService terminalStatisticsService;
    
    @Override
    public TerminalVersionCurrentView getCurrentDataByDeviceType() throws ParseException
    {
        List<TerminalStatisticsDay> terminalStatisticses = terminalStatisticsService.getCurrentList();
        return TerminalVersionHistoryStatisticsPacker.packCurrentList(terminalStatisticses);
    }
    
    @Override
    public TerminalDeviceHistoryView getListGroupByDeviceType(Integer startDay, Integer endDay,
        String interval) throws ParseException
    {
        List<TerminalStatisticsDay> list = terminalStatisticsService.getHistoryGroupByDeviceType(startDay,
            endDay);
        return TerminalHistoryStatisticsPacker.packHistoryList(list, interval);
    }
    
    @Override
    public TerminalVersionHistoryView getListByDeviceType(Integer startDay, Integer endDay, String interval,
        int deviceType) throws ParseException
    {
        List<TerminalStatisticsDay> list = terminalStatisticsService.getHistoryListGroupByVersion(startDay,
            endDay,
            deviceType);
        
        return TerminalVersionHistoryStatisticsPacker.packHistoryList(list, interval);
    }
    
    @Override
    public List<TerminalStatisticsInfo> getCurrentData()
    {
        List<TerminalStatisticsDay> terminalStatisticses = terminalStatisticsService.getCurrentList();
        List<TerminalStatisticsInfo> terminalInfoes = new ArrayList<TerminalStatisticsInfo>(
            terminalStatisticses.size());
        TerminalStatisticsInfo terminalInfo;
        for (TerminalStatisticsDay ts : terminalStatisticses)
        {
            terminalInfo = new TerminalStatisticsInfo(ts);
            terminalInfoes.add(terminalInfo);
        }
        return terminalInfoes;
    }
    
}
