package com.huawei.sharedrive.uam.user.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernamePasswordCaptchaToken extends UsernamePasswordToken
{
    private static final long serialVersionUID = 1L;
    
    private String captcha;
    
    private boolean ntlm;
    
    private String objectSid;
    
    private String enterpriseName;

    public boolean isNtlm()
    {
        return ntlm;
    }

    public void setNtlm(boolean ntlm)
    {
        this.ntlm = ntlm;
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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public UsernamePasswordCaptchaToken(String username, char[] password, boolean rememberMe, String host,
        boolean isNtlm, String objectSid, String captcha)
    {
        super(username, password, rememberMe, host);
        this.ntlm = isNtlm;
        this.objectSid = objectSid;
        this.captcha = captcha;
    }
}
