package com.huawei.sharedrive.app.oauth2.domain;

import java.util.Calendar;
import java.util.Date;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.user.domain.User;

public class UserToken extends User implements DataServerToken
{
	
	private byte isAdmin;
    
    /** 默认数据子系统的TOKEN超时时间 */
    public final static int DATA_SERVER_EXPIREDTIME = 10 * 60 * 1000;
    
    /** 默认超时时间 */
    public static final long EXPIRED_TIME = 30 * 60 * 1000;
    
    private static final long serialVersionUID = -7027930092174727949L;
    
    public UserToken()
    {
        
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public UserToken(String tokenType, String auth, String token, long id, Date expiredAt,
        String nodeLastModified)
    {
        this.tokenType = tokenType;
        this.auth = auth;
        this.token = token;
        this.setId(id);
        Calendar now = Calendar.getInstance();
        this.setCreatedAt(now.getTime());
        this.setExpiredAt(expiredAt);
        this.setNodeLastModified(nodeLastModified);
    }
    
    public void copyFrom(User user)
    {
        this.setCreatedAt(user.getCreatedAt());
//        this.setDepartment(user.getDepartment());
        this.setDomain(user.getDomain());
        this.setEmail(user.getEmail());
        this.setId(user.getId());
        this.setCloudUserId(user.getId());
        this.setLabel(user.getLabel());
        this.setLoginName(user.getLoginName());
        this.setModifiedAt(user.getModifiedAt());
        this.setName(user.getName());
        this.setObjectSid(user.getObjectSid());
        this.setPassword(user.getPassword());
        this.setRecycleDays(user.getRecycleDays());
        this.setRegionId(user.getRegionId());
        this.setSpaceQuota(user.getSpaceQuota());
        this.setSpaceUsed(user.getSpaceUsed());
        this.setStatus(user.getStatus());
        this.setType(user.getType());
        this.setAccountId(user.getAccountId());
    }
    
    private Account accountVistor;
    
    /** 权限描述信息 */
    private String auth;
    
    private Long cloudUserId;
    
    private Date createdAt;
    
    private String date;
    
    /** 客户端IP地址 */
    private String deviceAddress;
    
    /** 客户端软件版本 */
    private String deviceAgent;
    
    private String deviceArea;
    
    /** 客户端名称 */
    private String deviceName;
    
    /** 客户端软件信息 */
    private String deviceOS;
    
    /** 设备标识 */
    private String deviceSN;
    
    /** 设备类型 */
    private int deviceType;
    
    /** 过期时间 */
    private Date expiredAt;
    
    private String linkCode;
    
    private Integer loginRegion;
    
    private boolean needChangePassword;
    
    private boolean needDeclaration;
    
    /**
     * 节点最后修改时间, 临时token使用, 用于DC文件下载时header头部"Last-Modified"字段的填写
     */
    private String nodeLastModified;
    
    /**
     * 外链提取码
     */
    private String plainAccessCode;
    
    /** 刷新Token */
    private String refreshToken;
    
    /** 访问 临时Token 信息 */
    private String token;
    
    /** 授权Token 类型 */
    private String tokenType;
    
    private Integer networkType;
    
    private Long enterpriseId;
    
    public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	private String enterpriseName;

    public Account getAccountVistor()
    {
        return accountVistor;
    }
    
    @Override
    public String getAuth()
    {
        return auth;
    }
    
    public Long getCloudUserId()
    {
        return cloudUserId;
    }
    
    @Override
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDate()
    {
        return date;
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
    
    @Override
    public Date getExpiredAt()
    {
        if (expiredAt == null)
        {
            return null;
        }
        return (Date) expiredAt.clone();
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public Integer getLoginRegion()
    {
        return loginRegion;
    }
    
    public String getNodeLastModified()
    {
        return nodeLastModified;
    }
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public String getRefreshToken()
    {
        return refreshToken;
    }
    
    @Override
    public String getToken()
    {
        return token;
    }
    
    @Override
    public String getTokenType()
    {
        return tokenType;
    }
    
    public void setAccountVistor(Account accountVistor)
    {
        this.accountVistor = accountVistor;
    }
    
    @Override
    public void setAuth(String auth)
    {
        this.auth = auth;
    }
    
    public void setCloudUserId(Long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    @Override
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    public void setDate(String date)
    {
        this.date = date;
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
    
    @Override
    public void setExpiredAt(Date expiredAt)
    {
        if (expiredAt == null)
        {
            this.expiredAt = null;
        }
        else
        {
            this.expiredAt = (Date) expiredAt.clone();
        }
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setLoginRegion(Integer loginRegion)
    {
        this.loginRegion = loginRegion;
    }
    
    public void setNodeLastModified(String nodeLastModified)
    {
        this.nodeLastModified = nodeLastModified;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
    public void setRefreshToken(String refreshToken)
    {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public void setToken(String token)
    {
        this.token = token;
    }
    
    @Override
    public void setTokenType(String tokenType)
    {
        this.tokenType = tokenType;
    }
    
    public Integer getNetworkType()
    {
        return networkType;
    }
    
    public void setNetworkType(Integer networkType)
    {
        this.networkType = networkType;
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

	public byte getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(byte isAdmin) {
		this.isAdmin = isAdmin;
	}
    
    
}
