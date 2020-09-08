package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestEnterpriseAccountResponse implements Serializable
{
    private static final long serialVersionUID = 2296792594867179507L;
    
    private long id;
    
    private String appId;
    
    private Long createdAt;
    
    private Long modifiedAt;
    
    private String status;
    
    private Long enterpriseId;
    
    private int maxSpace;
    
    private int maxMember;
    
    private int maxFiles;
    
    private int maxTeamspace;
    
    private boolean filePreviewable;
    
    private boolean fileScanable;
    
    private Integer currentMembers;
    
    private Integer currentTeamspaces;
    
    private RestAccessKey accessToken;
    
    private long spaceUsed;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
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
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public int getMaxSpace()
    {
        return maxSpace;
    }
    
    public void setMaxSpace(int maxSpace)
    {
        this.maxSpace = maxSpace;
    }
    
    public int getMaxMember()
    {
        return maxMember;
    }
    
    public void setMaxMember(int maxMember)
    {
        this.maxMember = maxMember;
    }
    
    public int getMaxFiles()
    {
        return maxFiles;
    }
    
    public void setMaxFiles(int maxFiles)
    {
        this.maxFiles = maxFiles;
    }
    
    public int getMaxTeamspace()
    {
        return maxTeamspace;
    }
    
    public void setMaxTeamspace(int maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    
    public boolean isFilePreviewable()
    {
        return filePreviewable;
    }
    
    public void setFilePreviewable(boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    
    public boolean isFileScanable()
    {
        return fileScanable;
    }
    
    public void setFileScanable(boolean fileScanable)
    {
        this.fileScanable = fileScanable;
    }
    
    public RestAccessKey getAccessToken()
    {
        return accessToken;
    }
    
    public void setAccessToken(RestAccessKey accessToken)
    {
        this.accessToken = accessToken;
    }

    public Integer getCurrentMembers()
    {
        return currentMembers;
    }

    public void setCurrentMembers(Integer currentMembers)
    {
        this.currentMembers = currentMembers;
    }

    public Integer getCurrentTeamspaces()
    {
        return currentTeamspaces;
    }

    public void setCurrentTeamspaces(Integer currentTeamspaces)
    {
        this.currentTeamspaces = currentTeamspaces;
    }

	public long getSpaceUsed() {
		return spaceUsed;
	}

	public void setSpaceUsed(long spaceUsed) {
		this.spaceUsed = spaceUsed;
	}

}
