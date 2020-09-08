/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 扩展默认的用户认证的bean
 * 
 * @author s00108907
 * 
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken
{
    private static final long serialVersionUID = 1L;
    
    private String captcha;
    
    private boolean isNtlmStatus;
    
    private String objectSid;
    
    private String capKey;
    
    public boolean isNtlm()
    {
        return isNtlmStatus;
    }
    
    public void setNtlm(boolean isNtlm)
    {
        this.isNtlmStatus = isNtlm;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public UsernamePasswordCaptchaToken()
    {
        super();
    }
    
    public String getCaptcha()
    {
        return captcha;
    }
    
    public void setCaptcha(String captcha)
    {
        this.captcha = captcha;
    }
    
    public UsernamePasswordCaptchaToken(String username, char[] password, boolean rememberMe, String host)
    {
        super(username, password, rememberMe, host);
    }
    
    public String getCapKey()
    {
        return capKey;
    }
    
    public void setCapKey(String capKey)
    {
        this.capKey = capKey;
    }
}
