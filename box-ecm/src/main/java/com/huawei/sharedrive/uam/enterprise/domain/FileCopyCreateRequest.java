package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class FileCopyCreateRequest implements Serializable
{
    
    private static final long serialVersionUID = -4224320090232875471L;
    
    @NotEmpty
    private Long srcSafeRoleId;
    
    @NotEmpty
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
