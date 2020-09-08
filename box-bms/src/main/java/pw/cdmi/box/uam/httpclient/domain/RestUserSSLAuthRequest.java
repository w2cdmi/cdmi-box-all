package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestUserSSLAuthRequest implements Serializable
{
    
    private static final long serialVersionUID = 7507049251767917613L;
    
    private String appId;
    
    private String loginName;
    
    private String sslCert;
    
    private String data;
    
    private String sign;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getSslCert()
    {
        return sslCert;
    }
    
    public void setSslCert(String sslCert)
    {
        this.sslCert = sslCert;
    }
    
    public String getData()
    {
        return data;
    }
    
    public void setData(String data)
    {
        this.data = data;
    }
    
    public String getSign()
    {
        return sign;
    }
    
    public void setSign(String sign)
    {
        this.sign = sign;
    }
}
