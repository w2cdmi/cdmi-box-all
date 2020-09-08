package pw.cdmi.box.disk.user.shiro;

import java.util.Date;

import org.apache.shiro.authc.UsernamePasswordToken;

public class UsernamePasswordCaptchaToken extends UsernamePasswordToken
{
    private static final long serialVersionUID = 1L;
    
    private String deviceAddress;
    
    private String proxyAddress;
    
    private String deviceAgent;
    
    private String deviceArea;
    
    private String deviceOS;
    
    private boolean ntlm;
    
    private String objectSid;
    
    private String sessionWebId;
     
    private String token;
    
    private String refreshToken;
    
    private Date expired;
    
    private long enterpriseId;
    
    private long accountId;
    
    private String regionIp;
    
    private String ownerDomain;

    //企业名称，多企业情况下，需要携带企业名称
    private String enterpriseName;

    public UsernamePasswordCaptchaToken()
    {
        super();
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public UsernamePasswordCaptchaToken(String username, char[] password, boolean rememberMe, String host,
        boolean ntlm, String objectSid, String deviceOS, String deviceAddress, String proxyAddress,
        String deviceAgent, String sessionWebId, String token, String refreshToken, Date expired,
        long enterpriseId, long accountId, String regionIp,String ownerDomain)
    {
        super(username, password, rememberMe, host);
        this.ntlm = ntlm;
        this.objectSid = objectSid;
        this.deviceOS = deviceOS;
        this.deviceAddress = deviceAddress;
        this.proxyAddress = proxyAddress;
        this.deviceAgent = deviceAgent;
        this.sessionWebId = sessionWebId;
        this.token = token;
        this.refreshToken = refreshToken;
        this.enterpriseId = enterpriseId;
        this.accountId = accountId;
        this.regionIp = regionIp;
        this.ownerDomain=ownerDomain;
        if(expired == null)
        {
            this.expired = null;
        }
        else
        {
            this.expired = (Date) expired.clone();
        }
    }
    
    public String getOwnerDomain() {
		return ownerDomain;
	}

	public void setOwnerDomain(String ownerDomain) {
		this.ownerDomain = ownerDomain;
	}

	public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
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
    
    public String getProxyAddress()
    {
        return proxyAddress;
    }
    
    public void setProxyAddress(String proxyAddress)
    {
        this.proxyAddress = proxyAddress;
    }
    
    public String getDeviceAddress()
    {
        return deviceAddress;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public String getDeviceArea()
    {
        return deviceArea;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public String getSessionWebId()
    {
        return sessionWebId;
    }
    
    public boolean isNtlm()
    {
        return ntlm;
    }
    
    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public void setDeviceArea(String deviceArea)
    {
        this.deviceArea = deviceArea;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public void setNtlm(boolean ntlm)
    {
        this.ntlm = ntlm;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public void setSessionWebId(String sessionWebId)
    {
        this.sessionWebId = sessionWebId;
    }
    
    public Date getExpired()
    {
        if (expired == null)
        {
            return null;
        }
        return (Date) expired.clone();
    }
    
    public void setExpired(Date expired)
    {
        if (expired == null)
        {
            this.expired = null;
        }
        else
        {
            this.expired = (Date) expired.clone();
        }
    }
    
    public String getRegionIp()
    {
        return regionIp;
    }
    
    public void setRegionIp(String regionIp)
    {
        this.regionIp = regionIp;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }
}
