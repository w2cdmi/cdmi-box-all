package pw.cdmi.box.disk.client.domain.node;

public class RefreshUploadUrlResponse
{
    private String uploadUrl;
    
    public RefreshUploadUrlResponse()
    {
        
    }

    public RefreshUploadUrlResponse(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
    public String getUploadUrl()
    {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }
    
}
