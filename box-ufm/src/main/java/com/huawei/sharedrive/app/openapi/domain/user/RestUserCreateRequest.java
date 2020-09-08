package com.huawei.sharedrive.app.openapi.domain.user;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PatternRegUtil;

public class RestUserCreateRequest implements Serializable
{
    private static final long serialVersionUID = 3856611918780631589L;
    
    private String description;
    
    private String email;
    
    private Long fileCount;
    
    private long id;
    
    private String loginName;
    
    // 文件最大历史版本数
    private Integer maxVersions;
    
    private String name;
    
    private Byte regionId;
    
    private Long spaceQuota;
    
    private Long spaceUsed;
    
    private Byte status;
    
    private Date createdAt;
    
    /** 支持多版本文件的大小 */
    private Long versionFileSize;
    
    
    /** 支持多版本文件的类型 */
    private String versionFileType;
    
    
    public void copyFrom(User user)
    {
        this.setId(user.getId());
        this.setLoginName(user.getLoginName());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setSpaceQuota(user.getSpaceQuota());
        this.setSpaceUsed(user.getSpaceUsed());
        this.setStatus(Byte.parseByte(user.getStatus()));
        this.setRegionId((byte) user.getRegionId());
        this.setFileCount(user.getFileCount());
        this.setVersionFileSize(user.getVersionFileSize());
        this.setVersionFileType(user.getVersionFileType());
      
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public String getDescription()
    {
        return description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public Long getFileCount()
    {
        return fileCount;
    }
    
    public long getId()
    {
        return id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Byte getRegionId()
    {
        return regionId;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setFileCount(Long fileCount)
    {
        this.fileCount = fileCount;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRegionId(Byte regionId)
    {
        this.regionId = regionId;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public void checkUserAddParamter() throws InvalidParamException
    {
        String loginName = this.getLoginName();
        String name = this.getName();
        // 登录名、用户名参数校验
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(name))
        {
            throw new InvalidParamException();
        }
        if (loginName.length() > User.LOGIN_NAME_LENGTH || name.length() > User.USER_NAME_LENGTH)
        {
            throw new InvalidParamException();
        }
        // 邮箱参数校验
        if (StringUtils.isNotBlank(this.getEmail()) && this.getEmail().length() > 255)
        {
            throw new InvalidParamException();
        }
        if (StringUtils.isNotBlank(this.getEmail()))
        {
            PatternRegUtil.checkMailLegal(this.getEmail());
        }
        // 状态参数校验
        if (this.getStatus() != null && this.getStatus() != 0 && this.getStatus() != 1)
        {
            throw new InvalidParamException();
        }
        
        // 文件最大版本数校验
        Integer maxVersions = this.getMaxVersions();
        if (maxVersions != null && (maxVersions <= 0 && maxVersions != User.VERSION_NUM_UNLIMITED))
        {
            throw new InvalidParamException("Invalid max versions: " + maxVersions);
        }
        if (this.getSpaceQuota() != null && this.getSpaceQuota() != -1)
        {
            FilesCommonUtils.checkNonNegativeIntegers(this.getSpaceQuota());
        }
    }
    
    public void setDefaultValue()
    {
        if (this.getSpaceQuota() == null)
        {
            this.setSpaceQuota(-1L);
        }
        if (this.getRegionId() == null)
        {
            this.setRegionId((byte) -1);
        }
        if (this.getStatus() == null)
        {
            this.setStatus((byte) 0);
        }
    }
    
    public void transUserFromCreate(User user)
    {
        if (this.getEmail() != null)
        {
            user.setEmail(this.getEmail());
        }
        if (this.getLoginName() != null)
        {
            user.setLoginName(this.getLoginName());
        }
        if (this.getName() != null)
        {
            user.setName(this.getName());
        }
        if (this.getSpaceQuota() != null)
        {
            user.setSpaceQuota(this.getSpaceQuota());
        }
        if (this.getStatus() != null)
        {
            user.setStatus(String.valueOf(this.getStatus()));
        }
        if (this.getRegionId() != null)
        {
            user.setRegionId(this.getRegionId());
        }
        if (this.getDescription() != null)
        {
            if (this.getDescription().length() > 255)
            {
                throw new InvalidParamException("Deparetment length:" + this.getDescription().length());
            }
//            user.setDepartment(this.getDescription());
        }
        if (this.getMaxVersions() != null)
        {
            user.setMaxVersions(this.getMaxVersions());
        }
        
        if (this.getVersionFileSize() != null)
        {
            user.setVersionFileSize(this.getVersionFileSize());
        }
        if (this.getVersionFileType() != null)
        {
            user.setVersionFileType(this.getVersionFileType());
        }
//        if (this.getDepartmentId()!=0)
//        {
//            user.setDepartment(String.valueOf(this.getDepartmentId()));
//        }
    }

	public Long getVersionFileSize() {
		return versionFileSize;
	}

	public void setVersionFileSize(Long versionFileSize) {
		this.versionFileSize = versionFileSize;
	}

	public String getVersionFileType() {
		return versionFileType;
	}

	public void setVersionFileType(String versionFileType) {
		this.versionFileType = versionFileType;
	}
    
}
