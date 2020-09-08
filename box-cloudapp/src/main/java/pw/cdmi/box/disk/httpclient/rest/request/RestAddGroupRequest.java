package pw.cdmi.box.disk.httpclient.rest.request;

import pw.cdmi.box.disk.core.domain.RestGroupMember;

public class RestAddGroupRequest
{
    private String groupRole;
    
    private RestGroupMember member;
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
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
