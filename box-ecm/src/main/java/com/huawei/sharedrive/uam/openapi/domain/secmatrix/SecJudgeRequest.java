package com.huawei.sharedrive.uam.openapi.domain.secmatrix;

public class SecJudgeRequest
{
    
    private String operation;
    
    private Byte securityId;
    
    private Integer spaceSecRoleId;
    
    private Integer targetSecRoleId;
    
    public String getOperation()
    {
        return operation;
    }
    
    public Byte getSecurityId()
    {
        return securityId;
    }
    
    public Integer getSpaceSecRoleId()
    {
        return spaceSecRoleId;
    }
    
    public void setOperation(String operation)
    {
        this.operation = operation;
    }
    
    public void setSecurityId(Byte securityId)
    {
        this.securityId = securityId;
    }
    
    public void setSpaceSecRoleId(Integer spaceSecRoleId)
    {
        this.spaceSecRoleId = spaceSecRoleId;
    }
    
    public Integer getTargetSecRoleId()
    {
        return targetSecRoleId;
    }
    
    public void setTargetSecRoleId(Integer targetSecRoleId)
    {
        this.targetSecRoleId = targetSecRoleId;
    }
    
}
