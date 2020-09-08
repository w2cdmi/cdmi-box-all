package pw.cdmi.box.uam.httpclient.domain;

public class RestACL
{
    private int browse;
    
    private int preview;
    
    private int download;
    
    private int upload;
    
    private int edit;
    
    private int delete;
    
    private int publishLink;
    
    private int authorize;
    
    public int getBrowse()
    {
        return browse;
    }
    
    public void setBrowse(int browse)
    {
        this.browse = browse;
    }
    
    public int getPreview()
    {
        return preview;
    }
    
    public void setPreview(int preview)
    {
        this.preview = preview;
    }
    
    public int getDownload()
    {
        return download;
    }
    
    public void setDownload(int download)
    {
        this.download = download;
    }
    
    public int getUpload()
    {
        return upload;
    }
    
    public void setUpload(int upload)
    {
        this.upload = upload;
    }
    
    public int getEdit()
    {
        return edit;
    }
    
    public void setEdit(int edit)
    {
        this.edit = edit;
    }
    
    public int getDelete()
    {
        return delete;
    }
    
    public void setDelete(int delete)
    {
        this.delete = delete;
    }
    
    public int getPublishLink()
    {
        return publishLink;
    }
    
    public void setPublishLink(int publishLink)
    {
        this.publishLink = publishLink;
    }
    
    public int getAuthorize()
    {
        return authorize;
    }
    
    public void setAuthorize(int authorize)
    {
        this.authorize = authorize;
    }
    
}