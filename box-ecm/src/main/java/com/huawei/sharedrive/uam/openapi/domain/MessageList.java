package com.huawei.sharedrive.uam.openapi.domain;

import java.util.List;

public class MessageList
{
    private long offset;
    
    private int limit;
    
    private long totalCount;
    
    private List<MessageResponse> messages;
    
    public int getLimit()
    {
        return limit;
    }
    
    public List<MessageResponse> getMessages()
    {
        return messages;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setMessages(List<MessageResponse> messages)
    {
        this.messages = messages;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
