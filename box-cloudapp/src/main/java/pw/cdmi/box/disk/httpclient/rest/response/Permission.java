package pw.cdmi.box.disk.httpclient.rest.response;

public class Permission
{
    private boolean canDownload = true;
    
    private boolean canPreview = true;
    
    public boolean isCanDownload()
    {
        return canDownload;
    }
    
    public void setCanDownload(boolean canDownload)
    {
        this.canDownload = canDownload;
    }
    
    public boolean isCanPreview()
    {
        return canPreview;
    }
    
    public void setCanPreview(boolean canPreview)
    {
        this.canPreview = canPreview;
    }
    
}
