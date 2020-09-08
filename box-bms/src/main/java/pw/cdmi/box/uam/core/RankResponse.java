package pw.cdmi.box.uam.core;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.uam.user.domain.TeamSpace;
import pw.cdmi.box.uam.user.domain.User;

/**
 * 
 * 用于封装UFM API Controller 服务端返回的数据
 * 
 */
public class RankResponse implements Serializable
{
    
    private static final long serialVersionUID = -2688265771364586455L;
    
    /** 分页参数：当前页数量 */
    private int limit;
    
    /** 分页参数：偏移量 */
    private long offset;
    
    private List<TeamSpace> teamSpace;
    
    /** 总数据量 */
    private int totalCount;
    
    private List<User> users;
    
    public int getLimit()
    {
        return limit;
    }
    
    public long getOffset()
    {
        return offset;
    }
    
    public List<TeamSpace> getTeamSpace()
    {
        return teamSpace;
    }
    
    public int getTotalCount()
    {
        return totalCount;
    }
    
    public List<User> getUsers()
    {
        return users;
    }
    
    public void setLimit(int limit)
    {
        this.limit = limit;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    public void setTeamSpace(List<TeamSpace> teamSpace)
    {
        this.teamSpace = teamSpace;
    }
    
    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }
    
    public void setUsers(List<User> users)
    {
        this.users = users;
    }
    
}
