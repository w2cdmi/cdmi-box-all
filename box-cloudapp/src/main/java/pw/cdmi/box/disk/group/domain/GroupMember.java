package pw.cdmi.box.disk.group.domain;

public class GroupMember
{
    private static final String SUPPORTED_USER_TYPE = "user";
    
    private String groupRole;
    
    private Long userId;
    
    private String userType = SUPPORTED_USER_TYPE;
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public String getUserType()
    {
        return userType;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
}
