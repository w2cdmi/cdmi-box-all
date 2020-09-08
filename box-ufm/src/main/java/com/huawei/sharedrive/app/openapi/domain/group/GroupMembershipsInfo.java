package com.huawei.sharedrive.app.openapi.domain.group;


public class GroupMembershipsInfo
{
    private Long id;
    
    private String groupRole;
    
    private RestGroupMember member;
    
    private RestGroup group;
    
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public RestGroup getGroup()
    {
        return group;
    }
    
    public void setGroup(RestGroup group)
    {
        this.group = group;
    }
    
    public RestGroupMember getMember()
    {
        return member;
    }
    
    public void setMember(RestGroupMember member)
    {
        this.member = member;
    }
    
}
