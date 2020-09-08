package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.openapi.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.manager.LoginManager;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/api/v2/token")
public class TokenAPIController {
	private static Logger logger = LoggerFactory.getLogger(TokenAPIController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private LoginManager loginManager;

	private WxUserService wxUserService;

	/**
	 * 
	 * @param requestDomain
	 * @return
	 * @throws BaseRunException
	 * @throws IOException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> login(@RequestBody RestUserLoginCreateRequest requestDomain, HttpServletRequest request) throws BaseRunException, IOException {
		requestDomain.checkParameter(request);
		AuthApp authApp = getAuthApp(requestDomain.getAppId(), requestDomain.getLoginName());
		RestLoginResponse restLoginResponse = loginManager.userLogin(request, requestDomain, authApp);
		HttpSession session = request.getSession();
		if (session.getAttribute("ChgPwd") != null) {
			if ((boolean) session.getAttribute("ChgPwd")) {
				restLoginResponse.setNeedChangePassword(true);
			}
		}
		return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
	}

	// 微信用户登录
	@RequestMapping(value = "/wx", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxLogin(@RequestBody RestWxUserLoginRequest loginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		loginRequest.checkParameter(request);
		RestLoginResponse restLoginResponse = loginManager.userLogin(request, loginRequest);
		return new ResponseEntity<>(restLoginResponse, HttpStatus.OK);
	}

	// 企业微信用户登录
	@RequestMapping(value = "/wxwork", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxworkLogin(@RequestBody RestWxworkUserLoginRequest loginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		loginRequest.checkParameter(request);
		RestLoginResponse restLoginResponse = loginManager.userLogin(request, loginRequest);
		restLoginResponse.setIsWxEnterprise(RestLoginResponse.TRUE);
		return new ResponseEntity<>(restLoginResponse, HttpStatus.OK);
	}

	// 微信小程序用户登录
	@RequestMapping(value = "/wxmp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxMpLogin(@RequestBody RestWxMpUserLoginRequest loginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		loginRequest.checkParameter(request);
		RestLoginResponse restLoginResponse = loginManager.userLogin(request, loginRequest);
		return new ResponseEntity<>(restLoginResponse, HttpStatus.OK);
	}
	
	// 企业微信内部小程序用户登录
	@RequestMapping(value = "/wxworkwxmp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxworkWxmpLogin(@RequestBody RestWxworkWxMpUserLoginRequest loginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		loginRequest.checkParameter(request);
		RestLoginResponse restLoginResponse = loginManager.userLogin(request, loginRequest);
		return new ResponseEntity<>(restLoginResponse, HttpStatus.OK);
	}


	private AuthApp getAuthApp(String appId, String loginName) {
		AuthApp authApp = authAppService.getByAuthAppID(appId);
		if (null == authApp) {
			userLogService.saveFailLog(loginName, appId, null, UserLogType.KEY_GET_TOKEN_ERR);
			logger.error("no such appId:" + appId);
			throw new InvalidParamterException();
		}
		return authApp;
	}

	/**
	 * 
	 * @param requestDomain
	 * @return
	 * @throws BaseRunException
	 * @throws IOException
	 */
	@RequestMapping(value = "wxrobot", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxrobot(@RequestBody RestRobotLoginRequest requestDomain, HttpServletRequest request) throws BaseRunException, IOException {
		String appId = requestDomain.getAppId();
		String loginName = requestDomain.getLoginName();
		AuthApp authApp = getAuthApp(appId, loginName);
		RestLoginResponse restLoginResponse = loginManager.robotLogin(request, requestDomain, authApp);
		restLoginResponse.setAppId(appId);
		return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
	}

	/**
	 * 
	 * @param requestDomain
	 * @return
	 * @throws BaseRunException
	 * @throws IOException
	 */
	@RequestMapping(value = "/person", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> wxUserLogin(@RequestBody RestPersonRobotLoginRequest restPersonLoginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		RestLoginResponse restLoginResponse = loginManager.loginWithWxUser(request, restPersonLoginRequest.getWxUnionId(), restPersonLoginRequest.getAppId());
		return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
	}

	/**
	 *
	 * @param requestDomain
	 * @return
	 * @throws BaseRunException
	 * @throws IOException
	 */
	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<RestLoginResponse> adminLogin(@RequestBody RestPersonRobotLoginRequest restPersonLoginRequest, HttpServletRequest request) throws BaseRunException, IOException {
		RestLoginResponse restLoginResponse = loginManager.loginWithWxUser(request, restPersonLoginRequest.getWxUnionId(), restPersonLoginRequest.getAppId());
		return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws BaseRunException
	 */
	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<RestTokenRefreshResponse> refreshToken(@RequestHeader("Authorization") String authorization, HttpServletRequest request) throws BaseRunException {

		UserToken newUserToken;
		UserLog userLog = null;
		try {
			UserToken userToken = userTokenHelper.getUserTokenByRefreshToken(authorization);
			userLog = UserLogType.getUserLog(userToken);
			long accountId = userToken.getAccountId();
			long userId = userToken.getId();
			if (accountId != 0) {
				UserAccount userAccount = userAccountManager.get(userId, accountId);
				if (null == userAccount) {
					logger.error("userAccount is null userId:" + userId + " accountId:" + accountId);
					throw new NoSuchUserException();
				}
				if (UserAccount.INT_STATUS_DISABLE == userAccount.getStatus()) {
					logger.error("user status is disable  userId:" + userId + " accountId:" + accountId);
					throw new DisabledUserApiException();
				}
			}

			newUserToken = userTokenHelper.refreshToken(request, userToken.getToken(), userToken.getRefreshToken(), userToken.getAppId());
		} catch (RuntimeException e) {
			userLogService.saveUserLog(userLog, UserLogType.KEY_REFRESH_TOKEN_ERR, null);

			throw e;
		}
		RestTokenRefreshResponse restTokenRefreshResponse = new RestTokenRefreshResponse();
		restTokenRefreshResponse.setTimeout(userTokenHelper.getTokenExpireTime() / 1000);
		restTokenRefreshResponse.setToken(newUserToken.getToken());
		restTokenRefreshResponse.setRefreshToken(newUserToken.getRefreshToken());
		userLogService.saveUserLog(userLog, UserLogType.KEY_REFRESH_TOKEN, null);

		return new ResponseEntity<RestTokenRefreshResponse>(restTokenRefreshResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteToken(@RequestHeader("Authorization") String authorization) throws BaseRunException {
		UserToken userToken = null;
		UserLog userLog = null;
		try {
			userToken = userTokenHelper.unSafeCheckTokenAndGetUser(authorization);
			userLog = UserLogType.getUserLog(userToken);
			userTokenHelper.delUserToken(userToken);
		} catch (NoSuchTokenException e1) {
			logger.info("No token found: " + authorization);
		} catch (RuntimeException e) {
			userLogService.saveUserLog(userLog, UserLogType.KEY_DELETE_TOKEN_ERR, null);
			throw e;
		}
		userLogService.saveUserLog(userLog, UserLogType.KEY_DELETE_TOKEN, null);
		userService.createEvent(userToken, EventType.USER_LOGOUT, userToken.getId());
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
