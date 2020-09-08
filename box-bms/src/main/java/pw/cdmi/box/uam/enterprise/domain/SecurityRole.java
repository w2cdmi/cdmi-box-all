package pw.cdmi.box.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.util.HtmlUtils;

public class SecurityRole implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    public static final String CACHE_KEY_PREFIX_ID = "security_role_id_";
    
    public static final String STATUS_ENABLE_STR = "enable";
    
    public static final String STATUS_DISABLE_STR = "disable";
    
    public static final byte STATUS_ENABLE = 0;
    
    public static final byte STATUS_DISABLE = 1;
    
    private Long id;
    
    @Size(max = 128)
    @NotBlank
    private String roleName;
    
    @Size(max = 255)
    private String roleDesc;
    
    private long accountId;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    public static void htmlEscape(List<SecurityRole> list)
    {
        if (null == list)
        {
            return;
        }
        for (SecurityRole iter : list)
        {
            iter.setRoleName(HtmlUtils.htmlEscape(iter.getRoleName()));
            iter.setRoleDesc(HtmlUtils.htmlEscape(iter.getRoleDesc()));
        }
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    
    public String getRoleDesc()
    {
        return roleDesc;
    }
    
    public void setRoleDesc(String roleDesc)
    {
        this.roleDesc = roleDesc;
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
    
    public long getAccountId()
    {
        return accountId;
    }
    
    public void setAccountId(long accountId)
    {
        this.accountId = accountId;
    }
    
}
