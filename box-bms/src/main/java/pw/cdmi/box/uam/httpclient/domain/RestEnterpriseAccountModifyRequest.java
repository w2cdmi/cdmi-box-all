package pw.cdmi.box.uam.httpclient.domain;

public class RestEnterpriseAccountModifyRequest
{
    
    private String status;
    
    private String domain;
    
    private Long maxSpace;
    
    private Integer maxMember;
    
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
