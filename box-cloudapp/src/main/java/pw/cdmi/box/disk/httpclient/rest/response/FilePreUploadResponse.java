package pw.cdmi.box.disk.httpclient.rest.response;

public class FilePreUploadResponse
{
    private Long fileId;
    
    private String uploadUrl;
    
    public Long getFileId()
    {
        return fileId;
    }
    
    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
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
