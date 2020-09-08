package com.huawei.sharedrive.uam.statistics.domain;

import java.util.List;

public class TerminalVersionTypeView
{
    private List<TerminalHistoryNode> dataList;
    
    private String versionName;
    
    public List<TerminalHistoryNode> getDataList()
    {
        return dataList;
    }
    
    public String getVersionName()
    {
        return versionName;
    }
    
    public void setDataList(List<TerminalHistoryNode> dataList)
    {
        this.dataList = dataList;
    }
    
    public void setVersionName(String versionName)
    {
        this.versionName = versionName;
    }
    
}
