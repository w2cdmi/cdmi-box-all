package pw.cdmi.box.disk.client.domain.message;

import java.util.List;

/**
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-3-19
 * @see
 * @since
 */
public class MessageList
{
    private Long offset;
    
    private Long startId;
    
    private Integer limit;
    
    private byte status;
    
    private long totalCount;
    
    private List<MessageResponse> messages;
    
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
    
    public Long getStartId()
    {
        return startId;
    }
    
    public byte getStatus()
    {
        return status;
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
    
    public void setStartId(Long startId)
    {
        this.startId = startId;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
