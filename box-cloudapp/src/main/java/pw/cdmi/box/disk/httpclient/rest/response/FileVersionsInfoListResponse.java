package pw.cdmi.box.disk.httpclient.rest.response;

import java.io.Serializable;
import java.util.List;

public class FileVersionsInfoListResponse extends BaseResponse implements Serializable
{
    private static final long serialVersionUID = 3280266118894883908L;
    
    private int totalCount;
    
    private List<FileVersionResponse> versions;
    
    public FileVersionsInfoListResponse()
    {
        super();
    }
    
    public FileVersionsInfoListResponse(int totalCount, List<FileVersionResponse> versions)
    {
        super();
        this.totalCount = totalCount;
        this.versions = versions;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public List<FileVersionResponse> getVersions()
    {
        return versions;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setVersions(List<FileVersionResponse> versions)
    {
        this.versions = versions;
    }
    
    @Override
    public String toString()
    {
        return "FileVersionsInfoListResponse [totalCount=" + totalCount + ", versions=" + versions + ']';
    }
    
}
