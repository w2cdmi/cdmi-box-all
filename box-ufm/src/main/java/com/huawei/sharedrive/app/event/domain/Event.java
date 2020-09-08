/**
 * 
 */
package com.huawei.sharedrive.app.event.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.common.log.UserLog;

/**
 * @author q90003805
 * 
 */
public class Event implements Serializable
{
    private static final long serialVersionUID = 3516121656058173516L;
    
    /** 操作时间 */
    private Date createdAt;
    
    /** 操作用户ID */
    private long createdBy;
    
    /** 操作目标对象 */
    private INode dest;
    
    /** 客户端IP地址 */
    private String deviceAddress;
    
    /** 客户端软件信息 */
    private String deviceAgent;
    
    /** 设备区域 */
    private String deviceArea;
    
    /** 设备标识 */
    private String deviceSN;
    
    /** 设备类型 */
    private int deviceType;
    
    private String keyword;
    
    private UserLogType optType;
    
    private String[] params;
    
    /** 操作源对象 */
    private INode source;
    
    /** 事件类型 */
    private EventType type;
    
    private UserToken userToken;
    
    public Event()
    {
        
    }
    
    public Event(UserToken userToken)
    {
        this.userToken = userToken;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public Event(UserToken userToken, INode source, EventType type, UserLogType optType, String[] params, String keyword)
    {
        this.userToken = userToken;
        this.source = source;
        this.type = type;
        this.optType = optType;
        if (params == null)
        {
            this.params = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        else
        {
            this.params = params.clone();
        }
        this.keyword = keyword;
        this.createdAt = new Date();
        this.createdBy = userToken.getId();
    }
    
    public UserLog convertToUserLog()
    {
        UserLog userLog = new UserLog();
        userLog.setAppId(this.userToken.getAppId());
        userLog.setClientAddress(this.userToken.getDeviceAddress());
        userLog.setClientDeviceName(this.userToken.getDeviceName());
        userLog.setClientDeviceSN(this.userToken.getDeviceSN());
        userLog.setClientOS(this.userToken.getDeviceOS());
        userLog.setClientType((byte) this.userToken.getDeviceType());
        userLog.setClientVersion(this.userToken.getDeviceAgent());
        userLog.setCreatedAt(this.createdAt);
        if (this.optType != null)
        {
            userLog.setDetail(this.optType.getDetails(this.params));
            userLog.setLevel(this.optType.getLevel());
            userLog.setType(this.optType.getTypeCode());
        }
        userLog.setId(UUID.randomUUID().toString());
        userLog.setKeyword(this.keyword);
        userLog.setLoginName(this.userToken.getLoginName());
        try
        {
            userLog.setUserId(this.userToken.getCloudUserId());
        }
        catch (Exception e)
        {
            userLog.setUserId(0L);
        }
        return userLog;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public INode getDest()
    {
        return dest;
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
    
    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    public int getDeviceType()
    {
        return deviceType;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public UserLogType getOptType()
    {
        return optType;
    }
    
    public String[] getParams()
    {
        if (params == null)
        {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return params.clone();
    }
    
    public INode getSource()
    {
        return source;
    }
    
    public EventType getType()
    {
        return type;
    }
    
    public UserToken getUserToken()
    {
        return userToken;
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDest(INode dest)
    {
        this.dest = dest;
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
    
    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    public void setDeviceType(int deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public void setOptType(UserLogType optType)
    {
        this.optType = optType;
    }
    
    public void setParams(String[] params)
    {
        if (params == null)
        {
            this.params = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        else
        {
            this.params = params.clone();
        }
    }
    
    public void setSource(INode source)
    {
        this.source = source;
    }
    
    public void setType(EventType type)
    {
        this.type = type;
    }
    
    public void setUserToken(UserToken userToken)
    {
        this.userToken = userToken;
    }
}
