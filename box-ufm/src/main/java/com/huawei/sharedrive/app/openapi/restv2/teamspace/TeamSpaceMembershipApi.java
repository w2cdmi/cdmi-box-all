package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.teamspace.ListTeamSpaceMemberRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberList;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberModifyRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.*;
import com.huawei.sharedrive.app.teamspace.domain.TeamMemberList;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipManager;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Limit;

/**
 * 团队空间成员对外接口
 *
 * @author t00159390
 */
@Controller
@RequestMapping(value = "/api/v2/teamspaces/{teamId}/memberships")
@Api(description = "团队空间成员对外接口")
public class TeamSpaceMembershipApi extends TeamSpaceCommonApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamSpaceMembershipApi.class);

    @Autowired
    private TeamSpaceMembershipManager teamSpaceMembershipManager;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private TeamSpaceService teamSpaceService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;

	/**
	 * 添加团队空间成员
	 * 
	 * @param createRequest
	 * @param teamId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "添加团队空间成员")
    @ResponseBody
	public void addTeamMember(@RequestBody RestTeamMemberCreateRequest createRequest, @PathVariable Long teamId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		boolean isAccountOper = false;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId);

            createRequest.checkParameter();
            if (token.startsWith(UserTokenHelper.APP_PREFIX) || token.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
                Account account = userTokenHelper.checkAccountToken(token, date);
                userInfo = new UserToken();
                userInfo.setAppId(account.getAppId());
                userInfo.setId(User.APP_USER_ID);
                userInfo.setCloudUserId(UserTokenHelper.ACCOUNT_CLOUD_USER_ID);
                userInfo.setAccountVistor(account);
                isAccountOper = true;
            } else {
                userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, null);
            }

			// TODO 存在事务问题
			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceMembershipManager.createTeamSpaceMember(userInfo, teamId, createRequest, isAccountOper);
		} catch (RuntimeException t) {
			String loginName = null;
			if (userInfo != null) {
				loginName = userInfo.getLoginName();
			}
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.ADD_TEAMSPACE_MEMBER_ERR, teamId, loginName, StringUtils.trimToEmpty(createRequest.getRole()));
			throw t;
		}
	}

	/**
	 * 删除团队空间成员
	 * 
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/user/{cloudUserId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除团队空间成员")
    @ResponseBody
	public void deleteManager(@PathVariable long teamId, @PathVariable long cloudUserId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId, cloudUserId);

			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceMembershipManager.deleteMemberByUserId(userInfo, teamId, cloudUserId);
		} catch (RuntimeException t) {
			LOGGER.warn("exception", t);
			handleException(userInfo, teamSpace);
			throw t;
		}
	}

	private void handleException(UserToken userInfo, TeamSpace teamSpace) {
		if (userInfo != null) {
			TeamSpaceMemberships teamSpaceMemberships = null;
			try {
				String enterpriseId = "";
				if (userInfo.getAccountVistor() != null) {
					enterpriseId = String.valueOf(userInfo.getAccountVistor().getEnterpriseId());
				}
				teamSpaceMemberships = teamSpaceMembershipService.getUserMemberShips(teamSpace.getCloudUserId(), userInfo.getId(), enterpriseId);
			} catch (ForbiddenException e) {
				LOGGER.warn("forbidden exception");
			}
			String userLoginName = null;
			if (teamSpaceMemberships != null) {
				userLoginName = getUserLoginName(String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType());
			}
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.DELETE_TEAMSPACE_MEMBER_ERR, teamSpace.getCloudUserId(), userLoginName, teamSpaceMemberships != null ? teamSpaceMemberships.getTeamRole() : null);
		}
	}

	/**
	 * 删除团队空间成员
	 *
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/{teamMemberId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteTeamSpaceMember(@PathVariable Long teamId, @PathVariable Long teamMemberId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId, teamMemberId);

			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceMembershipManager.deleteTeamSpaceMemberById(userInfo, teamId, teamMemberId);
		} catch (RuntimeException t) {
			LOGGER.warn("exception", t);
			handleException(userInfo, teamSpace);
			throw t;
		}
	}

	/**
	 * 删除团队空间成员
	 *
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/userId/{cloudUserId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteMemberByCloudUserId(@PathVariable Long teamId, @PathVariable Long cloudUserId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId, cloudUserId);

			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceMembershipManager.deleteMemberByUserId(userInfo, teamId, cloudUserId);
		} catch (RuntimeException t) {
			LOGGER.warn("exception", t);
			handleException(userInfo, teamSpace);
			throw t;
		}
	}

	/**
	 * 删除团队空间成员
	 *
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	@ResponseBody
	public void batchDeleteTeamSpaceMember(@PathVariable Long teamId, @RequestBody Long[] teamMemberIds, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		try {
			// FilesCommonUtils.checkNonNegativeIntegers(teamId, teamMemberId);

			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在
			teamSpaceMembershipManager.batchDeleteTeamSpaceMemberById(userInfo, teamId, teamMemberIds);

		} catch (RuntimeException t) {
			LOGGER.warn("exception", t);
			handleException(userInfo, teamSpace);
			throw t;
		}
	}

	/**
	 * 获取团队成员信息
	 * 
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/{teamMemberId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取团队成员信息")
    @ResponseBody
	public ResponseEntity<RestTeamMemberInfo> getTeamMemberInfo(@PathVariable Long teamId, @PathVariable Long teamMemberId, @RequestHeader("Authorization") String token) throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId, teamMemberId);

			userInfo = userTokenHelper.checkTokenAndGetUserForV2(token, null);
			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipManager.getTeamSpaceMemberById(userInfo, teamId, teamMemberId);

			return new ResponseEntity<RestTeamMemberInfo>(new RestTeamMemberInfo(teamSpaceMember), HttpStatus.OK);
		} catch (RuntimeException t) {
			if (userInfo != null) {
				TeamSpaceMemberships teamSpaceMemberships = null;
				try {
					teamSpaceMemberships = teamSpaceMembershipService.getTeamSpaceMemberByUserNoCheck(teamId, String.valueOf(userInfo.getId()), TeamSpaceMemberships.TYPE_USER);
				} catch (ForbiddenException e) {
					LOGGER.warn("forbidden exception");
				}
				String userLoginName = null;
				if (teamSpaceMemberships != null) {
					userLoginName = getUserLoginName(String.valueOf(teamSpaceMemberships.getUserId()), teamSpaceMemberships.getUserType());
				}
				sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.GET_TEAMSPACE_MEMBER_ERR, teamId, userLoginName, teamSpaceMemberships != null ? teamSpaceMemberships.getRole() : null);
			}
			throw t;
		}
	}

	/**
	 * 列举团队空间成员列表,支持成员名过滤
	 * 
	 * @param teamId
	 * @param listRequest
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/items", method = RequestMethod.POST)
    @ApiOperation(value = "获取团队空间成员列表", notes = "支持成员名过滤")
    @ResponseBody
	public ResponseEntity<RestTeamMemberList> listTeamMember(@PathVariable Long teamId, @RequestBody ListTeamSpaceMemberRequest listRequest, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date)
			throws BaseRunException {
		UserToken userInfo = null;
		TeamSpace teamSpace = null;
		boolean isAccountOper = false;
		try {
			userInfo = userTokenHelper.getUserToken(token, date);

            FilesCommonUtils.checkNonNegativeIntegers(teamId);
            listRequest.checkParameter();
            // 设置每次返回结果数
            Limit limitObj = new Limit(listRequest.getOffset(), listRequest.getLimit());
            // 判断空间是否存在
            teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);

			TeamMemberList teamMemberList = teamSpaceMembershipManager.listTeamSpaceMemberships(userInfo, teamId, listRequest.getOrder(), limitObj, listRequest.getTeamRole(), listRequest.getKeyword(), isAccountOper);

			return new ResponseEntity<>(new RestTeamMemberList(teamMemberList), HttpStatus.OK);
		} catch (RuntimeException t) {
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.LIST_TEAMSPACE_MEMBER_ERR, teamId, null, null);
			throw t;
		}
	}

	/**
	 * 修改团队空间成员角色
	 * 
	 * @param modifyRequest
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/{teamMemberId}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新团队空间成员角色")
    @ResponseBody
	public ResponseEntity<RestTeamMemberInfo> modifyTeamMemberRole(@RequestBody RestTeamMemberModifyRequest modifyRequest, @PathVariable Long teamId, @PathVariable Long teamMemberId, @RequestHeader("Authorization") String token,
			@RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		TeamSpace teamSpace = null;
		UserToken userInfo = null;
		boolean isAccountOper = false;
		try {
			FilesCommonUtils.checkNonNegativeIntegers(teamId, teamMemberId);
			modifyRequest.checkParameter();
			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在 TODO 事务问题
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipManager.modifyTeamSpaceMemberRoleById(userInfo, teamId, teamMemberId, modifyRequest.getTeamRole(), modifyRequest.getRole(), isAccountOper);

			return new ResponseEntity<>(new RestTeamMemberInfo(teamSpaceMember), HttpStatus.OK);
		} catch (RuntimeException t) {
			String teamRole = modifyRequest.getTeamRole();
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.MODIFY_TEAMSPACE_MEMBER_ERR, teamId, null, teamRole);
			throw t;
		}
	}

	/**
	 * 修改拥有者
	 *
	 * @param modifyRequest
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/admin/{cloudUserId}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改拥有者")
    @ResponseBody
	public ResponseEntity<?> changeOwner(@PathVariable Long teamId, @PathVariable Long cloudUserId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		TeamSpace teamSpace = null;
		UserToken userInfo = null;
		try {
			userInfo = userTokenHelper.getUserToken(token, date);

			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);

			TeamSpaceMemberships membership = teamSpaceMembershipService.getByTeamIdAndUserId(teamId, cloudUserId);
			if (membership != null) {
				// 检查状态是否正常
				if (membership.getStatus() != TeamSpaceMemberships.STATUS_NORMAL) {
					String msg = "teamSpaceMember abnormal, teamSpaceId:" + teamId + ", userId:" + cloudUserId + ", status:" + membership.getStatus();
					return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
				}
			}

			teamSpaceService.changeTeamSpaceOwner(teamSpace, cloudUserId);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException t) {
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.MODIFY_TEAMSPACE_MEMBER_ERR, teamId, null, TeamRole.ROLE_ADMIN);
			throw t;
		}
	}

	/**
	 * 修改拥有者
	 *
	 * @param modifyRequest
	 * @param teamId
	 * @param teamMemberId
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "/deptAdmin/{cloudUserId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> changDeptAdmin(@PathVariable Long teamId, @PathVariable Long cloudUserId, @RequestHeader("Authorization") String token, @RequestHeader(value = "Date", required = false) String date) throws BaseRunException {
		TeamSpace teamSpace = null;
		UserToken userInfo = null;
		try {
			userInfo = userTokenHelper.getUserToken(token, date);
			// 判断空间是否存在
			teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamId);
			teamSpaceService.updateOwner(userInfo, teamId, cloudUserId);

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RuntimeException t) {
			sendTeamSpaceEvent(teamSpace, userInfo, UserLogType.MODIFY_TEAMSPACE_MEMBER_ERR, teamId, null, TeamRole.ROLE_ADMIN);
			throw t;
		}
	}

	private String getUserLoginName(String userId, String userType) {
		if (!TeamSpaceMemberships.TYPE_USER.equals(userType)) {
			return null;
		}
		String loginName = null;
		User user = userService.get(null, Long.valueOf(userId));
		loginName = user != null ? user.getLoginName() : null;
		return loginName;
	}

}
