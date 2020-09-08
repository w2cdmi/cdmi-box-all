package pw.cdmi.box.uam.enterprise.domain;

import java.util.Date;

public class AccountSecConfigExt
{
    
    private int accountId;
    
    private Date createdAt;
    
    private Byte enableFileSec;
    
    private Byte enableSpaceSec;
    
    private Byte enableFileCopySec;
    
    private Date modifiedAt;
    
    public int getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(int accountId)
    {
        this.accountId = accountId;
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
    
    public Byte getEnableFileSec()
    {
        return enableFileSec;
    }
    
    public void setEnableFileSec(Byte enableFileSec)
    {
        this.enableFileSec = enableFileSec;
    }
    
    public Byte getEnableSpaceSec()
    {
        return enableSpaceSec;
    }
    
    public void setEnableSpaceSec(Byte enableSpaceSec)
    {
        this.enableSpaceSec = enableSpaceSec;
    }
    
    public Byte getEnableFileCopySec()
    {
        return enableFileCopySec;
    }
    
    public void setEnableFileCopySec(Byte enableFileCopySec)
    {
        this.enableFileCopySec = enableFileCopySec;
    }
    
}
