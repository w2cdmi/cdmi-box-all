package pw.cdmi.box.disk.group.domain;

public class BasicGroupMember
{
 private long groupId;
    
    private long id;
    
    private String loginName;
    
    private String name;
    
    private Long userId;
    
    private String username;
    
    public long getGroupId()
    {
        return groupId;
    }
    
    public long getId()
    {
        return id;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Long getUserId()
    {
        return userId;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setGroupId(long groupId)
    {
        this.groupId = groupId;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
}
