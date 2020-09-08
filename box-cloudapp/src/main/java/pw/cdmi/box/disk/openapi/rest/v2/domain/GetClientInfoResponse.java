package pw.cdmi.box.disk.openapi.rest.v2.domain;

public class GetClientInfoResponse
{
    private String downloadUrl;
    
    private String versionInfo;
    
    public String getDownloadUrl()
    {
        return downloadUrl;
    }
    
    public String getVersionInfo()
    {
        return versionInfo;
    }
    
    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }
    
    public void setVersionInfo(String versionInfo)
    {
        this.versionInfo = versionInfo;
    }
    
}
