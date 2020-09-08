package com.huawei.sharedrive.isystem.mirror.domain;

import java.io.Serializable;

public class CopyPolicySiteInfo implements Serializable
{
    private static final long serialVersionUID = -464684065025650053L;
    
    private int id;
    
    public static final int DEFAULT_VALUE = 0;
    
    // 复制策略ID
    private int policyId;
    
    private int srcRegionId = DEFAULT_VALUE;
    
    private int srcResourceGroupId = DEFAULT_VALUE;
    
    private int destRegionId = DEFAULT_VALUE;
    
    private int destResourceGroupId = DEFAULT_VALUE;
    
    private int state;
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public int getSrcRegionId()
    {
        return srcRegionId;
    }
    
    public void setSrcRegionId(int srcRegionId)
    {
        this.srcRegionId = srcRegionId;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + destRegionId;
        result = prime * result + destResourceGroupId;
        result = prime * result + policyId;
        result = prime * result + srcRegionId;
        result = prime * result + srcResourceGroupId;
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        
        if (getClass() != obj.getClass())
        {
            return false;
        }
        CopyPolicySiteInfo other = (CopyPolicySiteInfo) obj;
        if (destRegionId != other.destRegionId)
        {
            return false;
        }
        
        if (destResourceGroupId != other.destResourceGroupId)
        {
            return false;
        }
        if (policyId != other.policyId)
        {
            return false;
        }
        
        if (srcRegionId != other.srcRegionId)
        {
            return false;
        }
        
        if (srcResourceGroupId != other.srcResourceGroupId)
        {
            return false;
        }
        
        return true;
    }
    
}
