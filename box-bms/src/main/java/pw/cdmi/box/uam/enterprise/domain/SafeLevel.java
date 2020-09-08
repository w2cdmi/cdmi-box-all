package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class SafeLevel implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "safe_level_id_";
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final byte STATUS_DISABLE = 1;
    
    private Long id;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    private long accountId;
    
    @Size(max = 128)
    @NotBlank
    private String safeLevelName;
    
    @Size(max = 255)
    private String safeLevelDesc;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
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
    
    public String getSafeLevelName()
    {
        return safeLevelName;
    }
    
    public void setSafeLevelName(String safeLevelName)
    {
        this.safeLevelName = safeLevelName;
    }
    
    public String getSafeLevelDesc()
    {
        return safeLevelDesc;
    }
    
    public void setSafeLevelDesc(String safeLevelDesc)
    {
        this.safeLevelDesc = safeLevelDesc;
    }
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
