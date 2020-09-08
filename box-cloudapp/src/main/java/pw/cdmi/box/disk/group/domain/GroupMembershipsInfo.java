package pw.cdmi.box.disk.group.domain;

public class GroupMembershipsInfo
{
    private Long id;
    
    private String groupRole;
    
    private GroupMemberships member;
    
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
    
    public GroupMemberships getMember()
    {
        return member;
    }
    
    public void setMember(GroupMemberships member)
    {
        this.member = member;
    }
    
}
