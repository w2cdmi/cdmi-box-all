package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class TerminalDeviceHistoryView
{
    
    private List<TerminalDeviceTypeView> deviceHistoryList;
    
    public List<TerminalDeviceTypeView> getDeviceHistoryList()
    {
        return deviceHistoryList;
    }
    
    public void setDeviceHistoryList(List<TerminalDeviceTypeView> deviceHistoryList)
    {
        this.deviceHistoryList = deviceHistoryList;
    }
    
}
