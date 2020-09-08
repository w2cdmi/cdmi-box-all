package com.huawei.sharedrive.uam.enterpriseuser.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.util.PatternRegUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("PMD.PreserveStackTrace")
public class EnterpriseUser implements Serializable
{
    private static final long serialVersionUID = 2707751074329653292L;
    
    public static final byte ROLE_ENTERPRISE_MANAGER = 1;
    
    public static final byte ROLE_ENTERPRISE_MEMBER = 2;
    
    public static final byte TYPE_TEMPORARY = 0; //临时账号
    public static final byte TYPE_OUTSOURCE = 1; //外包人员
    public static final byte TYPE_MEMBER = 2; //正式员工

    public static final byte AD_EXISTS = 1;
    
    public static final byte AD_NOTEXISTS = 2;
    
    public static final byte AD_UNCHECK = 3;
    
    public static final byte STATUS_ENABLE = 0;
    public final static byte STATUS_NOT_SYNC = -1;//未授权
    public final static byte STATUS_DISMISS = -2; //标记为离职
    public final static byte STATUS_HAND_OVER = -3; //移交完成
    public final static byte STATUS_DELETE = -4; //删除

    public static final int NAME_MAX_LENGTH = 127;
    
    public static final int NAME_MIN_LENGTH = Integer.parseInt(PropertiesUtils.getProperty("enterpise.user.name.min.length", "2"));
    
    private long id;
    
    @Size(max = 1024)
    @JsonIgnore
    private String password;
    
    @NotNull
    private long enterpriseId;
    
    @Size(max = 255)
    private String objectSid;
    
    @NotBlank
    @Size(max = 127)
    @Pattern(regexp = "^(?!.*((<)|(>)|(/)|(\\\\))).*$")
    private String name;
    
    @NotBlank
    @Size(max = 127)
    private String alias;
    
    @Size(max = 255)
    private String description;
    
/*
    @NotBlank
    @Size(max = 127)
    @Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
*/
    private String email;
    
    @Size(max = 20)
    @Pattern(regexp = "^[0-9 +-]*$")
    private String mobile;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Date lastLoginAt;
    
    private byte status = STATUS_ENABLE;
    
    private byte ldapStatus = 1;
    
    private long userSource = 0;
    
    @Size(max = 1024)
    @JsonIgnore
    private String validateKey;
    @JsonIgnore
    private String validateKeyEncodeKey;
    
    private int iterations = 1000;
    
    private byte role;
    
    @Size(max = 255)
    @JsonIgnore
    private String salt;
    
    private String tableSuffix;
    
    private int regionId;
    
    private long cloudUserId;
    
    private String userDn;
    //员工工号
    private String staffNo;
    
    //员工安全等级
  	private byte staffSecretLevel;
  	
    //员工类型
  	private byte type;
    
	private byte ldapTempStatus = 1;
    
    private Date resetPasswordAt;

    public static void checkCreateUserParament(EnterpriseUser user)
    {
        
        if (StringUtils.isBlank(user.getName()) || user.getName().length() > 127)
        {
            throw new InvalidParamterException("invalid name:" + user.getName());
        }
        if (StringUtils.isBlank(user.getAlias()) || user.getAlias().length() > 127)
        {
            throw new InvalidParamterException("invalid alias:" + user.getAlias());
        }
        try
        {
            PatternRegUtil.checkMailLegal(user.getEmail());
        }
        catch (Exception e)
        {
            throw new InvalidParamterException("invalid email:" + user.getEmail());
        }
        if (StringUtils.isNotBlank(user.getMobile()) && user.getMobile().length() > 20)
        {
            throw new InvalidParamterException("invalid mobile:" + user.getMobile());
        }
        if (StringUtils.isNotBlank(user.getDescription()) && user.getDescription().length() > 255)
        {
            throw new InvalidParamterException("invalid description:" + user.getDescription());
        }
    }
    
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
        if (null != createdAt)
        {
            this.createdAt = (Date) createdAt.clone();
        }
        else
        {
            this.createdAt = null;
        }
    }
    
    public Date getCreatedAt()
    {
        if (null != createdAt)
        {
            return (Date) createdAt.clone();
        }
        return null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (null != modifiedAt)
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
        else
        {
            this.modifiedAt = null;
        }
    }
    
    public Date getModifiedAt()
    {
        if (null != modifiedAt)
        {
            return (Date) modifiedAt.clone();
        }
        return null;
    }
    
    public void setLastLoginAt(Date lastLoginAt)
    {
        if (null != lastLoginAt)
        {
            this.lastLoginAt = (Date) lastLoginAt.clone();
        }
        else
        {
            this.lastLoginAt = null;
        }
    }
    
    public Date getLastLoginAt()
    {
        if (null != lastLoginAt)
        {
            return (Date) lastLoginAt.clone();
        }
        return null;
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
    
    public String getValidateKeyEncodeKey()
    {
        return validateKeyEncodeKey;
    }
    
    public void setValidateKeyEncodeKey(String validateKeyEncodeKey)
    {
        this.validateKeyEncodeKey = validateKeyEncodeKey;
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
    
    public String getUserDn()
    {
        return userDn;
    }
    
    public void setUserDn(String userDn)
    {
        this.userDn = userDn;
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
            user.setLdapStatus(enterpriseUser.getLdapStatus());
            user.setUserSource(enterpriseUser.getUserSource());
            user.setSalt(enterpriseUser.getSalt());
            user.setIterations(enterpriseUser.getIterations());
            user.setRegionId(enterpriseUser.getRegionId());
        }
        return user;
    }
    
    public static EnterpriseUser copyEnterpriseUser(User user)
    {
        EnterpriseUser enterpriseUser = new EnterpriseUser();
        if (null != user)
        {
            enterpriseUser.setId(user.getId());
            enterpriseUser.setPassword(user.getPassword());
            enterpriseUser.setEnterpriseId(user.getEnterpriseId());
            enterpriseUser.setObjectSid(user.getObjectSid());
            enterpriseUser.setName(user.getLoginName());
            enterpriseUser.setAlias(user.getName());
            enterpriseUser.setDescription(user.getDescription());
            enterpriseUser.setEmail(user.getEmail());
            enterpriseUser.setModifiedAt(user.getModifiedAt());
            enterpriseUser.setCreatedAt(user.getCreatedAt());
            enterpriseUser.setStatus(Byte.parseByte(user.getStatus()));
            enterpriseUser.setLdapStatus(user.getLdapStatus());
            enterpriseUser.setUserSource(user.getUserSource());
            enterpriseUser.setSalt(user.getSalt());
            enterpriseUser.setIterations(user.getIterations());
            enterpriseUser.setRegionId(user.getRegionId());
        }
        return enterpriseUser;
    }
    
    public void setRegionId(int regionId)
    {
        this.regionId = regionId;
    }
    
    public int getRegionId()
    {
        return regionId;
    }
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
    }
    
    public byte getLdapTempStatus()
    {
        return ldapTempStatus;
    }
    
    public void setLdapTempStatus(byte ldapTempStatus)
    {
        this.ldapTempStatus = ldapTempStatus;
    }
    
    public Date getResetPasswordAt()
    {
        if (null != resetPasswordAt)
        {
            return (Date) resetPasswordAt.clone();
        }
        return null;
    }
    
    public void setResetPasswordAt(Date resetPasswordAt)
    {
        if (null != resetPasswordAt)
        {
            this.resetPasswordAt = (Date) resetPasswordAt.clone();
        }
        else
        {
            this.resetPasswordAt = null;
        }
    }

    public String getStaffNo() {
		return staffNo;
	}

	public void setStaffNo(String staffNo) {
		this.staffNo = staffNo;
	}

    
	public byte getStaffSecretLevel() {
		return staffSecretLevel;
	}

	public void setStaffSecretLevel(byte staffSecretLevel) {
		this.staffSecretLevel = staffSecretLevel;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getRole() {
		return role;
	}

	public void setRole(byte role) {
		this.role = role;
	}
	
	
}
