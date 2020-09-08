package pw.cdmi.box.uam.statistics.domain;

import java.util.List;

public class TerminalVersionHistoryView
{
    
    private List<TerminalVersionTypeView> versionHistoyList;
    
    public List<TerminalVersionTypeView> getVersionHistoyList()
    {
        return versionHistoyList;
    }
    
    public void setVersionHistoyList(List<TerminalVersionTypeView> versionHistoyList)
    {
        this.versionHistoyList = versionHistoyList;
    }
}
