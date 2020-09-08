package com.huawei.sharedrive.app.teamspace.dao;

import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

/**
 * 
 */
public interface TeamSpaceMembershipsDAO
{
    /**
     * 添加团队关系记录
     * 
     * @param teamSpaceMemberships
     */
    void create(TeamSpaceMemberships teamSpaceMemberships);
    
    /**
     * 删除团队空间所有成员
     * 
     * @param teamSpaceId
     * @return
     */
    int deleteAll(long teamSpaceId);
    
    /**
     * 删除团队空间指定成员,需要传入定位参数：clouduserid,userid,usertype
     * 
     * @param teamSpaceMemberships
     */
    int delete(TeamSpaceMemberships teamSpaceMemberships);
    
    /**
     * 更新团队空间成员,需要传入定位参数：clouduserid,userid,usertype,其他更新信息
     * 
     * @param teamSpaceMemberships
     */
    int update(TeamSpaceMemberships teamSpaceMemberships);
    
    /**
     * 更新团队空间成员角色,需要传入定位参数：clouduserid,userid,usertype,teamRole
     * 
     * @param teamSpaceMemberships
     */
    int updateTeamSpaceMemberRole(TeamSpaceMemberships teamSpaceMemberships);
    
    /**
     * 
     * 通过用户获取团队关系
     * 
     * @param teamSpaceId
     * @param userId
     * @param type
     * @return
     */
    TeamSpaceMemberships getByUser(long teamSpaceId, String userId, String type);
    
    /**
     * 通过关系ID获取团队关系
     * 
     * @param teamSpaceId
     * @param id
     * @return
     */
    TeamSpaceMemberships getById(long teamSpaceId, long id);
    
    TeamSpaceMemberships getByTeamIdAndUserId(long teamSpaceId, long userId);

    /**
     * 获取团队空间成员总数,teamRole：按照角色过滤，keyword:按照用户名模糊匹配(name%)
     * 
     * @param teamSpaceId
     * @param teamRole
     * @param keyword
     * @return
     */
    long getTeamSpaceMembershipsCount(long teamSpaceId, String teamRole, String keyword);
    
    /**
     * 获取指定用户所属的团队空间总数
     * 
     * @param userId
     * @param type
     * @return
     */
    long getUserTeamSpaceCount(String userId, String type);
    
    /**
     * 获取团队空间成员列表,teamRole：按照角色过滤，keyword:按照用户名模糊匹配(name%)
     * 
     * @param teamSpaceId
     * @param orderList
     * @param limit
     * @param teamRole
     * @return
     */
    List<TeamSpaceMemberships> listTeamSpaceMemberships(long teamSpaceId, List<Order> orderList, Limit limit,
        String teamRole, String keyword);
    
    /**
     * 通过团队空间成员ID 和成员类型 获取团队空间关系
     * 
     * @param userId
     * @param userType
     * @return
     */
    List<TeamSpaceMemberships> getByUserId(String userId, String userType);
    
    /**
     * 获取指定用户所属的团队空间列表：支持分页
     * 
     * @param userId
     * @param type
     * @param orderList
     * @param limit
     * @return
     */
    List<TeamSpaceMemberships> listUserTeamSpaceMemberships(String userId, int type, String userType, List<Order> orderList,
                                                            Limit limit);
    
    /**
     * 获取关系表的最大ID
     * 
     * @param teamSpaceId
     * @return
     */
    long getMaxMembershipsId(long teamSpaceId);
    
    /**
     * 更新用户名
     * 
     * @param userId
     * @param userType
     * @param userName
     * @param newLoginName
     */
    void updateUsername(String userId, String userType, String newUsername, String newLoginName);
    
    /**
     * @param userId
     * @param userType
     * @param orderList
     * @return
     */
    List<Long> listUserTeamSpaceIds(String userId, String userType, List<Order> orderList);
    
    List<TeamSpaceMemberships> getByUserType(long teamSpaceId, String userType);
    
    long getTeamSpaceMembershipsCount(long teamSpaceId);

	TeamSpaceMemberships getTeamSpaceMemberByTeamIdAndRole(Long teamId, String userType, String role);
}
