/**
 * 
 */
package pw.cdmi.box.disk.client.domain.node;

public class PreviewDownloadResponse
{
    
    private String url;
    
    public PreviewDownloadResponse()
    {
        
    }
    
    public PreviewDownloadResponse(String url)
    {
        this.url = url;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
}
