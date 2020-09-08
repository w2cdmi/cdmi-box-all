package pw.cdmi.box.disk.logininfo.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginInfo implements Serializable
{
    public final static byte LOGININGO_NAME = 1;
    
    public final static byte LOGININGO_EMAIL = 2;
    
    public final static byte LOGININGO_MOBILE = 3;
    
    private static final long serialVersionUID = -6134411102565223945L;
    
    @NotNull
    @Size(max = 255)
    private String loginName;
    
    @NotNull
    private long enterpriseId;
    
    @NotNull
    private long userId;
    
    @NotNull
    private Byte loginType;
    
    @NotNull
    @Size(max = 64)
    private String domainName;
    
    private long accountId;
    
    private String tableSuffix;
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public Byte getLoginType()
    {
        return loginType;
    }
    
    public void setLoginType(Byte loginType)
    {
        this.loginType = loginType;
    }
    
    public String getDomainName()
    {
        return domainName;
    }
    
    public void setDomainName(String domainName)
    {
        this.domainName = domainName;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
