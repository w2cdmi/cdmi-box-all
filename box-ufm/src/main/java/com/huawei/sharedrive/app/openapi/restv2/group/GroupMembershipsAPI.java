package com.huawei.sharedrive.app.openapi.restv2.group;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.manager.GroupMembershipsManager;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.TokenChecker;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMemberOrderRequest;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsInfo;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsList;
import com.huawei.sharedrive.app.openapi.domain.group.RestAddGroupRequest;
import com.huawei.sharedrive.app.openapi.domain.group.RestModifyMemberRequest;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

@Controller
@RequestMapping(value = "/api/v2/groups/{groupId}/memberships")
public class GroupMembershipsAPI {
	@Autowired
	private GroupMembershipsManager groupMembershipManager;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addMemberships(@RequestBody RestAddGroupRequest memberships,
			@RequestHeader("Authorization") String authToken, @PathVariable("groupId") Long groupId,
			@RequestHeader(value = "Date", required = false) String date) {
		UserToken userToken = null;
		try {
			userToken = checkUserTokenAndAppToken(authToken, memberships.getMember().getUserId(), date);
			memberships.checkParameter();
			FilesCommonUtils.checkNonNegativeIntegers(groupId);
			GroupMembershipsInfo groupMembershipsInfo = groupMembershipManager.addMemberShips(userToken, memberships,
					groupId);
			return new ResponseEntity<GroupMembershipsInfo>(groupMembershipsInfo, HttpStatus.CREATED);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { null,
					memberships.getGroupRole() != null ? memberships.getGroupRole() : GroupConstants.ROLE_MEMBER,
					String.valueOf(groupId) };
			groupMembershipManager.sendEvent(userToken, EventType.OTHERS, null, null, UserLogType.ADD_GROUP_MEMBER_ERR,
					logParams, null, groupId, memberships.getMember().getUserId(),
					memberships.getMember().getUserType());
			throw t;
		}
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteMember(@RequestHeader("Authorization") String authToken,
			@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId,
			@RequestHeader(value = "Date", required = false) String date) {
		UserToken userToken = null;
		try {
			userToken = checkUserTokenAndAppToken(authToken, date);
			FilesCommonUtils.checkNonNegativeIntegers(groupId, userId);
			groupMembershipManager.deleteOne(userToken, userId, groupId);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { null, null, String.valueOf(groupId) };
			groupMembershipManager.sendEvent(userToken, EventType.OTHERS, null, null,
					UserLogType.DELETE_GROUP_MEMBER_ERR, logParams, null, groupId, userId,
					GroupConstants.USERTYPE_USER);
			throw t;
		}
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> listMembers(@RequestHeader("Authorization") String authToken,
			@RequestHeader(value = "Date", required = false) String date, @PathVariable("groupId") Long groupId,
			@RequestBody GroupMemberOrderRequest memberRequest) {
		FilesCommonUtils.checkNonNegativeIntegers(groupId);
		memberRequest.checkParameter();
		UserToken userToken = checkUserTokenAndAppToken(authToken, date);
		GroupMembershipsList memberList;
		try {
			memberList = groupMembershipManager.listMembers(userToken, memberRequest.getOrder(),
					memberRequest.getLimit(), memberRequest.getOffset(), memberRequest.getGroupRole(),
					memberRequest.getKeyword(), groupId);
		} catch (RuntimeException e) {
			groupMembershipManager.sendEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_GROUP_MEMBER_ERR,
					null, null, groupId, null, GroupConstants.USERTYPE_USER);
			throw e;
		}
		groupMembershipManager.sendEvent(userToken, EventType.OTHERS, null, null, UserLogType.LIST_GROUP_MEMBER, null,
				null, groupId, null, GroupConstants.USERTYPE_USER);
		return new ResponseEntity<GroupMembershipsList>(memberList, HttpStatus.OK);
	}

	@RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> modifyMember(@RequestHeader("Authorization") String authToken,
			@PathVariable("groupId") Long groupId, @RequestBody RestModifyMemberRequest modifyRequest,
			@PathVariable("userId") Long userId, @RequestHeader(value = "Date", required = false) String date) {
		UserToken userToken = null;
		try {
			userToken = checkUserTokenAndAppToken(authToken, userId, date);
			FilesCommonUtils.checkNonNegativeIntegers(groupId, userId);
			checkRole(modifyRequest.getGroupRole());
			GroupMembershipsInfo groupMembershipsInfo = groupMembershipManager.modifyMemberships(userToken,
					modifyRequest.getGroupRole(), userId, groupId);

			return new ResponseEntity<GroupMembershipsInfo>(groupMembershipsInfo, HttpStatus.OK);
		} catch (RuntimeException t) {
			String[] logParams = new String[] { null, null, String.valueOf(groupId) };
			groupMembershipManager.sendEvent(userToken, EventType.OTHERS, null, null,
					UserLogType.MODIFY_GROUP_MEMBER_ERR, logParams, null, groupId, userId,
					GroupConstants.USERTYPE_USER);
			throw t;
		}
	}

	private void checkRole(String role) {
		if (role == null) {
			role = GroupConstants.ROLE_MEMBER;
		}
		if (!StringUtils.equals(role, GroupConstants.ROLE_ADMIN)
				&& !StringUtils.equals(role, GroupConstants.ROLE_MANAGER)
				&& !StringUtils.equals(role, GroupConstants.ROLE_MEMBER)) {
			throw new InvalidParamException("groupRole error:" + role);
		}
	}

	private UserToken checkUserTokenAndAppToken(String authToken, Long userId, String date) {
		UserToken userToken = null;
		if (authToken.startsWith(UserTokenHelper.APP_PREFIX)
				|| authToken.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
			userToken = new UserToken();
			Account account = userTokenHelper.checkAccountToken(authToken, date);
			userToken.setAppId(account.getAppId());
			userToken.setId(UserToken.APP_GROUP_ID);
			userToken.setLoginName(TokenChecker.getAk(authToken));
			userTokenHelper.isAppUser(account.getAppId(), userId);
			userToken.setAccountVistor(account);
		} else {
			userToken = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
			userTokenHelper.checkUserStatus(null, userToken.getCloudUserId());
		}
		return userToken;
	}

	private UserToken checkUserTokenAndAppToken(String authToken, String date) {
		UserToken userToken = null;
		if (authToken.startsWith(UserTokenHelper.APP_PREFIX)
				|| authToken.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
			userToken = new UserToken();
			Account account = userTokenHelper.checkAccountToken(authToken, date);
			userToken.setAppId(account.getAppId());
			userToken.setId(UserToken.APP_GROUP_ID);
			userToken.setLoginName(TokenChecker.getAk(authToken));
			userToken.setAccountVistor(account);
		} else {
			userToken = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
			userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
		}
		return userToken;
	}

}
