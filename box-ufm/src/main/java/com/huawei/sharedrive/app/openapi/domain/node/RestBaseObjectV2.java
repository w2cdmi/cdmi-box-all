package com.huawei.sharedrive.app.openapi.domain.node;

public class RestBaseObjectV2
{
    
    private Long createdAt;
    
    private Long createdBy;
    
    private Long id;
    
    private Long modifiedAt;
    
    private Long modifiedBy;
    
    private byte type;
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
}
