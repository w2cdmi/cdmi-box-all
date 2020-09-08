package pw.cdmi.box.disk.group.domain;

import pw.cdmi.box.disk.core.domain.RestGroupMember;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;

public class GroupMembershipsInfoV2
{
    private RestGroupResponse group;
    
    private String groupRole;
    
    private Long id;
    
    private RestGroupMember member;
    
    public RestGroupResponse getGroup()
    {
        return group;
    }
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public RestGroupMember getMember()
    {
        return member;
    }
    
    public void setGroup(RestGroupResponse group)
    {
        this.group = group;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public void setMember(RestGroupMember member)
    {
        this.member = member;
    }
    
}
