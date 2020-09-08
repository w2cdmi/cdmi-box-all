package com.huawei.sharedrive.app.openapi.domain.share;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestLinkSendRequest
{
    public static final int MAX_MESSAGE_LENGTH = 5000;
    
    private String emails;
    
    private String linkUrl;
    
    private String message;
    
    private Integer totalCount;
    
    public String getEmails()
    {
        return emails;
    }
    
    public String getLinkUrl()
    {
        return linkUrl;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public void setEmails(String emails)
    {
        this.emails = emails;
    }
    
    public void setLinkUrl(String linkUrl)
    {
        this.linkUrl = linkUrl;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }

    public void checkParameter()
    {
        if (message != null && message.length() > MAX_MESSAGE_LENGTH)
        {
            throw new InvalidParamException("Message length invalid");
        }
    }
}
