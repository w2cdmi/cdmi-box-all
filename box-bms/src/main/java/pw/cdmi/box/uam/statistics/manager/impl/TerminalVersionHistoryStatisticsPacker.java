package pw.cdmi.box.uam.statistics.manager.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pw.cdmi.box.uam.httpclient.domain.TimePoint;
import pw.cdmi.box.uam.statistics.domain.TerminalCurrentVersionDay;
import pw.cdmi.box.uam.statistics.domain.TerminalCurrentVersionView;
import pw.cdmi.box.uam.statistics.domain.TerminalHistoryNode;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionCurrentView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalVersionTypeView;
import pw.cdmi.common.domain.Terminal;

/**
 * 历史节点统计数据封装器
 * 
 * 
 */
@SuppressWarnings("unchecked")
public final class TerminalVersionHistoryStatisticsPacker
{
    private TerminalVersionHistoryStatisticsPacker()
    {
    }
    
    public static TerminalVersionCurrentView packCurrentList(List<TerminalStatisticsDay> itemList)
    {
        Map<Integer, List<TerminalStatisticsDay>> map = new HashMap<Integer, List<TerminalStatisticsDay>>(16);
        Integer key;
        List<TerminalStatisticsDay> list;
        for (TerminalStatisticsDay tempStatistics : itemList)
        {
            key = tempStatistics.getDeviceType();
            if (map.containsKey(key))
            {
                map.get(key).add(tempStatistics);
            }
            else
            {
                list = new ArrayList<TerminalStatisticsDay>(10);
                list.add(tempStatistics);
                map.put(key, list);
            }
        }
        List<TerminalCurrentVersionDay> reOrganizedList;
        TerminalCurrentVersionView currentVersionView;
        List<TerminalCurrentVersionView> curentVersionViews = new ArrayList<TerminalCurrentVersionView>(
            map.size());
        TerminalCurrentVersionDay currentVersionDay;
        Integer deviceType;
        List<TerminalStatisticsDay> dayList;
        for (Entry<Integer, List<TerminalStatisticsDay>> entry : map.entrySet())
        {
            deviceType = entry.getKey();
            dayList = entry.getValue();
            currentVersionView = new TerminalCurrentVersionView();
            reOrganizedList = new ArrayList<TerminalCurrentVersionDay>(dayList.size());
            for (TerminalStatisticsDay tsd : dayList)
            {
                currentVersionDay = new TerminalCurrentVersionDay(tsd);
                reOrganizedList.add(currentVersionDay);
            }
            trandDeviceType(currentVersionView, deviceType);
            currentVersionView.setDeviceType(deviceType);
            currentVersionView.setData(reOrganizedList);
            curentVersionViews.add(currentVersionView);
        }
        TerminalVersionCurrentView currentView = new TerminalVersionCurrentView();
        currentView.setData(curentVersionViews);
        return currentView;
    }
    
    private static void trandDeviceType(TerminalCurrentVersionView currentVersionView, Integer deviceType)
    {
        if (deviceType == Terminal.CLIENT_TYPE_ANDROID)
        {
            currentVersionView.setDeviceName(Terminal.CLIENT_TYPE_ANDROID_STR);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_IOS)
        {
            currentVersionView.setDeviceName(Terminal.CLIENT_TYPE_IOS_STR);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_PC)
        {
            currentVersionView.setDeviceName(Terminal.CLIENT_TYPE_PC_STR);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_WEB)
        {
            currentVersionView.setDeviceName(Terminal.CLIENT_TYPE_WEB_STR);
        }
        
    }
    
    public static TerminalVersionHistoryView packHistoryList(List<TerminalStatisticsDay> itemList,
        String interval) throws ParseException
    {
        TerminalVersionHistoryView terminalDeviceView = new TerminalVersionHistoryView();
        List<TerminalVersionTypeView> versionViewList = new ArrayList<TerminalVersionTypeView>(10);
        terminalDeviceView.setVersionHistoyList(versionViewList);
        Map<String, List<TerminalStatisticsDay>> map = new HashMap<String, List<TerminalStatisticsDay>>(16);
        String key;
        List<TerminalStatisticsDay> list;
        for (TerminalStatisticsDay tempStatistics : itemList)
        {
            key = tempStatistics.getClientVersion();
            if (map.containsKey(key))
            {
                map.get(key).add(tempStatistics);
            }
            else
            {
                list = new ArrayList<TerminalStatisticsDay>(10);
                list.add(tempStatistics);
                map.put(key, list);
            }
        }
        
        List<TerminalStatisticsDay> originDataList;
        List<TerminalHistoryNode> reOrganizedList;
        TerminalVersionTypeView tempDeviceView;
        String tempVersion;
        for (Entry<String, List<TerminalStatisticsDay>> entry : map.entrySet())
        {
            tempVersion = entry.getKey();
            originDataList = entry.getValue();
            reOrganizedList = rePackHistoryList(originDataList, interval);
            tempDeviceView = new TerminalVersionTypeView();
            tempDeviceView.setVersionName(tempVersion);
            tempDeviceView.setDataList(reOrganizedList);
            versionViewList.add(tempDeviceView);
        }
        return terminalDeviceView;
    }
    
    private static List<TerminalHistoryNode> rePackHistoryList(List<TerminalStatisticsDay> itemList,
        String interval) throws ParseException
    {
        Map<String, TerminalHistoryNode> map = new HashMap<String, TerminalHistoryNode>(10);
        for (TerminalStatisticsDay itemStatistics : itemList)
        {
            putItemIntoMap(map, itemStatistics, interval);
        }
        Collection<TerminalHistoryNode> valueList = map.values();
        List<TerminalHistoryNode> dataList;
        if (null == valueList)
        {
            dataList = Collections.EMPTY_LIST;
        }
        else
        {
            dataList = new ArrayList<TerminalHistoryNode>(valueList);
        }
        return dataList;
    }
    
    private static void putItemIntoMap(Map<String, TerminalHistoryNode> map,
        TerminalStatisticsDay itemStatistics, String unit) throws ParseException
    {
        String timePointStr = TimePoint.convert(itemStatistics.getDay(), unit).toShowString();
        TerminalHistoryNode concStatistics = map.get(timePointStr);
        if (null == concStatistics)
        {
            concStatistics = TerminalHistoryNode.convert(itemStatistics, unit);
            map.put(timePointStr, concStatistics);
        }
        else
        {
            updateMax(concStatistics, itemStatistics);
        }
    }
    
    private static void updateMax(TerminalHistoryNode concStatistics, TerminalStatisticsDay itemStatistics)
    {
        if (itemStatistics.getUserCount() > concStatistics.getUserCount())
        {
            concStatistics.setUserCount(itemStatistics.getUserCount());
        }
    }
    
}
