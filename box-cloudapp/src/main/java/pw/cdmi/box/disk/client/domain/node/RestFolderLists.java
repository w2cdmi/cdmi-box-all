package pw.cdmi.box.disk.client.domain.node;

import java.util.List;

import pw.cdmi.box.disk.client.domain.common.PageResult;

public class RestFolderLists extends PageResult
{
    public RestFolderLists()
    {
        
    }
    
    public RestFolderLists(int limit, int offset, int totalCount)
    {
        super(limit, offset, totalCount);
    }

    private List<RestFileInfo> files;
    
    private List<RestFolderInfo> folders;
    
    
    public List<RestFileInfo> getFiles()
    {
        return files;
    }
    
    public List<RestFolderInfo> getFolders()
    {
        return folders;
    }
    
    public void setFiles(List<RestFileInfo> files)
    {
        this.files = files;
    }
    
    public void setFolders(List<RestFolderInfo> folders)
    {
        this.folders = folders;
    }
    
    public void transType()
    {
        if(folders != null)
        {
            for (RestFolderInfo item : folders)
            {
                item.transType();
            }
        }
    }
}
