package com.huawei.sharedrive.app.openapi.domain.user;

import pw.cdmi.common.domain.SecurityConfig;

public class RestSecurityConfigInfo
{
    /** 是否普通提取码 */
    private boolean complexCode;
    
    private boolean loginFailNotify;
    
    private int loginFailNum;
    
    private boolean notRemberMe;
    
    public RestSecurityConfigInfo(SecurityConfig securityConfig)
    {
        this.loginFailNotify = securityConfig.isLoginFailNotifyAdmim();
        this.loginFailNum = securityConfig.getLoginFailNum();
        this.notRemberMe = securityConfig.isDisableRemberMe();
        this.complexCode = securityConfig.isDisableSimpleLinkCode();
    }
    
    public int getLoginFailNum()
    {
        return loginFailNum;
    }
    
    public boolean isComplexCode()
    {
        return complexCode;
    }
    
    public boolean isLoginFailNotify()
    {
        return loginFailNotify;
    }
    
    public boolean isNotRemberMe()
    {
        return notRemberMe;
    }
    
    public void setComplexCode(boolean complexCode)
    {
        this.complexCode = complexCode;
    }
    
    public void setLoginFailNotify(boolean loginFailNotify)
    {
        this.loginFailNotify = loginFailNotify;
    }
    
    public void setLoginFailNum(int loginFailNum)
    {
        this.loginFailNum = loginFailNum;
    }
    
    public void setNotRemberMe(boolean notRemberMe)
    {
        this.notRemberMe = notRemberMe;
    }
    
}
