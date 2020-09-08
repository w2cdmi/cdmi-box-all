package pw.cdmi.box.uam.log.domain;

import pw.cdmi.common.log.ClientType;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.log.Level;

public class UserLogRes
{
    private String appId;
    
    private String clientAddress;
    
    private String clientDeviceName;
    
    private String clientDeviceSN;
    
    private String clientOS;
    
    private String clientType;
    
    private String clientVersion;
    
    private long createdAt;
    
    private String detail;
    
    private String id;
    
    private String level;
    
    private String loginName;
    
    private String type;
    
    private Long userId;
    
    public static final String KEYWORD = "[kd]";
    
    private Boolean operateResult;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getClientAddress()
    {
        return clientAddress;
    }
    
    public void setClientAddress(String clientAddress)
    {
        this.clientAddress = clientAddress;
    }
    
    public String getClientDeviceName()
    {
        return clientDeviceName;
    }
    
    public void setClientDeviceName(String clientDeviceName)
    {
        this.clientDeviceName = clientDeviceName;
    }
    
    public String getClientDeviceSN()
    {
        return clientDeviceSN;
    }
    
    public void setClientDeviceSN(String clientDeviceSN)
    {
        this.clientDeviceSN = clientDeviceSN;
    }
    
    public String getClientOS()
    {
        return clientOS;
    }
    
    public void setClientOS(String clientOS)
    {
        this.clientOS = clientOS;
    }
    
    public String getClientType()
    {
        return clientType;
    }
    
    public void setClientType(String clientType)
    {
        this.clientType = clientType;
    }
    
    public String getClientVersion()
    {
        return clientVersion;
    }
    
    public void setClientVersion(String clientVersion)
    {
        this.clientVersion = clientVersion;
    }
    
    public long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public String getDetail()
    {
        return detail;
    }
    
    public void setDetail(String detail)
    {
        this.detail = detail;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getLevel()
    {
        return level;
    }
    
    public void setLevel(String level)
    {
        if ("INFO".equalsIgnoreCase(level))
        {
            operateResult = true;
        }
        if ("ERROR".equalsIgnoreCase(level))
        {
            operateResult = false;
        }
        this.level = level;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public Boolean getOperateResult()
    {
        return operateResult == null ? Boolean.FALSE : operateResult;
    }
    
    public void setOperateResult(Boolean operateResult)
    {
        this.operateResult = operateResult == null ? Boolean.FALSE : operateResult;
    }
    
    public static UserLogRes builderUserLogRes(UserLog userLog)
    {
        UserLogRes outUserLog = new UserLogRes();
        outUserLog.setAppId(userLog.getAppId());
        outUserLog.setClientAddress(userLog.getClientAddress());
        outUserLog.setClientDeviceName(userLog.getClientDeviceName());
        outUserLog.setClientDeviceSN(userLog.getClientDeviceSN());
        outUserLog.setClientOS(userLog.getClientOS());
        try
        {
            ClientType clientType = ClientType.build(userLog.getClientType());
            outUserLog.setClientType(clientType == null ? null : clientType.toShowString());
        }
        catch (Exception e)
        {
            outUserLog.setClientType(null);
        }
        outUserLog.setClientVersion(userLog.getClientVersion());
        if (userLog.getCreatedAt() != null)
        {
            outUserLog.setCreatedAt(userLog.getCreatedAt().getTime());
        }
        String keyword = userLog.getKeyword();
        outUserLog.setDetail(userLog.getDetail().replace(KEYWORD, keyword != null ? keyword : ""));
        outUserLog.setId(userLog.getId());
        try
        {
            Level level = Level.build(userLog.getLevel());
            outUserLog.setLevel(level == null ? null : level.toShowString());
        }
        catch (Exception e)
        {
            outUserLog.setLevel(null);
        }
        outUserLog.setLoginName(userLog.getLoginName());
        outUserLog.setUserId(userLog.getUserId());
        try
        {
            UserLogType userLogType = UserLogType.build(userLog.getType());
            outUserLog.setType(userLogType == null ? null : userLogType.toString());
        }
        catch (Exception e)
        {
            outUserLog.setType(null);
        }
        return outUserLog;
    }
}
