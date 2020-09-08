package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;

public class RestVersionLists
{
    private Integer totalCount;
    
    private List<RestFileVersionInfo> versions;
    
    public RestVersionLists()
    {
        
    }
    
    public RestVersionLists(FileINodesList relist, int clientType)
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
        RestFileVersionInfo temp = null;
        for (INode node : relist.getFiles())
        {
            temp = new RestFileVersionInfo(node, clientType);
            versions.add(temp);
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
