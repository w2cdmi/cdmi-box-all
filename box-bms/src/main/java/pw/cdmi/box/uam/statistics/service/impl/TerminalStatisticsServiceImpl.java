package pw.cdmi.box.uam.statistics.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.statistics.dao.TerminalStatisticsDAO;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;
import pw.cdmi.box.uam.statistics.job.StatisticsDateUtils;
import pw.cdmi.box.uam.statistics.service.TerminalStatisticsService;

@Component
public class TerminalStatisticsServiceImpl implements TerminalStatisticsService
{
    
    @Autowired
    private TerminalStatisticsDAO terminalStatisticsDAO;
    
    @Override
    public List<TerminalStatisticsDay> getCurrentList()
    {
        Calendar calendar = Calendar.getInstance();
        int day = StatisticsDateUtils.getDay(calendar);
        List<TerminalStatisticsDay> list = terminalStatisticsDAO.getList(day);
        if (list.isEmpty())
        {
            day = StatisticsDateUtils.getLastDay(day);
            list = terminalStatisticsDAO.getList(day);
        }
        return list;
    }
    
    @Override
    public List<TerminalStatisticsDay> getCurrentListByDeviceType()
    {
        Calendar calendar = Calendar.getInstance();
        int day = StatisticsDateUtils.getDay(calendar);
        List<TerminalStatisticsDay> list = terminalStatisticsDAO.getListGroupByDeviceType(day);
        if (list.isEmpty())
        {
            day = StatisticsDateUtils.getLastDay(day);
            list = terminalStatisticsDAO.getListGroupByDeviceType(day);
        }
        return list;
    }
    
    @Override
    public List<TerminalStatisticsDay> getHistoryGroupByDeviceType(Integer beginDay, Integer endDay)
    {
        return terminalStatisticsDAO.getListGroupByDeviceType(beginDay, endDay);
    }
    
    @Override
    public List<TerminalStatisticsDay> getHistoryListGroupByVersion(Integer beginDay, Integer endDay,
        Integer deviceType)
    {
        return terminalStatisticsDAO.getList(beginDay, endDay, deviceType);
    }
}
