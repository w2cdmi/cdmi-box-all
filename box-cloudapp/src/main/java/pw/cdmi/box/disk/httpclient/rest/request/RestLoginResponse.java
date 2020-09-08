package pw.cdmi.box.disk.httpclient.rest.request;

import pw.cdmi.box.disk.client.api.RestTerminalRsp;

public class RestLoginResponse{
    private String token;
    
    private String refreshToken;
    
    private int timeout;
    
    private String loginName;

    private String alias;

    private long userId;
    
    private long cloudUserId;
    
    private long uploadQos;
    
    private long downloadQos;
    
    private long accountId;
    
    private long enterpriseId;

    private String mobile;

    private String domain;
    
    private boolean needDeclaration;
    
    private boolean needChangePassword;
    
    private String email;
    
    private RestTerminalRsp lastAccessTerminal;
    
    private String pwdLevel;
    
    private byte type;
    
    private String appId;
    
    private Long wxCloudUserId;
    
  	private byte isAdmin;
    
  //员工安全等级
  	private byte staffLevel;
    
    public byte getStaffLevel() {
		return staffLevel;
	}

	public void setStaffLevel(byte staffLevel) {
		this.staffLevel = staffLevel;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public boolean isNeedDeclaration()
    {
        return needDeclaration;
    }
    
    public void setNeedDeclaration(boolean needDeclaration)
    {
        this.needDeclaration = needDeclaration;
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
    
    public int getTimeout()
    {
        return timeout;
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
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
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }

	public String getPwdLevel() {
		return pwdLevel;
	}

	public void setPwdLevel(String pwdLevel) {
		this.pwdLevel = pwdLevel;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Long getWxCloudUserId() {
		return wxCloudUserId;
	}

	public void setWxCloudUserId(Long wxCloudUserId) {
		this.wxCloudUserId = wxCloudUserId;
	}

	public byte getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(byte isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	
    
}
