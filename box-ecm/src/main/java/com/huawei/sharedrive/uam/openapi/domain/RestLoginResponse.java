package com.huawei.sharedrive.uam.openapi.domain;

import java.util.Date;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;

public class RestLoginResponse extends RestResponse
{
    
    public static final byte TRUE = 1;
	
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

    private String enterpriseName;

    private String domain;
    
    private boolean needChangePassword;
    
    private boolean needDeclaration;

    private String mobile;

    private String email;
    
    private RestTerminalRsp lastAccessTerminal;
    
    private String pwdLevel;
    
    private byte type;
    
    private String appId;
    //员工安全等级
  	private byte staffLevel;
  	
  	private Long wxCloudUserId;

  	private byte isAdmin;
  	
 	private byte isWxEnterprise;

    private  EnterpriseInfo[] enterpriseList;

  	public byte getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(byte isAdmin) {
		this.isAdmin = isAdmin;
	}

	public RestLoginResponse() {
  	    super(GlobalErrorMessage.OK);
    }

    public RestLoginResponse(GlobalErrorMessage error) {
  	    super(error);
    }

	public static RestLoginResponse fillRestLoginResponse(UserToken userToken)
    {
        RestLoginResponse restLoginResponse = new RestLoginResponse();
        if (userToken.getExpiredAt() != null)
        {
            long timeout = (userToken.getExpiredAt().getTime() - new Date().getTime()) / 1000;
            restLoginResponse.setTimeout((int) timeout); 
        }
        restLoginResponse.setToken(userToken.getToken());
        restLoginResponse.setRefreshToken(userToken.getRefreshToken());
        restLoginResponse.setLoginName(userToken.getLoginName());
        restLoginResponse.setUserId(userToken.getId());
        restLoginResponse.setCloudUserId(userToken.getCloudUserId());
        restLoginResponse.setAccountId(userToken.getAccountId());
        restLoginResponse.setEnterpriseId(userToken.getEnterpriseId());
        restLoginResponse.setUploadQos(-1);
        restLoginResponse.setDownloadQos(-1);
        restLoginResponse.setType(userToken.getUserType());
        restLoginResponse.setStaffLevel(userToken.getStaffLevel());
        restLoginResponse.setAlias(userToken.getName());

        return restLoginResponse;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
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
    
    public boolean isNeedDeclaration()
    {
        return needDeclaration;
    }

    public void setNeedDeclaration(boolean needDeclaration)
    {
        this.needDeclaration = needDeclaration;
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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
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
    
    public RestTerminalRsp getLastAccessTerminal()
    {
        return lastAccessTerminal;
    }
    
    public void setLastAccessTerminal(RestTerminalRsp lastAccessTerminal)
    {
        this.lastAccessTerminal = lastAccessTerminal;
    }

	public String getPwdLevel() {
		return pwdLevel;
	}

	public void setPwdLevel(String pwdLevel) {
		this.pwdLevel = pwdLevel;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

    public byte getStaffLevel() {
        return staffLevel;
    }

    public void setStaffLevel(byte staffLevel) {
        this.staffLevel = staffLevel;
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


    public EnterpriseInfo[] getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(EnterpriseInfo[] enterpriseList) {
        this.enterpriseList = enterpriseList;
    }

	public byte getIsWxEnterprise() {
		return isWxEnterprise;
	}

	public void setIsWxEnterprise(byte isWxEnterprise) {
		this.isWxEnterprise = isWxEnterprise;
	}
    
    
}
