package pw.cdmi.box.disk.client.domain.message;

import pw.cdmi.core.exception.InvalidParamException;

/**
 * @version  CloudStor CSE Service Platform Subproject, 2015-3-18
 * @see  
 * @since  
 */
public class ListAnnouncementRequest
{
    private static final long DEFAULT_OFFSET = 0;
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final int MAX_LIMIT = 1000;
    
    private Long offset;
    
    private Integer limit;
    
    public ListAnnouncementRequest()
    {
        offset = DEFAULT_OFFSET;
        limit = DEFAULT_LIMIT;
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
    
    public void checkParameter() throws InvalidParamException
    {
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException("Invalid offset " + offset);
        }
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
        {
            throw new InvalidParamException("Invalid limit " + limit);
        }
    }
}
