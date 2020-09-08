package com.huawei.sharedrive.app.openapi.domain.message;

import java.util.List;

/**
 * 消息列举响应对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-19
 * @see
 * @since
 */
public class MessageList
{
    private Long offset;
    
    private Integer limit;
    
    private long totalCount;
    
    private List<MessageResponse> messages;
    
    public MessageList()
    {
        
    }
    
    public MessageList(Long offset, Integer limit, long totalCount, List<MessageResponse> messages)
    {
        this.offset = offset;
        this.limit = limit;
        this.totalCount = totalCount;
        this.messages = messages;
    }
    
    public Integer getLimit()
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
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setMessages(List<MessageResponse> messages)
    {
        this.messages = messages;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
