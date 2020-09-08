package com.huawei.sharedrive.app.files.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class MailMsg
{
    
    /** mail message type -0.share */
    public final static String SOURCE_SHARE = "share";
    
    /** mail message type -1.link */
    public final static String SOURCE_LINK = "link";
    
    /** user id */
    @JsonIgnore
    private long userId;
    
    private long sender;
    
    private String source;
    
    /** node owner */
    @JsonIgnore
    private long ownerId;
    
    private long ownedBy;
    
    /** node id */
    private long nodeId;
    
    /** mail subject */
    private String subject;
    
    /** mail message */
    private String message;
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getSubject()
    {
        return subject;
    }
    
    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getSource()
    {
        return source;
    }
    
    public void setSource(String source)
    {
        this.source = source;
    }

    public long getOwnedBy()
    {
        return ownedBy;
    }

    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public long getSender()
    {
        return sender;
    }

    public void setSender(long sender)
    {
        this.sender = sender;
    }
}
