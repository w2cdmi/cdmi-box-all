package pw.cdmi.box.disk.httpclient.rest.response;

import java.util.List;

public class UserListResponse
{
    private Integer totalCount;
    
    private Integer offset;
    
    private Integer limit;
    
    private String prefix;
    
    private String users;
    
    private String entries;
    
    private List<UserInfo> userList;
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
    public String getUsers()
    {
        return users;
    }
    
    public void setUsers(String users)
    {
        this.users = users;
    }
    
    public String getEntries()
    {
        return entries;
    }
    
    public void setEntries(String entries)
    {
        this.entries = entries;
    }
    
    public List<UserInfo> getUserList()
    {
        return userList;
    }
    
    public void setUserList(List<UserInfo> userList)
    {
        this.userList = userList;
    }
    
}
