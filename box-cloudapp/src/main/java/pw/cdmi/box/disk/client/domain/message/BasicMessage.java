package pw.cdmi.box.disk.client.domain.message;

import java.util.Date;

public class BasicMessage
{
    
    private String appId;

    private String content;

    private Date createdAt;

    private Date expiredAt;

    private long id;

    

    private long providerId;

    private long receiverId;

    public String getAppId()
    {
        return appId;
    }

    public String getContent()
    {
        return content;
    }

    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }

    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }

    public long getId()
    {
        return id;
    }


    public long getProviderId()
    {
        return providerId;
    }

    public long getReceiverId()
    {
        return receiverId;
    }

    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setContent(String content)
    {
        this.content = content;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setExpiredAt(Date expiredAt)
    {
        if ( expiredAt == null)
        {
            this.expiredAt = null;
        }
        else
        {
            this.expiredAt = (Date)  expiredAt.clone();
        }
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setProviderId(long providerId)
    {
        this.providerId = providerId;
    }
    
    public void setReceiverId(long receiverId)
    {
        this.receiverId = receiverId;
    }
    
}
