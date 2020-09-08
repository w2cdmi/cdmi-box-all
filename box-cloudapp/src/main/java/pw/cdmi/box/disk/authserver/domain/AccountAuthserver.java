package pw.cdmi.box.disk.authserver.domain;

import java.io.Serializable;

import javax.validation.constraints.Size;

public class AccountAuthserver implements Serializable
{
    
    private static final long serialVersionUID = 1433866462020306173L;
    
    public static final byte MANUAL_OPEN_ACCOUNT = 1;
    
    public static final byte AUTO_OPEN_ACCOUNT = 2;
    
    public static final byte UNDEFINED_OPEN_ACCOUNT = -1;
    
    private long authserverId;
    
    private long accountId;
    
    @Size(max = 32)
    private String ipStart;
    
    @Size(max = 32)
    private String ipEnd;
    
    private long ipStartValue = -1;
    
    private long ipEndValue = -1;
    
    private byte type = MANUAL_OPEN_ACCOUNT;
    
    private String authAppId;
    
    private Boolean isBind;
    
    private String description;
    
    public void setAuthserverId(long authserverId)
    {
        this.authserverId = authserverId;
    }
    
    public long getAuthserverId()
    {
        return authserverId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setIpStart(String ipStart)
    {
        this.ipStart = ipStart;
    }
    
    public String getIpStart()
    {
        return ipStart;
    }
    
    public void setIpEnd(String ipEnd)
    {
        this.ipEnd = ipEnd;
    }
    
    public String getIpEnd()
    {
        return ipEnd;
    }
    
    public void setIpStartValue(long ipStartValue)
    {
        this.ipStartValue = ipStartValue;
    }
    
    public long getIpStartValue()
    {
        return ipStartValue;
    }
    
    public void setIpEndValue(long ipEndValue)
    {
        this.ipEndValue = ipEndValue;
    }
    
    public long getIpEndValue()
    {
        return ipEndValue;
    }
    
    public void setAuthAppId(String authAppId)
    {
        this.authAppId = authAppId;
    }
    
    public String getAuthAppId()
    {
        return authAppId;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public Boolean getIsBind()
    {
        return isBind == null ? Boolean.FALSE : isBind;
    }
    
    public void setIsBind(Boolean isBind)
    {
        this.isBind = isBind;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
}
