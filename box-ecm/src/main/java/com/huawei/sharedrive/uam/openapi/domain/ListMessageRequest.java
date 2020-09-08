package com.huawei.sharedrive.uam.openapi.domain;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class ListMessageRequest
{
    public static final String DEFAULT_STATUS = MessageStatusEnum.ALL.getDesc();
    
    private static final long DEFAULT_OFFSET = 0;
    
    private static final long DEFAULT_STARTID = 0;
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final int MAX_LIMIT = 1000;
    
    private String status;
    
    private Long offset;
    
    private Long startId;
    
    private Integer limit;
    
    public ListMessageRequest()
    {
        this.offset = DEFAULT_OFFSET;
        this.limit = DEFAULT_LIMIT;
        this.status = DEFAULT_STATUS;
        this.startId = DEFAULT_STARTID;
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
    
    public String getStatus()
    {
        return status;
    }
    
    public byte getStatusCode()
    {
        if (StringUtils.isBlank(status))
        {
            return MessageStatusEnum.ALL.getCode();
        }
        
        MessageStatusEnum statusEnum = MessageStatusEnum.parseByDesc(status);
        if (null == statusEnum)
        {
            throw new InvalidParamterException("Invalid status " + status);
        }
        
        return statusEnum.getCode();
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
    
    public void setStatus(String status)
    {
        this.status = status;
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
        if (StringUtils.isNotBlank(status))
        {
            MessageStatusEnum statusEnum = MessageStatusEnum.parseByDesc(status);
            if (null == statusEnum)
            {
                throw new InvalidParamterException("Invalid status " + status);
            }
        }
        
    }
}
