package com.huawei.sharedrive.uam.openapi.domain.user;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;

public class TokenMeResponseUser
{
    
    public static final String STATUS_NO_SYSTEM = "nonesystemuser";
    
    public static TokenMeResponseUser convetToResponseUser(UserToken user)
    {
        TokenMeResponseUser rspUser = new TokenMeResponseUser();
        if (user.getCloudUserId() != null && user.getCloudUserId() != 0L)
        {
            rspUser.setCloudUserId(user.getCloudUserId());
           if(user.getId() == 0L){
               user.setId(user.getCloudUserId());
           }
        }
        rspUser.setDescription(user.getDepartment());
        rspUser.setEmail(user.getEmail());
        if (user.getId() == 0L)
        {
            rspUser.setStatus(STATUS_NO_SYSTEM);
        }else{
            rspUser.setId(user.getId());
            rspUser.setStatus(user.getStatus());
            rspUser.setRegionId(user.getRegionId());
            rspUser.setAppId(user.getAppId());
            rspUser.setDeviceAddress(user.getDeviceAddress());
            rspUser.setDeviceAgent(user.getDeviceAgent());
            rspUser.setDeviceArea(user.getDeviceArea());
            rspUser.setDeviceSN(user.getDeviceSN());
            rspUser.setDeviceType(user.getDeviceType());
            rspUser.setDeviceOS(user.getDeviceOS());
            rspUser.setDeviceName(user.getDeviceName());
            rspUser.setEnterpriseId(user.getEnterpriseId());
        }
        rspUser.setLoginName(user.getLoginName());
        rspUser.setName(user.getName());
        rspUser.setSpaceQuota(user.getSpaceQuota());
        rspUser.setMaxVersions(user.getMaxVersions());
        rspUser.setAccountId(user.getAccountId());
        rspUser.setLoginRegion(user.getLoginRegion());
        rspUser.setNeedChangePassword(user.isNeedChangePassword());
        rspUser.setNeedDeclaration(user.isNeedDeclaration());
        rspUser.setIsAdmin(user.getIsAdmin());
        return rspUser;
    }
    
    private String appId;
    
    private Long cloudUserId;
    
    private String description;
    
    private String email;
    
    private Long id;
    
    private String loginName;
    
    private String name;
    
    private Integer regionId;
    
    private String status;
    
    private long spaceQuota;
    
    private String deviceAddress;
    
    private String deviceAgent;
    
    private String deviceArea;
    
    private String deviceSN;
    
    private String deviceOS;
    
    private String deviceName;
    
    private int deviceType;
    
    private int maxVersions;
    
    private Long accountId;
    
    private Integer loginRegion;
    
    private boolean needChangePassword;
    
    private boolean needDeclaration;
    
    private Long wxCloudUserId;
    
    private byte isAdmin;
    
    private Long enterpriseId;
    
    private String enterpriseName;
    
    public Long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getDeviceAddress()
    {
        return deviceAddress;
    }
    
    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public String getDeviceArea()
    {
        return deviceArea;
    }
    
    public void setDeviceArea(String deviceArea)
    {
        this.deviceArea = deviceArea;
    }
    
    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public int getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(int maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
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

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
    
    
}
