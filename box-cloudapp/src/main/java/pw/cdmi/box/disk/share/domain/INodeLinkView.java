package pw.cdmi.box.disk.share.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class INodeLinkView extends BasicLink
{
    private Date createdAt;
    
    private long createdBy;
    
    @JsonIgnore
    private String creator;
    
    private Date effectiveAt;
    
    private Date expireAt;
    
    private String id;
    
    private long iNodeId;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private long ownedBy;
    
    @JsonIgnore
    private String password;
    
    private String plainAccessCode;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getCreator()
    {
        return creator;
    }
    
    public Date getEffectiveAt()
    {
        if (effectiveAt == null)
        {
            return null;
        }
        return (Date) effectiveAt.clone();
    }
    
    public Date getExpireAt()
    {
        if (expireAt == null)
        {
            return null;
        }
        return (Date) expireAt.clone();
    }
    
    public String getId()
    {
        return id;
    }
    
    public long getiNodeId()
    {
        return iNodeId;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setCreator(String creator)
    {
        this.creator = creator;
    }
    
    public void setEffectiveAt(Date effectiveAt)
    {
        if (effectiveAt == null)
        {
            this.effectiveAt = null;
        }
        else
        {
            this.effectiveAt = (Date) effectiveAt.clone();
        }
    }
    
    public void setExpireAt(Date expireAt)
    {
        if (expireAt == null)
        {
            this.expireAt = null;
        }
        else
        {
            this.expireAt = (Date) expireAt.clone();
        }
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setiNodeId(long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
}
