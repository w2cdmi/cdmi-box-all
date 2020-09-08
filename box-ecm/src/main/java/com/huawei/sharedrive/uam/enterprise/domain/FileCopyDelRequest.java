package com.huawei.sharedrive.uam.enterprise.domain;

public class FileCopyDelRequest
{
    private Long srcSafeRoleId;
    
    private Long targetSafeRoleId;
    
    public Long getSrcSafeRoleId()
    {
        return srcSafeRoleId;
    }
    
    public void setSrcSafeRoleId(Long srcSafeRoleId)
    {
        this.srcSafeRoleId = srcSafeRoleId;
    }
    
    public Long getTargetSafeRoleId()
    {
        return targetSafeRoleId;
    }
    
    public void setTargetSafeRoleId(Long targetSafeRoleId)
    {
        this.targetSafeRoleId = targetSafeRoleId;
    }
    
}
