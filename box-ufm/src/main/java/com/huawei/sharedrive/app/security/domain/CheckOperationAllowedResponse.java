package com.huawei.sharedrive.app.security.domain;

/**
 * 判断操作是否安全请求对象
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-23
 * @see
 * @since
 */
public class CheckOperationAllowedResponse
{
    // 操作类型
    private Boolean allowed;
    
    public Boolean isAllowed()
    {
        return allowed;
    }

    public void setAllowed(Boolean allowed)
    {
        this.allowed = allowed;
    }

    
}
