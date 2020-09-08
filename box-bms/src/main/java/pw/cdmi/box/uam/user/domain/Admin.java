package pw.cdmi.box.uam.user.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Admin implements Serializable
{
    
    private static final long serialVersionUID = -3750995218462936811L;
    
    public static final String CACHE_KEY_PREFIX_ID = "admin_id_";
    
    public static final String CACHE_KEY_PREFIX_LOGINNAME = "Admin_Information_";
    
    public static final byte STATUS_DISABLE = 0;
    
    public static final byte STATUS_ENABLE = 1;
    
    public static final byte ROLE_ADMIN = -1;
    
    public static final byte ROLE_MANAGER = 1;
    
    private long id;
    
    private byte type;
    
    private byte domainType;
    
    private Set<AdminRole> roles;
    
    @NotEmpty
    @Size(min = 4, max = 60)
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9]+$")
    private String loginName;
    
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private String oldPasswd;
    
    @NotEmpty
    @Size(min = 2, max = 60)
    private String name;
    
    @NotEmpty
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    private String email;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Date lastLoginTime;
    
    private String objectSid;
    
    private String validateKey;
    
    private String dynamicPassword;
    
    @Size(max = 255)
    private String noteDesc;
    
    private Byte status;
    
    private int iterations;
    
    private String salt;
    
    private long enterpriseId;
    
    private Date resetPasswordAt;
    
    private String lastLoginIP;
    
    public static void htmlEscape(List<Admin> list)
    {
        if (null == list)
        {
            return;
        }
        for (Admin iter : list)
        {
            iter.setLoginName(HtmlUtils.htmlEscape(iter.getLoginName()));
            iter.setName(iter.getName());
            iter.setEmail(HtmlUtils.htmlEscape(iter.getEmail()));
            iter.setNoteDesc(HtmlUtils.htmlEscape(iter.getNoteDesc()));
        }
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public Set<AdminRole> getRoles()
    {
        return roles;
    }
    
    public String getRoleNames()
    {
        StringBuilder sb = new StringBuilder();
        if (roles != null && !roles.isEmpty())
        {
            for (AdminRole role : roles)
            {
                sb.append(role.name()).append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public void setRoles(Set<AdminRole> roles)
    {
        this.roles = roles;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
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
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public byte getDomainType()
    {
        return domainType;
    }
    
    public void setDomainType(byte domainType)
    {
        this.domainType = domainType;
    }
    
    public Date getLastLoginTime()
    {
        return lastLoginTime == null ? null : (Date) lastLoginTime.clone();
    }
    
    public void setLastLoginTime(Date lastLoginTime)
    {
        this.lastLoginTime = (lastLoginTime == null ? null : (Date) lastLoginTime.clone());
    }
    
    public String getOldPasswd()
    {
        return oldPasswd;
    }
    
    public void setOldPasswd(String oldPasswd)
    {
        this.oldPasswd = oldPasswd;
    }
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }
    
    public String getDynamicPassword()
    {
        return dynamicPassword;
    }
    
    public void setDynamicPassword(String dynamicPassword)
    {
        this.dynamicPassword = dynamicPassword;
    }
    
    public String getNoteDesc()
    {
        return noteDesc;
    }
    
    public void setNoteDesc(String noteDesc)
    {
        this.noteDesc = noteDesc;
    }
    
    public int getIterations()
    {
        return iterations;
    }
    
    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }
    
    public String getSalt()
    {
        return salt;
    }
    
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public Date getResetPasswordAt()
    {
        return resetPasswordAt == null ? null : (Date) resetPasswordAt.clone();
    }
    
    public void setResetPasswordAt(Date resetPasswordAt)
    {
        this.resetPasswordAt = (resetPasswordAt == null ? null : (Date) resetPasswordAt.clone());
    }
    
    public String getLastLoginIP()
    {
        return lastLoginIP;
    }
    
    public void setLastLoginIP(String lastLoginIP)
    {
        this.lastLoginIP = lastLoginIP;
    }
    
}
