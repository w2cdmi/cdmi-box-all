package com.huawei.sharedrive.isystem.mirror.web.view;

import java.io.Serializable;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicySiteInfo;

public class CopyPolicySiteInfoView implements Serializable
{
    private static final long serialVersionUID = 1691357321528234920L;
    
    private int srcRegionId;
    
    private int destRegionId;
    
    private int srcResourceGroupId;
    
    private String srcResourceGroupName;
    
    private int destResourceGroupId;
    
    private String destResourceGroupName;
    
    public CopyPolicySiteInfoView()
    {
        
    }
    
    public CopyPolicySiteInfoView(int srcRegionId, int destRegionId, int srcResourceGroupId,
        int destResourceGroupId)
    {
        this.destRegionId = srcRegionId;
        this.srcRegionId = destRegionId;
        this.srcResourceGroupId = srcResourceGroupId;
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public CopyPolicySiteInfoView(CopyPolicySiteInfo copyPolicySiteInfo)
    {
        this.destRegionId = copyPolicySiteInfo.getDestRegionId();
        this.srcRegionId = copyPolicySiteInfo.getSrcRegionId();
        this.srcResourceGroupId = copyPolicySiteInfo.getSrcResourceGroupId();
        this.destResourceGroupId = copyPolicySiteInfo.getDestResourceGroupId();
    }
    
    public CopyPolicySiteInfoView(CopyPolicySiteInfo copyPolicySiteInfo, String srcResourceGroupName,
        String destResourceGroupName)
    {
        this(copyPolicySiteInfo);
        this.srcResourceGroupName = srcResourceGroupName;
        this.destResourceGroupName = destResourceGroupName;
    }
    
    public int getSrcResourceGroupId()
    {
        return srcResourceGroupId;
    }
    
    public void setSrcResourceGroupId(int srcResourceGroupId)
    {
        this.srcResourceGroupId = srcResourceGroupId;
    }
    
    public String getSrcResourceGroupName()
    {
        return srcResourceGroupName;
    }
    
    public void setSrcResourceGroupName(String srcResourceGroupName)
    {
        this.srcResourceGroupName = srcResourceGroupName;
    }
    
    public int getDestResourceGroupId()
    {
        return destResourceGroupId;
    }
    
    public void setDestResourceGroupId(int destResourceGroupId)
    {
        this.destResourceGroupId = destResourceGroupId;
    }
    
    public String getDestResourceGroupName()
    {
        return destResourceGroupName;
    }
    
    public void setDestResourceGroupName(String destResourceGroupName)
    {
        this.destResourceGroupName = destResourceGroupName;
    }
    
    public int getSrcRegionId()
    {
        return srcRegionId;
    }
    
    public void setSrcRegionId(int srcRegionId)
    {
        this.srcRegionId = srcRegionId;
    }
    
    public int getDestRegionId()
    {
        return destRegionId;
    }
    
    public void setDestRegionId(int destRegionId)
    {
        this.destRegionId = destRegionId;
    }
    
}
