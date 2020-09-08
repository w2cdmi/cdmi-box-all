package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;

public class MirrorObject
{
    private long ownedBy;
    
    private String srcObjectId;
    
    private int srcResourceGroupId;
    
    private String destObjectId;
    
    private int destResourceGroupId;
    
    private Date createAt;
    
    private int policyId;
    
    private int type;
    
    /** 分表信息 */
    private int tableSuffix;
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public String getSrcObjectId()
    {
        return srcObjectId;
    }
    
    public void setSrcObjectId(String srcObjectId)
    {
        this.srcObjectId = srcObjectId;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public String getDestObjectId()
    {
        return destObjectId;
    }
    
    public void setDestObjectId(String destObjectId)
    {
        this.destObjectId = destObjectId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public Date getCreateAt()
    {
        if (this.createAt == null)
        {
            return null;
        }
        return (Date) createAt.clone();
    }
    
    public void setCreateAt(Date createAt)
    {
        if (createAt == null)
        {
            this.createAt = null;
        }
        else
        {
            this.createAt = (Date) createAt.clone();
        }
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
}
