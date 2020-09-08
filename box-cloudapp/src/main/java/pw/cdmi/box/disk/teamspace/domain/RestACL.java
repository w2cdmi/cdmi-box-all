package pw.cdmi.box.disk.teamspace.domain;

public class RestACL
{
    private int authorize;
    
    private int browse;
    
    private int delete;
    
    private int download;
    
    private int edit;
    
    private int preview;
    
    private int publishLink;
    
    private int upload;
    
    public int getAuthorize()
    {
        return authorize;
    }
    
    public int getBrowse()
    {
        return browse;
    }
    
    public int getDelete()
    {
        return delete;
    }
    
    public int getDownload()
    {
        return download;
    }
    
    public int getEdit()
    {
        return edit;
    }
    
    public int getPreview()
    {
        return preview;
    }
    
    public int getPublishLink()
    {
        return publishLink;
    }
    
    public int getUpload()
    {
        return upload;
    }
    
    public void setAuthorize(int authorize)
    {
        this.authorize = authorize;
    }
    
    public void setBrowse(int browse)
    {
        this.browse = browse;
    }
    
    public void setDelete(int delete)
    {
        this.delete = delete;
    }
    
    public void setDownload(int download)
    {
        this.download = download;
    }
    
    public void setEdit(int edit)
    {
        this.edit = edit;
    }
    
    public void setPreview(int preview)
    {
        this.preview = preview;
    }
    
    public void setPublishLink(int publishLink)
    {
        this.publishLink = publishLink;
    }
    
    public void setUpload(int upload)
    {
        this.upload = upload;
    }
    
}