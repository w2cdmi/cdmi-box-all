package com.huawei.sharedrive.app.security.domain;

/**
 * 获取文件安全级别响应对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-24
 * @see
 * @since
 */
public class GetSecurityIdResponse
{
    private Byte securityId;
    
    public Byte getSecurityId()
    {
        return securityId;
    }
    
    public void setSecurityId(Byte securityId)
    {
        this.securityId = securityId;
    }
    
}
