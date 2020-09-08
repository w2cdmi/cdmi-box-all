/**
 * 
 */
package com.huawei.sharedrive.isystem.user.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author q90003805
 * 
 */
public class Admin implements Serializable
{
    private static final long serialVersionUID = -3750995218462936811L;
    
    public static final String CACHE_KEY_PREFIX_ID = "admin_id_";
    
    public static final byte STATUS_DISABLE = 0;
    
    public static final byte STATUS_ENABLE = 1;
    
    public static final byte STATUS_CONFIG = 2;
    private long id;
    
    private byte type;
    
    private byte domainType;
    
    private Set<AdminRole> roles;
    
    @NotBlank
    @Size(min = 4, max = 60)
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9]+$")
    private String loginName;
    
    @JsonIgnore
    private String password;
    
    @JsonIgnore
    private String oldPasswd;
    
    @NotBlank
    @Size(min = 2, max = 60)
    private String name;
    
    @NotNull
    @Size(max = 60)
    private String email;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Date lastLoginTime;
    
    private String objectSid;
    
    private String validateKey;
    
    private String dynamicPassword;
    
    private Byte status;
    
    private int iterations;
    
    private String salt;
    
    private Date resetPasswordAt;
    
    private String lastLoginIP;
    
    private String noteDesc;
    
    
    public String getNoteDesc() {
		return noteDesc;
	}

	public void setNoteDesc(String noteDesc) {
		this.noteDesc = noteDesc;
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
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt != null)
        {
            this.modifiedAt = new Date(modifiedAt.getTime());
        }
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
        return lastLoginTime != null ? new Date(lastLoginTime.getTime()) : null;
    }
    
    public void setLastLoginTime(Date lastLoginTime)
    {
        this.lastLoginTime = lastLoginTime == null ? null : (Date) lastLoginTime.clone();
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
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
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
    
    public Date getResetPasswordAt()
    {
        return resetPasswordAt == null ? null : (Date) resetPasswordAt.clone();
    }
    
    public void setResetPasswordAt(Date resetPasswordAt)
    {
        this.resetPasswordAt = resetPasswordAt == null ? null : (Date) resetPasswordAt.clone();
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
