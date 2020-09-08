package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShareLinkCommon implements Serializable
{
    private static final long serialVersionUID = 3424050247633512160L;
    
    public static final int INODEID_BATCH = -1;

    @JsonIgnore
    private Date createdAt;
    
    @JsonIgnore
    private long createdBy;
    
    private long iNodeId;
    
    private String subINodes;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private byte status;
    
    @JsonIgnore
    private int tableSuffix;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    /**
     * 设置创建时间
     * 
     * @param createdAt
     */
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
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public long getiNodeId()
    {
        return iNodeId;
    }
    
    public void setiNodeId(long iNodeId)
    {
        this.iNodeId = iNodeId;
    }
    
    /**
     * 获取最后修改时间
     * 
     * @return
     */
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    /**
     * 设置最后修改时间
     * 
     * @param modifiedAt
     */
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
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }

	public String getSubINodes() {
		return subINodes;
	}

	public void setSubINodes(String subINodes) {
		this.subINodes = subINodes;
	}
    
    

}
