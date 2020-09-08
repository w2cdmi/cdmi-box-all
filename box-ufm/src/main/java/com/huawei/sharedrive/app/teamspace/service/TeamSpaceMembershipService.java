package com.huawei.sharedrive.app.teamspace.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface TeamSpaceMembershipService {
	/**
	 * 检查团队空间成员是否存在
	 * 
	 * @param teamSpaceId
	 * @param userId
	 * @param type
	 * @return
	 * @throws BaseRunException
	 */
	boolean checkTeamSpaceMemberExist(long teamSpaceId, String userId, String type) throws BaseRunException;

	/**
	 * 添加空间成员
	 * 
	 * @param teamSpaceMemberships
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships createTeamSpaceMember(TeamSpaceMemberships teamSpaceMemberships) throws BaseRunException;

	/**
	 * 通过关系ID删除指定的空间成员
	 * 
	 * @param teamSpaceId
	 * @param id
	 * @throws BaseRunException
	 */
	void deleteTeamSpaceMemberById(long teamSpaceId, long id) throws BaseRunException;

	void deleteTeamSpaceMemberByTeamIdAndUserId(long teamSpaceId, long mem) throws BaseRunException;

	/**
	 * 根据空间ID 用户ID 用户类型删除成员
	 * 
	 * @param teamSpaceId
	 * @param userId
	 * @param userType
	 */
	void deleteTeamSpaceMember(long teamSpaceId, String userId, String userType);

	/**
	 * 删除空间所有成员关系
	 * 
	 * @param teamSpaceId
	 * @throws BaseRunException
	 */
	void deleteAllTeamSpaceMember(long teamSpaceId) throws BaseRunException;

	/**
	 * 通过关系ID获取指定团队空间成员
	 * 
	 * @param teamSpaceId
	 * @param id
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships getTeamSpaceMemberById(long teamSpaceId, long id) throws BaseRunException;

	/**
	 * 通过空间和成员ID获取
	 *
	 * @param teamSpaceId
	 * @param memberId
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships getByTeamIdAndUserId(long teamSpaceId, long userId) throws BaseRunException;

	/**
	 * 获取指定团队空间指定用户的成员信息
	 * 
	 * @param teamSpaceId
	 * @param userId
	 * @param type
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships getTeamSpaceMemberByUser(long teamSpaceId, String userId, String type) throws BaseRunException;

	/**
	 * 获取指定团队空间指定用户的成员信息，不做校验
	 * 
	 * @param teamSpaceId
	 * @param userId
	 * @param type
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships getTeamSpaceMemberByUserNoCheck(long teamSpaceId, String userId, String type)
			throws BaseRunException;

	/**
	 * 判断是否团队空间成员，否则返回权限异常
	 * 
	 * @param teamSpaceId
	 * @param userId
	 * @param type
	 *            未使用
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships getUserMemberShips(long teamSpaceId, long userId,String enterpriseId) throws BaseRunException;

	/**
	 * 获取团队空间成员总数
	 * 
	 * @param teamSpaceId
	 * @return
	 * @throws BaseRunException
	 */
	long getTeamSpaceMembersCount(long teamSpaceId) throws BaseRunException;

	/**
	 * 获取指定用户所属的团队空间总数
	 * 
	 * @param userId
	 * @param type
	 * @return
	 * @throws BaseRunException
	 */
	long getUserTeamSpaceCount(String userId, String type) throws BaseRunException;

	/**
	 * 获取团队空间成员列表,teamRole：按照角色过滤，keyword:按照用户名模糊匹配(name%)
	 * 
	 * @param teamSpaceId
	 * @param orderList
	 * @param limit
	 * @param teamRole
	 * @param keyword
	 * @return
	 * @throws BaseRunException
	 */
	TeamMemberList listTeamSpaceMemberships(long teamSpaceId, List<Order> orderList, Limit limit, String teamRole,
			String keyword) throws BaseRunException;

	/**
	 * 根据用户ID用户类型获取用户关系
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
	 * @throws BaseRunException
	 */
	TeamMemberList listUserTeamSpaceMemberships(String userId, String type, List<Order> orderList, Limit limit)
			throws BaseRunException;

	TeamMemberList listUserTeamSpaceMemberships(UserToken userToken, String userId, int type, String userType, List<Order> orderList,
												Limit limit) throws BaseRunException;

	/**
	 * 更新团队空间成员角色,需要传入定位参数：clouduserid,userid,usertype,teamRole
	 * 
	 * @param teamSpaceMemberships
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceMemberships modifyTeamSpaceMemberRole(TeamSpaceMemberships teamSpaceMemberships) throws BaseRunException;

	/**
	 * 更新用户名，供外部调用,支持单用户及群组名更新
	 * 
	 * @param userId
	 * @param usertype
	 * @param userName
	 */
	void updateUsername(User user, String usertype);

	TeamSpaceMemberships getTeamSpaceMemberByTeamIdAndRole(Long teamId, String userType, String role);

	long countTeamSpaceMemberships(long teamSpaceId, List<Order> orderList, Limit limit, String teamRole, String keyword);

	TeamMemberList listTeamSpacesByOwner(UserToken user, String valueOf, int type, String userType, List<Order> orderList, Limit limit);
}
