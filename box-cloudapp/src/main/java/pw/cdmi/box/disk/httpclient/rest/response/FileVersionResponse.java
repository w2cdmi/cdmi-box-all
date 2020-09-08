package pw.cdmi.box.disk.httpclient.rest.response;

import java.io.Serializable;

public class FileVersionResponse extends BaseResponse implements Serializable
{
    private static final long serialVersionUID = 8968237704039171413L;
    
    private long contentCreatedAt;
    
    private long contentModifiedAt;
    
    private long createdAt;
    
    private String createdBy;
    
    private String id;
    
    private boolean encrypted;
    
    private long modifiedAt;
    
    private String modifiedBy;
    
    private String name;
    
    private String ownerBy;
    
    private String parent;
    
    private String sha1;
    
    private long size;
    
    private String status;
    
    private String type;
    
    private String version;
    
    public long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public long getCreatedAt()
    {
        return createdAt;
    }
    
    public String getCreatedBy()
    {
        return createdBy;
    }
    
    public String getId()
    {
        return id;
    }
    
    public long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public String getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getOwnerBy()
    {
        return ownerBy;
    }
    
    public String getParent()
    {
        return parent;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getType()
    {
        return type;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public boolean isEncrypt()
    {
        return encrypted;
    }
    
    public void setContentCreatedAt(long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setCreatedAt(long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setEncrypt(boolean isEncrypt)
    {
        this.encrypted = isEncrypt;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setModifiedAt(long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public void setModifiedBy(String modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOwnerBy(String ownerBy)
    {
        this.ownerBy = ownerBy;
    }
    
    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(long size)
    {
        this.size = size;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    @Override
    public String toString()
    {
        return "FileVersionResponse [id=" + id + ", parent=" + parent + ", type=" + type + ", size=" + size
            + ", version=" + version + ", name=" + name + ", status=" + status + ", sha1=" + sha1
            + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + ", ownerBy=" + ownerBy
            + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", contentCreatedAt="
            + contentCreatedAt + ", contentModifiedAt=" + contentModifiedAt + ", isEncrypt=" + encrypted
            + ']';
    }
    
}
