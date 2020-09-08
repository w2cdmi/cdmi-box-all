package com.huawei.sharedrive.cloudapp.cmb.user.domain;

import java.io.Serializable;
import java.util.List;

public class ResponseOrgTreeNode implements Serializable
{
    private static final long serialVersionUID = 5017679412660390397L;
    
    private Integer limit;
    
    private Long totalCount;
    
    private Long offset;
    
    private List<OrgTreeNode> orgs;
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public List<OrgTreeNode> getOrgs()
    {
        return orgs;
    }
    
    public void setOrgs(List<OrgTreeNode> orgs)
    {
        this.orgs = orgs;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
}
