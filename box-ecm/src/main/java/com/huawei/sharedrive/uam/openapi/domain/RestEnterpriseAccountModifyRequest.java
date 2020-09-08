package com.huawei.sharedrive.uam.openapi.domain;

public class RestEnterpriseAccountModifyRequest
{
    
    private String status;
    
    private String domain;
    
    private Long enterpriseId;
    
    private Integer maxSpace;
    
    private Integer maxMember;
    
    private Integer maxFiles;
    
    private Integer maxTeamspace;
    
    private Boolean filePreviewable;
    
    private Boolean fileScanable;
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public Integer getMaxSpace()
    {
        return maxSpace;
    }
    
    public void setMaxSpace(Integer maxSpace)
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
    
    public Integer getMaxFiles()
    {
        return maxFiles;
    }
    
    public void setMaxFiles(Integer maxFiles)
    {
        this.maxFiles = maxFiles;
    }
    
    public Integer getMaxTeamspace()
    {
        return maxTeamspace;
    }
    
    public void setMaxTeamspace(Integer maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    
    public Boolean isFilePreviewable()
    {
        return filePreviewable;
    }
    
    public void setFilePreviewable(Boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    
    public Boolean isFileScanable()
    {
        return fileScanable;
    }
    
    public void setFileScanable(Boolean fileScanable)
    {
        this.fileScanable = fileScanable;
    }
    
}
