package com.huawei.sharedrive.app.openapi.domain.message;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.message.domain.MessageStatus;

/**
 * 列举消息请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-18
 * @see
 * @since
 */
public class ListMessageRequest
{
    /** 默认查找全部的消息 */
    public static final String DEFAULT_STATUS = MessageStatus.ALL.getStatus();
    
    // 偏移量默认值
    private static final long DEFAULT_OFFSET = 0;
    
    // 默认起始ID值
    private static final long DEFAULT_START_ID = 0;
    
    // 返回的条目数默认值
    private static final int DEFAULT_LIMIT = 100;
    
    // 最大查找条目数
    private static final int MAX_LIMIT = 1000;
    
    // 消息状态: all-未读和已读 unread-未读 read-已读
    private String status;
    
    private Long offset;
    
    private Long startId;
    
    private Integer limit;
    
    public ListMessageRequest()
    {
        offset = DEFAULT_OFFSET;
        limit = DEFAULT_LIMIT;
        startId = DEFAULT_START_ID;
        status = DEFAULT_STATUS;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException("Invalid offset " + offset);
        }
        if (startId != null && (startId < 0))
        {
            throw new InvalidParamException("Invalid start id " + startId);
        }
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
        {
            throw new InvalidParamException("Invalid limit " + limit);
        }
        if (StringUtils.isNotBlank(status))
        {
            MessageStatus messageStatus = MessageStatus.getMessageStatus(status);
            if (messageStatus == null)
            {
                throw new InvalidParamException("Invalid status " + status);
            }
            switch (messageStatus)
            {
                case READ:
                case UNREAD:
                case ALL:
                    break;
                default:
                    throw new InvalidParamException("Invalid status " + status);
            }
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
    
    public Long getStartId()
    {
        return startId;
    }
    
    public String getStatus()
    {
        return status;
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
    
}
