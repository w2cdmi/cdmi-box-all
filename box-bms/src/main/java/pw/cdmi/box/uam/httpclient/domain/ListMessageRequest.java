package pw.cdmi.box.uam.httpclient.domain;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.InvalidParamterException;

/**
 * 列举消息请求对象
 * 
 * @version CloudStor CSE Service Platform Subproject, 2015-3-18
 * @see
 * @since
 */
public class ListMessageRequest
{
    /** 默认查找全部的消息 */
    public static final String DEFAULT_STATUS = MessageStatusEnum.ALL.getDesc();
    
    // 偏移量默认值
    private static final long DEFAULT_OFFSET = 0;
    
    private static final long DEFAULT_STARTID = 0;
    
    // 返回的条目数默认值
    private static final int DEFAULT_LIMIT = 100;
    
    // 最大查找条目数
    private static final int MAX_LIMIT = 1000;
    
    // 消息状态: 0-未读和已读 1-未读 2-已读
    // all unread read
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
