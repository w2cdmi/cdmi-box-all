package pw.cdmi.box.disk.share.domain;

import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;

public class LinkAndNodeV2
{
    
    private INodeLink link;
    
    private RestFileInfoV2 file;
    
    private RestFolderInfo folder;
    
    public INodeLink getLink()
    {
        return link;
    }
    
    public void setLink(INodeLink link)
    {
        this.link = link;
    }
    
    public RestFileInfoV2 getFile()
    {
        return file;
    }
    
    public void setFile(RestFileInfoV2 file)
    {
        this.file = file;
    }
    
    public RestFolderInfo getFolder()
    {
        return folder;
    }
    
    public void setFolder(RestFolderInfo folder)
    {
        this.folder = folder;
    }
    
}
