package pw.cdmi.box.disk.group.domain;

public class GroupMemberships extends BasicGroupMember
{
    
    private String groupRole;
    
    private String userType;

    public String getGroupRole()
    {
        return groupRole;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
    
    
}
