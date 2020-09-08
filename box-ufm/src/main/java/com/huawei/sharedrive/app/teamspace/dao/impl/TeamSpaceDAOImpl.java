package com.huawei.sharedrive.app.teamspace.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.utils.OrderCommon;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.SqlUtils;

@Service("teamSpaceDAO")
@SuppressWarnings("deprecation")
public class TeamSpaceDAOImpl extends CacheableSqlMapClientDAO implements TeamSpaceDAO
{
    @Override
    public int changeOwner(TeamSpace teamspace)
    {
        int result = sqlMapClientTemplate.update("TeamSpace.changeOwner", teamspace);
        String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamspace.getCloudUserId();
        deleteCacheAfterCommit(key);
        return result;
    }
    
    @Override
    public void create(TeamSpace teamspace)
    {
        sqlMapClientTemplate.insert("TeamSpace.insert", teamspace);
    }
    
    @Override
    public int delete(long teamSpaceId)
    {
        int result = sqlMapClientTemplate.delete("TeamSpace.delete", teamSpaceId);
        String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamSpaceId;
        deleteCacheAfterCommit(key);
        return result;
    }
    
    @Override
    public TeamSpace get(long teamSpaceId)
    {
        if (isCacheSupported())
        {
            String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamSpaceId;
            TeamSpace teamspace = (TeamSpace) getCacheClient().getCache(key);
            if (teamspace != null)
            {
                return teamspace;
            }
            teamspace = (TeamSpace) sqlMapClientTemplate.queryForObject("TeamSpace.get", teamSpaceId);
            if (teamspace == null)
            {
                return null;
            }
            getCacheClient().setCache(key, teamspace);
            return teamspace;
        }
        return (TeamSpace) sqlMapClientTemplate.queryForObject("TeamSpace.get", teamSpaceId);
    }
    
    @Override
    public int getTeamSpaceCount(TeamSpace filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        
        map.put("filter", filter);
        
        if (filter != null && StringUtils.isNotBlank(filter.getName()))
        {
            filter.setName(SqlUtils.stringToSqlLikeFields(filter.getName()));
        }
        
        return (Integer) sqlMapClientTemplate.queryForObject("TeamSpace.getFilterdCount", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamSpace> listTeamSpaces(List<Order> orderList, Limit limit, TeamSpace filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        
        if (CollectionUtils.isNotEmpty(orderList))
        {
            map.put("orderBy", getOrderByStr(orderList));
        }
        
        map.put("limit", limit);
        map.put("filter", filter);
        
        return sqlMapClientTemplate.queryForList("TeamSpace.getFilterd", map);
    }
    
    @Override
    public int update(TeamSpace teamspace)
    {
        int result = sqlMapClientTemplate.update("TeamSpace.update", teamspace);
        String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamspace.getCloudUserId();
        deleteCacheAfterCommit(key);
        return result;
    }
    
    @Override
    public int updateStatus(long teamSpaceId, int status)
    {
        TeamSpace teamspace = new TeamSpace();
        teamspace.setCloudUserId(teamSpaceId);
        teamspace.setStatus(status);
        int result = sqlMapClientTemplate.update("TeamSpace.updateStatus", teamspace);
        String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamSpaceId;
        deleteCacheAfterCommit(key);
        return result;
    }
    
    @Override
    public int updateUploadNotice(long teamSpaceId, byte uploadNotice)
    {
        TeamSpace teamspace = new TeamSpace();
        teamspace.setCloudUserId(teamSpaceId);
        teamspace.setUploadNotice(uploadNotice);
        int result = sqlMapClientTemplate.update("TeamSpace.updateUploadNotice", teamspace);
        String key = TeamSpace.CACHE_KEY_PREFIX_ID + teamSpaceId;
        deleteCacheAfterCommit(key);
        return result;
    }
    
    private String getOrderByStr(List<Order> orderList)
    {
        return OrderCommon.getOrderByStr(orderList);
    }

	@Override
	public TeamSpace getByName(String name,long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("name", name);
		map.put("accountId", accountId);
		return (TeamSpace) sqlMapClientTemplate.queryForObject("TeamSpace.getByName", map);
	}

	@Override
	public TeamSpace getByType(long cloudUserId, int type) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("type", type);
		map.put("cloudUserId", cloudUserId);
		return (TeamSpace) sqlMapClientTemplate.queryForObject("TeamSpace.getByType", map);
	}
}
