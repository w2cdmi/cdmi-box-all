package com.huawei.sharedrive.uam.statistics.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.uam.util.BusinessConstants;

import pw.cdmi.common.domain.Terminal;

public final class StatisticsPacker
{
    private StatisticsPacker()
    {
        
    }
    
    public static List<TerminalHistoryTableResponse> packerTerminalHistoryData(
        List<ThirdTerminalData> tableNodes)
    {
        Map<String, TerminalHistoryTableResponse> map = new HashMap<String, TerminalHistoryTableResponse>(
            BusinessConstants.INITIAL_CAPACITIES);
        TerminalHistoryTableResponse tableResponse;
        String key;
        for (ThirdTerminalData tableNode : tableNodes)
        {
            key = tableNode.getDateStr();
            if (map.containsKey(key))
            {
                transNum(map.get(key), tableNode.getDeviceType(), tableNode.getCount());
            }
            else
            {
                tableResponse = new TerminalHistoryTableResponse();
                tableResponse.setTimePoint(tableNode.getTimePoint());
                tableResponse.setDate(key);
                transNum(tableResponse, tableNode.getDeviceType(), tableNode.getCount());
                map.put(key, tableResponse);
            }
        }
        List<TerminalHistoryTableResponse> tableData = new ArrayList<TerminalHistoryTableResponse>(
            map.size());
        
        for(Map.Entry<String, TerminalHistoryTableResponse> entry : map.entrySet())
        {
            tableData.add(entry.getValue());
        }
        Collections.sort(tableData, new TerminalHistoryDateSortor());
        return tableData;
    }
    
    private static void transNum(TerminalHistoryTableResponse tableResponse, int deviceType, int userCount)
    {
        if (deviceType == Terminal.CLIENT_TYPE_ANDROID)
        {
            tableResponse.setAndroidUserCount(tableResponse.getAndroidUserCount() + userCount);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_IOS)
        {
            tableResponse.setIosUserCount(tableResponse.getIosUserCount() + userCount);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_PC)
        {
            tableResponse.setPcUserCount(tableResponse.getPcUserCount() + userCount);
        }
        else if (deviceType == Terminal.CLIENT_TYPE_WEB)
        {
            tableResponse.setWebUserCount(tableResponse.getWebUserCount() + userCount);
        }
    }
    
}
