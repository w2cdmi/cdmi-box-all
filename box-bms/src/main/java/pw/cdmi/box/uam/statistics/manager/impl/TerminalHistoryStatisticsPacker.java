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
import pw.cdmi.box.uam.statistics.domain.TerminalDeviceHistoryView;
import pw.cdmi.box.uam.statistics.domain.TerminalDeviceTypeView;
import pw.cdmi.box.uam.statistics.domain.TerminalHistoryNode;
import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;
import pw.cdmi.common.domain.Terminal;

/**
 * 历史节点统计数据封装器
 * 
 * 
 */
@SuppressWarnings("unchecked")
public final class TerminalHistoryStatisticsPacker
{
    private TerminalHistoryStatisticsPacker()
    {
    }
    
    public static TerminalDeviceHistoryView packHistoryList(List<TerminalStatisticsDay> itemList,
        String interval) throws ParseException
    {
        TerminalDeviceHistoryView terminalDeviceView = new TerminalDeviceHistoryView();
        List<TerminalDeviceTypeView> deviceTypeView = new ArrayList<TerminalDeviceTypeView>(10);
        terminalDeviceView.setDeviceHistoryList(deviceTypeView);
        Map<Integer, List<TerminalStatisticsDay>> map = new HashMap<Integer, List<TerminalStatisticsDay>>(
            itemList.size());
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
        
        List<TerminalStatisticsDay> originDataList;
        List<TerminalHistoryNode> reOrganizedList;
        TerminalDeviceTypeView tempDeviceView;
        Integer tempDevice;
        for (Entry<Integer, List<TerminalStatisticsDay>> entry : map.entrySet())
        {
            tempDevice = entry.getKey();
            originDataList = entry.getValue();
            reOrganizedList = rePackHistoryList(originDataList, interval);
            tempDeviceView = new TerminalDeviceTypeView();
            tempDeviceView.setDeviceType(tempDevice);
            trandDeviceType(tempDeviceView, tempDevice);
            tempDeviceView.setDataList(reOrganizedList);
            deviceTypeView.add(tempDeviceView);
        }
        return terminalDeviceView;
    }
    
    private static void trandDeviceType(TerminalDeviceTypeView currentVersionView, Integer deviceType)
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
    
    /**
     * 更新最大值
     * 
     * @param concStatistics
     * @param itemStatistics
     */
    private static void updateMax(TerminalHistoryNode concStatistics, TerminalStatisticsDay itemStatistics)
    {
        if (itemStatistics.getUserCount() > concStatistics.getUserCount())
        {
            concStatistics.setUserCount(itemStatistics.getUserCount());
        }
    }
    
}
