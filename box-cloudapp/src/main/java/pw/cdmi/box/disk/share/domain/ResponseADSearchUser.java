package pw.cdmi.box.disk.share.domain;

import java.util.List;

public class ResponseADSearchUser
{
    
    private List<ADSearchUser> users;
    
    private Integer limit;
    
    private Long offset;
    
    private Long totalCount;
    
    public List<ADSearchUser> getUsers()
    {
        return users;
    }
    
    public void setUsers(List<ADSearchUser> users)
    {
        this.users = users;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
    public Long getOffset()
    {
        return offset;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public Long getTotalCount()
    {
        return totalCount;
    }
    
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }
    
}
