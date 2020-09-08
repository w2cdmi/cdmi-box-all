package pw.cdmi.box.disk.client.domain.user;

import java.io.Serializable;
import java.security.InvalidParameterException;

public class ListTerminalRequest implements Serializable
{
    private static final long serialVersionUID = -239302497914331155L;
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    private static final int MAX_LIMIT = 1000;
    
    private Integer limit;
    
    private Long offset;
    
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
    
    public void checkParameter() throws InvalidParameterException
    {
        if (limit != null && (limit < 1 || limit > MAX_LIMIT))
        {
            throw new InvalidParameterException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParameterException();
        }
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
