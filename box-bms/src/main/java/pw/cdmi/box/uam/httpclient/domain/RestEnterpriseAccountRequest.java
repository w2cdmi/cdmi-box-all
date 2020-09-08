package pw.cdmi.box.uam.httpclient.domain;

import java.io.Serializable;

public class RestEnterpriseAccountRequest implements Serializable
{
    
    private static final long serialVersionUID = 2338309092339999437L;
    
    private String domain;
    
    private long enterpriseId;
    
    private String status;
    
    private long maxSpace;
    
    private int maxMember;
    
    private int maxTeamspace;
    
    private String authAppId;
    
    private Boolean filePreviewable;
    
    private Boolean fileScanable;
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setEnterpriseId(long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setMaxSpace(long maxSpace)
    {
        this.maxSpace = maxSpace;
    }
    
    public long getMaxSpace()
    {
        return maxSpace;
    }
    
    public void setMaxMember(int maxMember)
    {
        this.maxMember = maxMember;
    }
    
    public int getMaxMember()
    {
        return maxMember;
    }
    
    public void setMaxTeamspace(int maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    
    public int getMaxTeamspace()
    {
        return maxTeamspace;
    }
    
    public String getAuthAppId()
    {
        return authAppId;
    }
    
    public void setAuthAppId(String authAppId)
    {
        this.authAppId = authAppId;
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
    
}
