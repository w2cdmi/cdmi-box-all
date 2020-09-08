package com.huawei.sharedrive.app.dataserver.domain;

/**
 * 网络距离配置
 * 
 * @author c00287749
 * 
 */
public class RegionNetworkDistance
{
    // 名称
    private String name;
    
    // 源区域
    private int srcRegionId;
    
    // 源资源组
    private int srcResourceGroupId;
    
    // 目标区域
    private int destRegionId;
    
    // 目标资源组
    private int destResourceGroupId;
    
    // 距离值，当距离越小的时候，表示越近
    private int value;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
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
    
    public int getValue()
    {
        return value;
    }
    
    public void setValue(int value)
    {
        this.value = value;
    }
    
}
