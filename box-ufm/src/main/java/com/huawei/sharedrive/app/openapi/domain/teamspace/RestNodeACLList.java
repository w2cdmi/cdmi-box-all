package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.acl.domain.INodeACLList;

public class RestNodeACLList
{
    private List<RestNodeACLInfo> acls;
    
    private int limit;
    
    private long offset;
    
    private long totalCount;
    
    public RestNodeACLList()
    {
        
    }
    
    public RestNodeACLList(INodeACLList iNodeACLList)
    {
        this.setAcls(iNodeACLList.getNodeACLs());
        this.limit = iNodeACLList.getLimit();
        this.offset = iNodeACLList.getOffset();
        this.totalCount = iNodeACLList.getTotalCount();
    }
    
    public List<RestNodeACLInfo> getAcls()
    {
        return acls;
    }
    
    public void setAcls(List<INodeACL> aclsList)
    {
        if (null == aclsList)
        {
            return;
        }
        
        this.acls = new ArrayList<RestNodeACLInfo>(aclsList.size());
        RestNodeACLInfo temp = null;
        for (INodeACL iNodeACL : aclsList)
        {
            temp = new RestNodeACLInfo(iNodeACL);
            this.acls.add(temp);
        }
    }
    
    public int getLimit()
    {
        return limit;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
