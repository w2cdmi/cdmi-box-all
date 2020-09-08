package pw.cdmi.box.disk.client.domain.common;

import java.util.Date;

public class RestBaseObject
{
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Date createdAt;
    
    private Long createdBy;
    
    private String createdByName;
    
    private Long id;
    
    private Boolean isEncrypt;
    
    private Date modifiedAt;
    
    private Long modifiedBy;
    
    private String modifiedByName;
    
    private String name;
    
    private Long ownedBy;
    
    private Long parent;
    
    private Long size;
    
	private Boolean isVirus;
    
    private byte status;
    
    private byte type;
    
    private String mender;
    
    private String menderName;
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public Boolean getIsEncrypt()
    {
        return isEncrypt;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getOwnedBy()
    {
        return ownedBy;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
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
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setIsEncrypt(Boolean isEncrypt)
    {
        this.isEncrypt = isEncrypt;
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
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnedBy(Long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public String getMender()
    {
        return mender;
    }
    
    public void setMender(String mender)
    {
        this.mender = mender;
    }
    
    public String getMenderName()
    {
        return menderName;
    }
    
    public void setMenderName(String menderName)
    {
        this.menderName = menderName;
    }
    public Boolean getIsVirus() {
		return isVirus;
	}

	public void setIsVirus(Boolean isVirus) {
		this.isVirus = isVirus;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}
	
}
