package pw.cdmi.box.disk.client.domain.node;

public class SwfDownloadResponse
{
    private String swfUrl;
    
    public SwfDownloadResponse()
    {
        
    }

    public SwfDownloadResponse(String swfUrl)
    {
        this.swfUrl = swfUrl;
    }
    
    public String getSwfUrl()
    {
        return swfUrl;
    }
    public void setSwfUrl(String swfUrl)
    {
        this.swfUrl = swfUrl;
    }
    
    
}
