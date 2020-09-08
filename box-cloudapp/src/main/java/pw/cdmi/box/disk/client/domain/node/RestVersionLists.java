package pw.cdmi.box.disk.client.domain.node;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.files.domain.FileINodesList;

public class RestVersionLists
{
    private Integer totalCount;
    
    private List<RestFileVersionInfo> versions;
    
    public RestVersionLists()
    {
        
    }
    
    public RestVersionLists(FileINodesList relist)
    {
        if (null == relist || null == relist.getFiles())
        {
            return;
        }
        this.setTotalCount(relist.getTotalCount());
        if (null == versions)
        {
            versions = new ArrayList<RestFileVersionInfo>(relist.getFiles().size());
        }
        RestFileVersionInfo info = null;
        for (INode node : relist.getFiles())
        {
            info = new RestFileVersionInfo(node);
            versions.add(info);
        }
    }
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public List<RestFileVersionInfo> getVersions()
    {
        return versions;
    }
    
    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setVersions(List<RestFileVersionInfo> versions)
    {
        this.versions = versions;
    }
    
}
