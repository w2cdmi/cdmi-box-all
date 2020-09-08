package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestUserCreateRequest implements Serializable
{
    private static final long serialVersionUID = 3856611918780631589L;
    
    private String description;
    
    private String email;
    
    private Long fileCount;
    
    private long id;
    
    private String loginName;
    
    private int maxVersions;
    
    private String name;
    
    private Byte regionId;
    
    private Long spaceQuota;
    
    private Long spaceUsed;
    
    private Byte status;
    
    private Long versionFileSize;
    
    
    /** 支持多版本文件的类型 */
    private String versionFileType;
    
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
    
    public int getMaxVersions()
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
    
    public void setMaxVersions(int maxVersions)
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
