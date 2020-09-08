package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.common.domain.Terminal;

public class ListTerminalResonse implements Serializable
{
    private static final long serialVersionUID = 7841263206344393320L;
    
    private List<Terminal> terminalList;
    
    private int limit;
    
    private long offset;
    
    private int totalCount;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public List<Terminal> getTerminalList()
    {
        return terminalList;
    }
    
    public void setTerminalList(List<Terminal> terminalList)
    {
        this.terminalList = terminalList;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
