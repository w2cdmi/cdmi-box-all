package pw.cdmi.box.disk.client.domain.user;

import java.util.List;

public class RestUserList
{
    private Integer limit;
    
    private Integer offset;
    
    private String prefix;
    
    private Integer totalCount;
    
    private List<RestUserInfo> users;
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public Integer getTotalCount()
    {
        return totalCount;
    }
    
    public List<RestUserInfo> getUsers()
    {
        return users;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
    public void setTotalCount(Integer totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUsers(List<RestUserInfo> users)
    {
        this.users = users;
    }
    
}
