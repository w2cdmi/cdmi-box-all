package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pw.cdmi.box.disk.client.domain.node.ThumbnailUrl;

public class INodeShare implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -2986101527348733200L;

    public static final byte STATUS_IN_RECYCLE = 1;
    
    public static final byte STATUS_NORMAL = 0;
    
    public static final byte TYPE_FILE = 1;
    
    public static final byte TYPE_FOLDER = 0;
    
    @JsonIgnore
    private Date createdAt;
    
    @JsonIgnore
    private long createdBy;
    
    private long id;
    
    private long iNodeId;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private String name;
    
    private long ownerId;
    
    private String ownerLoginName;
    
    private String ownerName;
    
    private String roleName;
    
    private String sharedDepartment;
    
    private String sharedUserEmail;
    
    private long sharedUserId;
    
    private String sharedUserLoginName;
    
    private String sharedUserName;
    
    private byte sharedUserType;
    
    private long size;
    
    private byte status;
    
    private String version;
    
    @JsonIgnore
    private int tableSuffix;
    
    private String thumbnailBigURL;
    
    private String thumbnailUrl;
    
    private byte type;
    
    private String extraType;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    private boolean previewable;
    
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
    
    public long getId()
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
    
    public String getName()
    {
        return name;
    }
    
    public long getOwnerId()
    {
        return ownerId;
    }
    
    public String getOwnerLoginName()
    {
        return ownerLoginName;
    }
    
    public String getOwnerName()
    {
        return ownerName;
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public String getSharedDepartment()
    {
        return sharedDepartment;
    }
    
    public String getSharedUserEmail()
    {
        return sharedUserEmail;
    }
    
    public long getSharedUserId()
    {
        return sharedUserId;
    }
    
    public String getSharedUserLoginName()
    {
        return sharedUserLoginName;
    }
    
    public String getSharedUserName()
    {
        return sharedUserName;
    }
    
    public byte getSharedUserType()
    {
        return sharedUserType;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public String getThumbnailBigURL()
    {
        return thumbnailBigURL;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public byte getType()
    {
        return type;
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
    
    public void setId(long id)
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
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnerId(long ownerId)
    {
        this.ownerId = ownerId;
    }
    
    public void setOwnerLoginName(String ownerLoginName)
    {
        this.ownerLoginName = ownerLoginName;
    }
    
    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public void setSharedDepartment(String sharedDepartment)
    {
        this.sharedDepartment = sharedDepartment;
    }
    
    public void setSharedUserEmail(String sharedUserEmail)
    {
        this.sharedUserEmail = sharedUserEmail;
    }
    
    public void setSharedUserId(long sharedUserId)
    {
        this.sharedUserId = sharedUserId;
    }
    
    public void setSharedUserLoginName(String sharedUserLoginName)
    {
        this.sharedUserLoginName = sharedUserLoginName;
    }
    
    public void setSharedUserName(String sharedUserName)
    {
        this.sharedUserName = sharedUserName;
    }
    
    public void setSharedUserType(byte sharedUserType)
    {
        this.sharedUserType = sharedUserType;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setThumbnailBigURL(String thumbnailBigURL)
    {
        this.thumbnailBigURL = thumbnailBigURL;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
    public String getExtraType()
    {
        return extraType;
    }

    public void setExtraType(String extraType)
    {
        this.extraType = extraType;
    }

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
    
}
