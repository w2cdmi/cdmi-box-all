package pw.cdmi.box.disk.client.domain.node;

import java.io.Serializable;

public class ThumbnailUrl implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 2998103398812646826L;
    private String thumbnailUrlPath;
    
    public ThumbnailUrl()
    {
        
    }
    
    public ThumbnailUrl(String thumbnailUrlPath)
    {
        this.thumbnailUrlPath = thumbnailUrlPath;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrlPath;
    }
    
    public void setThumbnailUrl(String thumbnailUrlPath)
    {
        this.thumbnailUrlPath = thumbnailUrlPath;
    }
    
}
