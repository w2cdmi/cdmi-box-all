package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;
import java.util.List;

public class ListTerminalRequest implements Serializable
{
    public static final int DEFAULT_LIMIT = 100;
    
    public static final long DEFAULT_OFFSET = 0L;
    
    public static final int MAX_LIMIT = 1000;
    
    private static final long serialVersionUID = 4625085350841098511L;
    
    private Integer limit;
    
    private Long offset;
    
    private List<TerminalOrder> terminalOrder;
    
    public ListTerminalRequest()
    {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }
    
    public ListTerminalRequest(Integer limit, Long offset)
    {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }
    
    public List<TerminalOrder> getTerminalOrder()
    {
        return terminalOrder;
    }
    
    public void setTerminalOrder(List<TerminalOrder> terminalOrder)
    {
        this.terminalOrder = terminalOrder;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
}
