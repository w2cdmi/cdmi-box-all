package com.huawei.sharedrive.app.security.domain;

/**
 * 获取文件安全级别响应对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-24
 * @see
 * @since
 */
public class UserSecurityResponse
{
    private Integer securityRoleId;
    
    public Integer getSecurityRoleId()
    {
        return securityRoleId;
    }
    
    public void setSecurityRoleId(Integer securityRoleId)
    {
        this.securityRoleId = securityRoleId;
    }
    
}
