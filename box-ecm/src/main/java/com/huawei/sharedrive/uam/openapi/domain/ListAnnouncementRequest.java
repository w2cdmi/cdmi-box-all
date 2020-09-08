package com.huawei.sharedrive.uam.openapi.domain;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class ListAnnouncementRequest
{
    private static final long DEFAULT_OFFSET = 0;
    
    private static final long DEFAULT_STARTID = 0;
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final int MAX_LIMIT = 1000;
    
    private Long offset;
    
    private Long startId;
    
    private Integer limit;
    
    public ListAnnouncementRequest()
    {
        offset = DEFAULT_OFFSET;
        limit = DEFAULT_LIMIT;
        startId = DEFAULT_STARTID;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public Long getStartId()
    {
        return startId;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setStartId(Long startId)
    {
        this.startId = startId;
    }
    
    public void checkParameter() throws InvalidParamterException
    {
        if (offset != null && offset < 0)
        {
            throw new InvalidParamterException("Invalid offset " + offset);
        }
        if (startId != null && (startId < 0))
        {
            throw new InvalidParamterException("Invalid start id " + startId);
        }
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
        {
            throw new InvalidParamterException("Invalid limit " + limit);
        }
    }
}
