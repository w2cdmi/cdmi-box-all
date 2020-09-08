package com.huawei.sharedrive.app.teamspace.service;

import java.util.List;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceModifyRequest;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceList;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface TeamSpaceService {
	/**
	 * 
	 * 变更团队空间拥有者
	 * 
	 * @param teamSpaceId
	 * @param newOwnerId
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace changeTeamSpaceOwner(TeamSpace teamSpace, long newOwnerId) throws BaseRunException;

	/**
	 * 判断团队空间是否存在
	 * 
	 * @param teamSpaceId
	 * @throws BaseRunException
	 */
	TeamSpace checkAndGetTeamSpaceExist(long teamSpaceId) throws BaseRunException;

	boolean isTeamSpace(long teamSpaceId);

	/**
	 * 
	 * 创建团队空间
	 * 
	 * @param user
	 * @param TeamSpace
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace createTeamSpace(UserToken user, TeamSpace teamSpace) throws BaseRunException;

	/**
	 * 删除团队空间
	 * 
	 * @param user
	 * @param teamSpaceId
	 * @return
	 * @throws BaseRunException
	 */
	void deleteTeamSpace(UserToken user, long teamSpaceId, String enterpriseId) throws BaseRunException;

	/**
	 * 查询团队空间扩展属性
	 * 
	 * @param name
	 * @param teamSpaceId
	 * @return
	 * @throws BaseRunException
	 */
	List<TeamSpaceAttribute> getTeamSpaceAttrs(User user, String name, long teamSpaceId, String enterpriseId)
			throws BaseRunException;

	/**
	 * 获取团队空间基本信息
	 * 
	 * @param user
	 * @param teamSpaceId
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace getTeamSpaceInfo(UserToken user, long teamSpaceId) throws BaseRunException;

	/**
	 * 获取团队空间,不进行校验
	 * 
	 * @param teamSpaceId
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace getTeamSpaceNoCheck(long teamSpaceId);

	/**
	 * 列举所有团队空间，支持模糊查询
	 * 
	 * @param orderList
	 * @param limit
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpaceList listAllTeamSpaces(List<Order> orderList, Limit limit, TeamSpace filter) throws BaseRunException;

	/**
	 * 列举用户的团队空间， 实际用户
	 * 
	 * @param user
	 * @param orderList
	 * @param limit
	 * @param userId
	 * @param type
	 *@param userType
	 *            未使用  @return
	 * @throws BaseRunException
	 */
	TeamMemberList listUserTeamSpaces(UserToken user, List<Order> orderList, Limit limit, long userId, int type, String userType)
			throws BaseRunException;

	/**
	 * 修改团队空间，包括空间名称和空间描述
	 * 
	 * @param user
	 * @param teamId
	 * @param name
	 * @param description
	 * @param spaceQuota
	 * @param status
	 * @param maxMembers
	 * @param maxVersions
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace modifyTeamSpace(UserToken user, Long teamId, RestTeamSpaceModifyRequest modifyRequest)
			throws BaseRunException;

	/**
	 * 设置团队空间扩展属性
	 * 
	 * @param name
	 * @param value
	 * @throws BaseRunException
	 */
	void setTeamSpaceAttr(User user, TeamSpaceAttribute config, long teamSpaceId, String enterpriseId)
			throws BaseRunException;

	/**
	 * 更改团队空间拥有者
	 * 
	 * @param user
	 * @param teamSpaceId
	 * @param ownerBy
	 * @return
	 * @throws BaseRunException
	 */
	TeamSpace updateOwner(UserToken user, long teamSpaceId, long ownerBy) throws BaseRunException;

	/**
	 * 当用户删除时,更新用户的团队空间,供用户模块调用
	 * 
	 * @param userId
	 * @return
	 * @throws BaseRunException
	 */
	void updateTeamSpacesForUserDelete(long accountId, long userId);

	TeamSpace initTeamspace(RestTeamSpaceCreateRequest createRequest, UserToken userInfo);

	/**
	 * 按名称查询团队空间
	 * @param user
	 * @param name
	 * @return
	 */
	TeamSpace getTeamSpaceByName(UserToken user, String name);

	TeamSpace migrationTeamSpace(TeamSpace teamSpace, long newOwnerId) throws BaseRunException;

	TeamMemberList listTeamSpacesByOwner(UserToken ownerToken, List<Order> order, Limit limitObj, Long userId, int type, String typeUser);
}
