package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;
import java.util.Date;

public class Terminal implements Serializable
{
    
    // android
    public static final int CLIENT_TYPE_ANDROID = 2;
    
    public static final String CLIENT_TYPE_ANDROID_STR = "android";
    
    // ios
    public static final int CLIENT_TYPE_IOS = 3;
    
    public static final String CLIENT_TYPE_IOS_STR = "ios";
    
    // PC
    public static final int CLIENT_TYPE_PC = 1;
    
    public static final String CLIENT_TYPE_PC_STR = "pc";
    
    // web
    public static final int CLIENT_TYPE_WEB = 0;
    
    public static final String CLIENT_TYPE_WEB_STR = "web";
    
    public static final byte STATUS_CURRENT = 1;
    
    public static final byte STATUS_IGNORE = 2;
    
    public static final byte STATUS_NORMAL = 0;
    
    private static final long serialVersionUID = 1411041777085531943L;
    
    private Date createdAt;
    
    private String deviceAgent;
    
    private String deviceName;
    
    private String deviceOS;
    
    private String deviceSn;
    
    private int deviceType;
    
    private String token;
    
    private long id;
    
    private Date lastAccessAt;
    
    private String lastAccessIP;
    
    private byte status;
    
    private long userId;
    
    public Terminal()
    {
        super();
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public Terminal(long userId, String deviceSn, String deviceName, String lastAccessIP, int deviceType,
        String deviceOS, String deviceAgent)
    {
        super();
        this.userId = userId;
        this.deviceSn = deviceSn;
        this.deviceName = deviceName;
        this.lastAccessIP = lastAccessIP;
        this.deviceType = deviceType;
        this.deviceOS = deviceOS;
        this.deviceAgent = deviceAgent;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDeviceAgent()
    {
        return deviceAgent;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public String getDeviceOS()
    {
        return deviceOS;
    }
    
    public String getDeviceSn()
    {
        return deviceSn;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public long getId()
    {
        return id;
    }
    
    public Date getLastAccessAt()
    {
        if (lastAccessAt == null)
        {
            return null;
        }
        return (Date) lastAccessAt.clone();
    }
    
    public String getLastAccessIP()
    {
        return lastAccessIP;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
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
    
    public void setDeviceAgent(String deviceAgent)
    {
        this.deviceAgent = deviceAgent;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public void setDeviceOS(String deviceOS)
    {
        this.deviceOS = deviceOS;
    }
    
    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLastAccessAt(Date lastAccessAt)
    {
        if (lastAccessAt == null)
        {
            this.lastAccessAt = null;
        }
        else
        {
            this.lastAccessAt = (Date) lastAccessAt.clone();
        }
    }
    
    public void setLastAccessIP(String lastAccessIP)
    {
        this.lastAccessIP = lastAccessIP;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public String getUniqueId()
    {
        if (deviceType == CLIENT_TYPE_WEB)
        {
            return deviceType + deviceAgent + lastAccessIP + userId;
        }
        return deviceSn + userId;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
}
