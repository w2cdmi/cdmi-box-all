package pw.cdmi.box.disk.client.domain.user;

import pw.cdmi.box.disk.client.api.RestTerminalRsp;

public class RestUserV2loginRsp
{
    
    private String refreshToken;
    
    private String token;
    
    private String objectSid;
    
    private long cloudUserId;
    
    private String loginName;
    
    private long timeout;
    
    private long uploadQos;
    
    private long downloadQos;
    
    private long userId;
    
    private long enterpriseId;
    
    private String domain;
    
    private long accountId;
    
    private boolean needDeclaration;
    
    private boolean needChangePassword;
    
    private String email;
    
    private RestTerminalRsp lastAccessTerminal;
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public long getTimeout()
    {
        return timeout;
    }
    
    public boolean isNeedDeclaration()
    {
        return needDeclaration;
    }
    
    public void setNeedDeclaration(boolean needDeclaration)
    {
        this.needDeclaration = needDeclaration;
    }
    
    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }
    
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public long getUploadQos()
    {
        return uploadQos;
    }
    
    public void setUploadQos(long uploadQos)
    {
        this.uploadQos = uploadQos;
    }
    
    public long getDownloadQos()
    {
        return downloadQos;
    }
    
    public void setDownloadQos(long downloadQos)
    {
        this.downloadQos = downloadQos;
    }
    
    public RestTerminalRsp getLastAccessTerminal()
    {
        return lastAccessTerminal;
    }
    
    public void setLastAccessTerminal(RestTerminalRsp lastAccessTerminal)
    {
        this.lastAccessTerminal = lastAccessTerminal;
    }
    
    public boolean isNeedChangePassword()
    {
        return needChangePassword;
    }
    
    public void setNeedChangePassword(boolean needChangePassword)
    {
        this.needChangePassword = needChangePassword;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
}
