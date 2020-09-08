package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class TerminalVersionCurrentView
{
    private List<TerminalCurrentVersionView> data;
    
    public List<TerminalCurrentVersionView> getData()
    {
        return data;
    }
    
    public void setData(List<TerminalCurrentVersionView> data)
    {
        this.data = data;
    }
    
}
