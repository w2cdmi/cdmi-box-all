package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;

public class FolderListResponse
{
    private Integer totalCount;
    
    private Integer offset;
    
    private Integer limit;
    
    private List<FolderResponse> folders;
    
    private List<FileInfoResponse> files;
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public List<FileInfoResponse> getFiles()
    {
        return files;
    }
    
    public void setFiles(List<FileInfoResponse> files)
    {
        this.files = files;
    }
    
    public List<FolderResponse> getFolders()
    {
        return folders;
    }
    
    public void setFolders(List<FolderResponse> folders)
    {
        this.folders = folders;
    }
    
}
