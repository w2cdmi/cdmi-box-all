package com.huawei.sharedrive.app.mirror.domain;

public class CopyPolicySiteInfo
{
    
    public static final int DEFAULT_VALUE = 0;
    
    private int destRegionId = DEFAULT_VALUE;
    
    private int destResourceGroupId = DEFAULT_VALUE;
    
    private int id;
    
    // 复制策略ID
    private int policyId;
    
    private int srcRegionId = DEFAULT_VALUE;
    
    private int srcResourceGroupId = DEFAULT_VALUE;
    
    private int state;
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public int getId()
    {
        return id;
    }
    
    public int getPolicyId()
    {
        return policyId;
    }
    
    public int getSrcRegionId()
    {
        return srcRegionId;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setPolicyId(int policyId)
    {
        this.policyId = policyId;
    }
    
    public void setSrcRegionId(int srcRegionId)
    {
        this.srcRegionId = srcRegionId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
}
