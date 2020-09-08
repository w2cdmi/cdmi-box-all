package com.huawei.sharedrive.app.security.domain;

/**
 * 判断操作是否安全请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-23
 * @see
 * @since
 */
public class CheckOperationAllowedRequest
{
    // 操作类型
    private String operation;
    
    // 安全类型id
    private byte securityId;
    
    // 源文件所在空间所属安全角色
    private int spaceSecRoleId;
    
    // 目标文件所在空间所属安全角色
    private Integer targetSecRoleId;
    
    public CheckOperationAllowedRequest()
    {
        
    }
    
    public CheckOperationAllowedRequest(String operation, byte securityId, int spaceSecRoleId,
        Integer targetSecRoleId)
    {
        this.operation = operation;
        this.securityId = securityId;
        this.spaceSecRoleId = spaceSecRoleId;
        this.targetSecRoleId = targetSecRoleId;
    }
    
    public String getOperation()
    {
        return operation;
    }
    
    public byte getSecurityId()
    {
        return securityId;
    }
    
    public int getSpaceSecRoleId()
    {
        return spaceSecRoleId;
    }
    
    public Integer getTargetSecRoleId()
    {
        return targetSecRoleId;
    }
    
    public void setOperation(String operation)
    {
        this.operation = operation;
    }
    
    public void setSecurityId(byte securityId)
    {
        this.securityId = securityId;
    }
    
    public void setSpaceSecRoleId(int spaceSecRoleId)
    {
        this.spaceSecRoleId = spaceSecRoleId;
    }
    
    public void setTargetSecRoleId(Integer targetSecRoleId)
    {
        this.targetSecRoleId = targetSecRoleId;
    }
    
}
