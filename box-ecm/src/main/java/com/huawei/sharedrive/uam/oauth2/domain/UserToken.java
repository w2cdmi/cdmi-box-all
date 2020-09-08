package com.huawei.sharedrive.uam.oauth2.domain;

import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;

import java.util.Date;

public class UserToken extends User
{
    
    private static final long serialVersionUID = -7027930092174727949L;
    
    private String auth;
    
    private Date createdAt;
    
    private String deviceAddress;
    
    private String proxyAddress;
    
    private String deviceAgent;
    
    private String deviceArea;
    
    private String deviceName;
    
    private String deviceOS;
    
    private String deviceSN;
    
    private byte userType;
    
    private int deviceType;
    
    private Date expiredAt;
    
    private String linkCode;
    
    private String refreshToken;
    
    private String token;
    
    private String tokenType;
    
    private byte teamSpaceFlag;
    
    private int teamSpaceMaxNum;
    
    private Integer loginRegion;
    
    private boolean needChangePassword;
    
    private boolean needDeclaration;
    
  //员工安全等级
  	private byte staffLevel;
  	
  	private byte isAdmin;
    
    public byte getStaffLevel() {
		return staffLevel;
	}

	public byte getIsAdmin() {
		return isAdmin;
	}

	public void setAdmin() {
		this.isAdmin = 1;
	}
	
	public void setIsAdmin(byte isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void setStaffLevel(byte staffLevel) {
		this.staffLevel = staffLevel;
	}

    public static void buildWxUserToken(UserToken userToken, WxUser dbWxUser) {
        userToken.setCreatedAt(dbWxUser.getCreatedAt());
        userToken.setEmail(dbWxUser.getEmail());
        userToken.setLoginName(dbWxUser.getUnionId());
        userToken.setModifiedAt(dbWxUser.getModifiedAt());
        userToken.setName(dbWxUser.getNickName());
//        userToken.setObjectSid(enterpriseUserAccount.getObjectSid());
        userToken.setRegionId(dbWxUser.getRegionId());
        userToken.setStatus(String.valueOf(dbWxUser.getStatus()));
        userToken.setTeamSpaceFlag(User.TEAMSPACE_FLAG_UNSET);
        userToken.setTeamSpaceMaxNum(0);
        userToken.setCloudUserId(dbWxUser.getCloudUserId());
        userToken.setLastLoginAt(dbWxUser.getModifiedAt());
        userToken.setMaxVersions(-1);
        userToken.setSpaceQuota(dbWxUser.getQuota());
        userToken.setDownloadBandWidth(-1L);
        userToken.setUploadBandWidth(-1L);
        userToken.setType(dbWxUser.getType());
        //此处生成的是个人账户token，个人账号中accountId=0, enterpriseId=0, userId=0
        userToken.setAccountId(0);
        userToken.setEnterpriseId(0);
        userToken.setNeedChangePassword(false);
//        userToken.setRoleId(enterpriseUserAccount.getRoleId());
//        userToken.setUserType();
    }
	
	
	public static void buildUserToken(UserToken userToken, EnterpriseUserAccount enterpriseUserAccount)
    {
        userToken.setCreatedAt(enterpriseUserAccount.getCreatedAt());
        userToken.setDepartment(enterpriseUserAccount.getDescription());
        userToken.setEmail(enterpriseUserAccount.getEmail());
        userToken.setId(enterpriseUserAccount.getUserId());
        userToken.setLoginName(enterpriseUserAccount.getName());
        userToken.setModifiedAt(enterpriseUserAccount.getModifiedAt());
        userToken.setName(enterpriseUserAccount.getAlias());
        userToken.setObjectSid(enterpriseUserAccount.getObjectSid());
        userToken.setRegionId((int) enterpriseUserAccount.getRegionId());
        userToken.setStatus(String.valueOf(enterpriseUserAccount.getAccountStatus()));
        userToken.setTeamSpaceFlag(enterpriseUserAccount.getTeamSpaceFlag() == null ? User.TEAMSPACE_FLAG_SET: Byte.parseByte(enterpriseUserAccount.getTeamSpaceFlag().toString()));
        if (null == enterpriseUserAccount.getTeamSpaceMaxNum()){
            userToken.setTeamSpaceMaxNum(-1);
        }else{
            userToken.setTeamSpaceMaxNum(enterpriseUserAccount.getTeamSpaceMaxNum());
        }
        userToken.setCloudUserId(enterpriseUserAccount.getCloudUserId());
        userToken.setLastLoginAt(enterpriseUserAccount.getLastLoginAt());
        userToken.setMaxVersions(enterpriseUserAccount.getMaxVersions());
        userToken.setSpaceQuota(enterpriseUserAccount.getSpaceQuota());
        userToken.setDownloadBandWidth(enterpriseUserAccount.getDownloadBandWidth());
        userToken.setUploadBandWidth(enterpriseUserAccount.getUploadBandWidth());
        userToken.setAccountId(enterpriseUserAccount.getAccountId());
        userToken.setEnterpriseId(enterpriseUserAccount.getEnterpriseId());
        userToken.setRoleId(enterpriseUserAccount.getRoleId());
        userToken.setUserType(enterpriseUserAccount.getType());
        userToken.setAppId(enterpriseUserAccount.getAppId());
    }
    
    public String getAuth()
    {
        return auth;
    }
    
    @Override
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
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
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public Date getExpiredAt()
    {
        if (null != expiredAt)
        {
            return (Date) expiredAt.clone();
        }
        return null;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public String getTokenType()
    {
        return tokenType;
    }
    
    public void setAuth(String auth)
    {
        this.auth = auth;
    }
    
    @Override
    public void setCreatedAt(Date createdAt)
    {
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
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
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setExpiredAt(Date expiredAt)
    {
        if (null != expiredAt)
        {
            this.expiredAt = (Date) expiredAt.clone();
        }
        else
        {
            this.expiredAt = null;
        }
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    public String getProxyAddress()
    {
        return proxyAddress;
    }
    
    public void setProxyAddress(String proxyAddress)
    {
        this.proxyAddress = proxyAddress;
    }
    
    public byte getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public void setTeamSpaceFlag(byte teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public int getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setTeamSpaceMaxNum(int teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public Integer getLoginRegion()
    {
        return loginRegion;
    }
    
    public void setLoginRegion(Integer loginRegion)
    {
        this.loginRegion = loginRegion;
    }
    
    public boolean isNeedChangePassword()
    {
        return needChangePassword;
    }
    
    public void setNeedChangePassword(boolean needChangePassword)
    {
        this.needChangePassword = needChangePassword;
    }
    
    public boolean isNeedDeclaration()
    {
        return needDeclaration;
    }
    
    public void setNeedDeclaration(boolean needDeclaration)
    {
        this.needDeclaration = needDeclaration;
    }

	public byte getUserType() {
		return userType;
	}

	public void setUserType(byte userType) {
		this.userType = userType;
	}
	
	
}
