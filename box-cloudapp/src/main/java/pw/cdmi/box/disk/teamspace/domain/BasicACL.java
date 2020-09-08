package pw.cdmi.box.disk.teamspace.domain;

public class BasicACL
{
    private long id;

    private String loginName;

    private String role;

    private Long userId;

    private String username;

    private String userType;

    public long getId()
    {
        return id;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public String getRole()
    {
        return role;
    }

    public Long getUserId()
    {
        return userId;
    }

    public String getUsername()
    {
        return username;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
}
