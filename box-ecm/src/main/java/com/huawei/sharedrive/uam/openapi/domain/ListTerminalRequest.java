package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class ListTerminalRequest implements Serializable
{
    public static final int DEFAULT_LIMIT = 100;
    
    public static final long DEFAULT_OFFSET = 0L;
    
    public static final int MAX_LIMIT = 1000;
    
    private static final long serialVersionUID = 4625085350841098511L;
    
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
