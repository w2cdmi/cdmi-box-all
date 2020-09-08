package pw.cdmi.box.disk.user.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

public class EnterpriseUser implements Serializable
{
    
    private static final long serialVersionUID = 737708534667010455L;
    
    public static final byte ROLE_ENTERPRISE_MANAGER = 1;
    
    public static final byte ROLE_ENTERPRISE_MEMBER = 2;

    public static final byte TYPE_GUST = 0;//临时员工
    public static final byte TYPE_MANAGER = 1;//部门主管
    public static final byte TYPE_MEMBER = 2; //普通员工
    public static final byte TYPE_OUTSOURCE = 3; //外包人员
    
    private long id;
    
    @Size(max = 255)
    private String password;
    
    @NotNull
    private long enterpriseId;

    @NotNull
    private byte role = 2;
    
    @Size(max = 255)
    private String objectSid;
    
    @NotNull
    @Size(max = 127)
    private String name;
    
    @Size(max = 255)
    private String alias;
    
    @Size(max = 255)
    private String description;
    
    @NotNull
    @Size(max = 255)
    @Email
    private String email;
    
    @Size(max = 20)
    private String mobile;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Date lastLoginAt;
    
    private byte status = 1;
    
    private byte ldapStatus = 1;
    
    private long userSource = 0;
    
    @Size(max = 128)
    private String validateKey;
    
    private int iterations = 1000;
    
    @Size(max = 255)
    private String salt;
    
    private String tableSuffix;

    private String department;

    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }

    public void setRole(Byte role)
    {
        this.role = role;
    }
    
    public Byte getRole()
    {
        return role;
    }
    
    public void setObjectSid(String objectSid)
    {
        this.objectSid = objectSid;
    }
    
    public String getObjectSid()
    {
        return objectSid;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setAlias(String alias)
    {
        this.alias = alias;
    }
    
    public String getAlias()
    {
        return alias;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
    
    public String getMobile()
    {
        return mobile;
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
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public void setLastLoginAt(Date lastLoginAt)
    {
        if (lastLoginAt == null)
        {
            this.lastLoginAt = null;
        }
        else
        {
            this.lastLoginAt = (Date) lastLoginAt.clone();
        }
    }
    
    public Date getLastLoginAt()
    {
        if (lastLoginAt == null)
        {
            return null;
        }
        return (Date) lastLoginAt.clone();
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setLdapStatus(byte ldapStatus)
    {
        this.ldapStatus = ldapStatus;
    }
    
    public byte getLdapStatus()
    {
        return ldapStatus;
    }
    
    public void setUserSource(long userSource)
    {
        this.userSource = userSource;
    }
    
    public long getUserSource()
    {
        return userSource;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setIterations(int iterations)
    {
        this.iterations = iterations;
    }
    
    public int getIterations()
    {
        return iterations;
    }
    
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    
    public String getSalt()
    {
        return salt;
    }
    
    public String getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(String tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public static User copyEnterpriseUser(EnterpriseUser enterpriseUser)
    {
        User user = new User();
        if (null != enterpriseUser)
        {
            user.setId(enterpriseUser.getId());
            user.setPassword(enterpriseUser.getPassword());
            user.setEnterpriseId(enterpriseUser.getEnterpriseId());
            user.setObjectSid(enterpriseUser.getObjectSid());
            user.setName(enterpriseUser.getAlias());
            user.setLoginName(enterpriseUser.getName());
            user.setDepartment(enterpriseUser.getDescription());
            user.setEmail(enterpriseUser.getEmail());
            user.setModifiedAt(enterpriseUser.getModifiedAt());
            user.setCreatedAt(enterpriseUser.getCreatedAt());
            user.setStatus(String.valueOf(enterpriseUser.getStatus()));
        }
        return user;
    }
}
