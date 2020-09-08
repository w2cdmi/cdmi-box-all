package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

public class AccountSecConfig implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 2625282806063396120L;
    
    public final static byte ENABLE = 1;
    
    public final static byte DISABLE = 0;
    
    private int accountId;
    
    private Date createdAt;
    
    private byte enableFileSec;
    
    private byte enableSpaceSec;
    
    private byte enableFileCopySec;
    
    private Date modifiedAt;
    
    public int getAccountId()
    {
        return accountId;
    }
    
    public Date getCreatedAt()
    {
        return createdAt == null ? null : (Date) createdAt.clone();
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = (createdAt == null ? null : (Date) createdAt.clone());
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt == null ? null : (Date) modifiedAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        this.modifiedAt = (modifiedAt == null ? null : (Date) modifiedAt.clone());
    }
    
    public byte getEnableFileSec()
    {
        return enableFileSec;
    }
    
    public byte getEnableSpaceSec()
    {
        return enableSpaceSec;
    }
    
    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
    }
    
    public void setEnableFileSec(byte enableFileSec)
    {
        this.enableFileSec = enableFileSec;
    }
    
    public void setEnableSpaceSec(byte enableSpaceSec)
    {
        this.enableSpaceSec = enableSpaceSec;
    }
    
    public byte getEnableFileCopySec()
    {
        return enableFileCopySec;
    }
    
    public void setEnableFileCopySec(byte enableFileCopySec)
    {
        this.enableFileCopySec = enableFileCopySec;
    }
    
}
