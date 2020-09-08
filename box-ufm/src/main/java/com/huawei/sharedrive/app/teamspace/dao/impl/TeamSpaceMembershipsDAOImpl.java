package com.huawei.sharedrive.app.teamspace.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceMembershipsDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.HashTool;
import pw.cdmi.core.utils.SqlUtils;

@Service
@SuppressWarnings("deprecation")
public class TeamSpaceMembershipsDAOImpl extends CacheableSqlMapClientDAO implements TeamSpaceMembershipsDAO
{
    private static final long BASE_NODE_ID = 0;
    
    private static final int TABLE_COUNT = 50;
    
    private static final String TEAM_SAPCE_COUNT = "team_sapce_count_";
    
    /**
     * 获取TEAM ID 的分表
     * 
     * @param teamSpaceMemberships
     * @return
     */
    private int getTableSuffix(TeamSpaceMemberships teamSpaceMemberships)
    {
        long ownerId = teamSpaceMemberships.getCloudUserId();
        if (ownerId <= 0)
        {
            throw new IllegalArgumentException("illegal owner id " + ownerId);
        }
        return getTableSuffix(String.valueOf(ownerId));
    }
    
    private int getTableSuffix(String ownerId)
    {
        int table = (int) (HashTool.apply(ownerId) % TABLE_COUNT);
        return table;
    }
    
    /**
     * 通过用户获取分表
     * 
     * @param teamSpaceMemberships
     * @return
     */
    private int getTableSuffixByUser(TeamSpaceMemberships teamSpaceMemberships)
    {
        String userId = String.valueOf(teamSpaceMemberships.getUserId());
        return getTableSuffix(userId);
    }
    
    @Override
    public void create(TeamSpaceMemberships teamSpaceMemberships)
    {
        // 正向表
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        sqlMapClientTemplate.insert("TeamSpaceMemberships.insert", teamSpaceMemberships);
        
        // 反向表
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        sqlMapClientTemplate.insert("TeamSpaceMemberships.insertToUserTable", teamSpaceMemberships);
        
        if (isCacheSupported())
        {
            getCacheClient().deleteCache(TEAM_SAPCE_COUNT + teamSpaceMemberships.getCloudUserId());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public int deleteAll(long teamSpaceId)
    {
        if (isCacheSupported())
        {
            getCacheClient().deleteCache(TEAM_SAPCE_COUNT + teamSpaceId);
        }
        
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        
        Map<String, Object> map = new HashMap<String, Object>(1);
        
        map.put("filter", teamSpaceMemberships);
        
        List<TeamSpaceMemberships> itemList = sqlMapClientTemplate.queryForList("TeamSpaceMemberships.listTeamSpaceMemberships",
            map);
        
        for (TeamSpaceMemberships item : itemList)
        {
            // 反向表
            item.setTableSuffix(getTableSuffixByUser(item));
            sqlMapClientTemplate.delete("TeamSpaceMemberships.deleteToUserTable", item);
        }
        
        // 正向表
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        return sqlMapClientTemplate.delete("TeamSpaceMemberships.deleteAll", teamSpaceMemberships);
        
    }
    
    @Override
    public int delete(TeamSpaceMemberships teamSpaceMemberships)
    {
        
        if (isCacheSupported())
        {
            getCacheClient().deleteCache(TEAM_SAPCE_COUNT + teamSpaceMemberships.getCloudUserId());
        }
        // 正向表
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        sqlMapClientTemplate.delete("TeamSpaceMemberships.delete", teamSpaceMemberships);
        
        // 反向表
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        return sqlMapClientTemplate.delete("TeamSpaceMemberships.deleteToUserTable", teamSpaceMemberships);
        
    }
    
    @Override
    public int update(TeamSpaceMemberships teamSpaceMemberships)
    {
        // 正向表
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        sqlMapClientTemplate.update("TeamSpaceMemberships.update", teamSpaceMemberships);
        
        // 反向表
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        return sqlMapClientTemplate.update("TeamSpaceMemberships.updateToUserTableByID", teamSpaceMemberships);
        
    }
    
    @Override
    public TeamSpaceMemberships getByUser(long teamSpaceId, String userId, String userType)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        if(StringUtils.isNotEmpty(userId)){
        	teamSpaceMemberships.setUserId(Long.parseLong(userId));
        }else{
        	teamSpaceMemberships.setUserId(0);
        }
        teamSpaceMemberships.setUserType(userType);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        return (TeamSpaceMemberships) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getByUser",
            teamSpaceMemberships);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamSpaceMemberships> getByUserType(long teamSpaceId, String userType)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setUserType(userType);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        return sqlMapClientTemplate.queryForList("TeamSpaceMemberships.getByUserType", teamSpaceMemberships);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamSpaceMemberships> listTeamSpaceMemberships(long cloudUserID, List<Order> orderList,
        Limit limit, String teamRole, String keyword)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(cloudUserID);
        teamSpaceMemberships.setTeamRole(teamRole);
        if (StringUtils.isNotBlank(keyword))
        {
            keyword = SqlUtils.stringToSqlLikeFields(keyword);
            teamSpaceMemberships.setLoginName(keyword);
            teamSpaceMemberships.setUsername(keyword);
        }
        teamSpaceMemberships.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        
        Map<String, Object> map = new HashMap<String, Object>(3);
        
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        
        map.put("filter", teamSpaceMemberships);
        map.put("limit", limit);
        
        return sqlMapClientTemplate.queryForList("TeamSpaceMemberships.listTeamSpaceMemberships", map);
        
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            
            // 解决中文名称排序问题
            if ("userName".equalsIgnoreCase(field))
            {
                field = "convert(userName using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamSpaceMemberships> listUserTeamSpaceMemberships(String userId, int type, String userType, List<Order> orderList, Limit limit)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        if(userId!=null){
            teamSpaceMemberships.setUserId(Long.parseLong(userId));
            teamSpaceMemberships.setUserType(userType);
            teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));

            Map<String, Object> map = new HashMap<String, Object>(3);
            if (CollectionUtils.isNotEmpty(orderList))
            {
                map.put("orderBy", getOrderByStr(orderList));
            }
            map.put("filter", teamSpaceMemberships);
            map.put("limit", limit);
            map.put("type", type);

            return sqlMapClientTemplate.queryForList("TeamSpaceMemberships.listUserTeamSpaceMemberships", map);
        }
        return  null;

    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamSpaceMemberships> getByUserId(String userId, String userType)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setUserId(Long.parseLong(userId));
        teamSpaceMemberships.setUserType(userType);
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        return sqlMapClientTemplate.queryForList("TeamSpaceMemberships.getByUserId", teamSpaceMemberships);
    }
    
    @Override
    public TeamSpaceMemberships getById(long teamSpaceId, long id)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setId(id);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        return (TeamSpaceMemberships) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getByID", teamSpaceMemberships);
    }

    @Override
    public TeamSpaceMemberships getByTeamIdAndUserId(long teamSpaceId, long userId) {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setUserId(userId);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        return (TeamSpaceMemberships) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getByTeamIdAndUserId", teamSpaceMemberships);
    }

    @Override
    public long getTeamSpaceMembershipsCount(long teamSpaceId, String teamRole, String keyword)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setTeamRole(teamRole);
        if (StringUtils.isNotBlank(keyword))
        {
            keyword = SqlUtils.stringToSqlLikeFields(keyword);
            teamSpaceMemberships.setLoginName(keyword);
            teamSpaceMemberships.setUsername(keyword);
        }
        teamSpaceMemberships.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        
        Map<String, Object> map = new HashMap<String, Object>(2);
        
        map.put("filter", teamSpaceMemberships);
        
        return (Long) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getMemberCount", map);
    }
    
    @Override
    public long getTeamSpaceMembershipsCount(long teamSpaceId)
    {
        if (isCacheSupported())
        {
            Long total = (Long) getCacheClient().getCache(TEAM_SAPCE_COUNT + teamSpaceId);
            if (null != total)
            {
                return total;
            }
        }
        
        long count = getTeamSpaceMembershipsCount(teamSpaceId, null, null);
        if (isCacheSupported())
        {
            getCacheClient().setCache(TEAM_SAPCE_COUNT + teamSpaceId, count);
        }
        return count;
    }
    
    @Override
    public int updateTeamSpaceMemberRole(TeamSpaceMemberships teamSpaceMemberships)
    {
        TeamSpaceMemberships ships = getById(teamSpaceMemberships.getCloudUserId(),
            teamSpaceMemberships.getId());
        
        teamSpaceMemberships.setUserId(ships.getUserId());
        teamSpaceMemberships.setUserType(ships.getUserType());
        // 正向表
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        sqlMapClientTemplate.update("TeamSpaceMemberships.updateRole", teamSpaceMemberships);
        
        // 反向表
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        
        return sqlMapClientTemplate.update("TeamSpaceMemberships.updateRoleToUserTable", teamSpaceMemberships);
    }
    
    @Override
    public long getUserTeamSpaceCount(String userId, String type)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setUserId(Long.parseLong(userId));
        teamSpaceMemberships.setUserType(type);
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        return (Long) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getUserTeamSpaceCount",
            teamSpaceMemberships);
    }
    
    @Override
    public long getMaxMembershipsId(long teamSpaceId)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setCloudUserId(teamSpaceId);
        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
        Object maxMembershipsId = sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getMaxMembershipsId",
            teamSpaceMemberships);
        if (maxMembershipsId == null)
        {
            return BASE_NODE_ID;
        }
        return (Long) maxMembershipsId;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void updateUsername(String userId, String userType, String newUserName, String newLoginName)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setUserId(Long.parseLong(userId));
        teamSpaceMemberships.setUserType(userType);
        
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        
        Map<String, Object> map = new HashMap<String, Object>(1);
        
        map.put("filter", teamSpaceMemberships);
        
        List<TeamSpaceMemberships> itemList = sqlMapClientTemplate.queryForList("TeamSpaceMemberships.listUserTeamSpaceMemberships",
            map);
        
        for (TeamSpaceMemberships item : itemList)
        {
            // 正向表
            item.setUsername(newUserName);
            item.setLoginName(newLoginName);
            item.setTableSuffix(getTableSuffix(item));
            sqlMapClientTemplate.update("TeamSpaceMemberships.updateMemberName", item);
        }
        
        // 反向表
        teamSpaceMemberships.setUsername(newUserName);
        teamSpaceMemberships.setLoginName(newLoginName);
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        sqlMapClientTemplate.update("TeamSpaceMemberships.updateUserNameToUserTable", teamSpaceMemberships);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> listUserTeamSpaceIds(String userId, String userType, List<Order> orderList)
    {
        TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
        teamSpaceMemberships.setUserId(Long.parseLong(userId));
        teamSpaceMemberships.setUserType(userType);
        teamSpaceMemberships.setTableSuffix(getTableSuffixByUser(teamSpaceMemberships));
        
        Map<String, Object> map = new HashMap<String, Object>(2);
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        map.put("filter", teamSpaceMemberships);
        
        return sqlMapClientTemplate.queryForList("TeamSpaceMemberships.listUserTeamSpaceIds", map);
    }

	@Override
	public TeamSpaceMemberships getTeamSpaceMemberByTeamIdAndRole(Long teamId, String userType, String role) {
		// TODO Auto-generated method stub
		    TeamSpaceMemberships teamSpaceMemberships = new TeamSpaceMemberships();
	        teamSpaceMemberships.setCloudUserId(teamId);
	        teamSpaceMemberships.setTeamRole(role);
	        teamSpaceMemberships.setUserType(userType);
	        teamSpaceMemberships.setStatus(TeamSpaceMemberships.STATUS_NORMAL);
	        teamSpaceMemberships.setTableSuffix(getTableSuffix(teamSpaceMemberships));
	        
	        Map<String, Object> map = new HashMap<String, Object>(2);
	        
	        map.put("filter", teamSpaceMemberships);
	        
	        return (TeamSpaceMemberships) sqlMapClientTemplate.queryForObject("TeamSpaceMemberships.getTeamSpaceMemberByTeamIdAndRole", map);
	}
    
}
