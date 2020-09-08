package com.huawei.sharedrive.uam.cmb.retrieve.domain;

import java.io.Serializable;

public class OrgTreeNode implements Serializable
{
    private static final long serialVersionUID = -2506900828089362337L;
    
    private Integer orgId;
    
    private String groupId;
    
    private String name;
    
    private String fatherGroupId;
    
    private String isParent;
    
    public Integer getOrgId()
    {
        return orgId;
    }
    
    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getFatherGroupId()
    {
        return fatherGroupId;
    }
    
    public void setFatherGroupId(String fatherGroupId)
    {
        this.fatherGroupId = fatherGroupId;
    }

    public String getIsParent()
    {
        return isParent;
    }

    public void setIsParent(String isParent)
    {
        this.isParent = isParent;
    }
    
    
}
