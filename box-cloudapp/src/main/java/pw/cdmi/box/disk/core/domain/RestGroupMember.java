package pw.cdmi.box.disk.core.domain;

import pw.cdmi.box.disk.group.domain.BasicGroupMember;

public class RestGroupMember extends BasicGroupMember
{
    
    private String description;

    private String groupRole;
    
    private String userType;

    public String getDescription()
    {
        return description;
    }

    public String getGroupRole()
    {
        return groupRole;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
   
    
}
