package com.huawei.sharedrive.isystem.account.domain;

import java.util.Date;

public class Account
{
    private Long id;
    
    private String appId;
    
    private String name;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Byte status;
    
    private Long enterpriseId;
    
    private Long maxSpace;
    
    private Integer maxMember;
    
    private Long maxFiles;
    
    private Integer maxTeamspaces;
    
    private Boolean filePreviewable;
    
    private Boolean fileScanable;
 
    private Integer currentMember;     
    
    private Integer currentTeamspace; 
 
    public Integer getCurrentMember()
    {
        return currentMember;
    }

    public Integer getCurrentTeamspace()
    {
        return currentTeamspace;
    } 
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Date getCreateAt()
    {
        if (this.createdAt != null)
        {
            return new Date(this.createdAt.getTime());
        }
        return null;
    }
    
    public void setCreateAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public Date getModifiedAt()
    {
        if (this.modifiedAt != null)
        {
            return new Date(this.modifiedAt.getTime());
        }
        return null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if(modifiedAt != null)
        {
            this.modifiedAt = new Date(modifiedAt.getTime());
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
    
    public Long getMaxSpace()
    {
        return maxSpace;
    }
    
    public void setMaxSpace(Long maxSpace)
    {
        this.maxSpace = maxSpace;
    }
    
    public Integer getMaxMember()
    {
        return maxMember;
    }
    
    public void setMaxMember(Integer maxMember)
    {
        this.maxMember = maxMember;
    }
    
    public Long getMaxFiles()
    {
        return maxFiles;
    }
    
    public void setMaxFiles(Long maxFiles)
    {
        this.maxFiles = maxFiles;
    }
    
    public Integer getMaxTeamspaces()
    {
        return maxTeamspaces;
    }
    
    public void setMaxTeamspaces(Integer maxTeamspaces)
    {
        this.maxTeamspaces = maxTeamspaces;
    }
    
    public Boolean getFilePreviewable()
    {
        return filePreviewable;
    }
    
    public void setFilePreviewable(Boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    
    public Boolean getFileScanable()
    {
        return fileScanable;
    }
    
    public void setFileScanable(Boolean fileScanable)
    {
        this.fileScanable = fileScanable;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    public void setCurrentTeamspace(Integer currentTeamspace)
    {
        this.currentTeamspace = currentTeamspace;
    }
    
    public void setCurrentMember(Integer currentMember)
    {
        this.currentMember = currentMember;
    }
}
