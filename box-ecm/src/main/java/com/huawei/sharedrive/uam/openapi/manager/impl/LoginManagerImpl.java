package com.huawei.sharedrive.uam.openapi.manager.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.ManagerLocked;
import pw.cdmi.common.domain.UserSignDeclare;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.uam.domain.AuthApp;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authapp.manager.AuthAppNetRegionIpManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.declare.manager.UserSignDeclareManager;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountConfigService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterprisecontrol.EnterpriseAuthControlManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.DisabledUserApiException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginTerminalManager;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LoginUpdateManagerImpl;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.log.service.UserLoginLogService;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.EnterpriseInfo;
import com.huawei.sharedrive.uam.openapi.domain.GlobalErrorMessage;
import com.huawei.sharedrive.uam.openapi.domain.RestLoginResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestRobotLoginRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestTerminalRsp;
import com.huawei.sharedrive.uam.openapi.domain.RestUserLoginCreateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestUserRegister;
import com.huawei.sharedrive.uam.openapi.domain.RestWxLoginResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestWxMpUserLoginRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestWxUserLoginRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestWxworkUserLoginRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestWxworkWxMpUserLoginRequest;
import com.huawei.sharedrive.uam.openapi.manager.LoginManager;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;
import com.huawei.sharedrive.uam.util.RequestUtils;
import com.huawei.sharedrive.uam.weixin.domain.ShareLevel;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterpriseUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.WxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxWorkUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.WxworkWxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxworkWxMpOauth2Proxy;
import com.huawei.sharedrive.uam.weixin.service.ShareLevelService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseUserService;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxUserManager;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;

@Service
public class LoginManagerImpl implements LoginManager {
	private static final String CLOUDAPP = "cloudapp";

	private static final int ONE_FAIL_TIME = 1;

	@Autowired
	private AuthAppNetRegionIpManager authAppNetRegionIpManager;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private CustomizeLogoService customizeLogoService;

	@Autowired
	private AccountConfigService accountConfigService;

	@Autowired
	private EnterpriseAuthControlManager enterpriseAuthControlManager;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private LoginUpdateManagerImpl loginUpdateManagerImpl;

	@Autowired
	private UserLockService userLockService;

	@Autowired
	private UserLogService userLogService;

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private UserLoginLogService userLoginLogService;

	@Autowired
	private UserService userService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private LoginTerminalManager loginTerminalManager;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private UserSignDeclareManager userSignDeclareManager;

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private EnterpriseAccountService enterpriseAccountService;

	@Autowired
	WxUserManager wxUserManager;
	
	@Autowired
	private ShareLevelService shareLevelService;

	@Autowired
	@Qualifier("wxWorkOauth2Service")
	WxWorkOauth2Service wxWorkOauth2Service;

	@Autowired
	WxOauth2Service wxOauth2Service;

	@Autowired
	private WxEnterpriseService wxEnterpriseService;

	@Autowired
	private WxUserService wxUserService;

	@Autowired
	private WxUserEnterpriseService wxUserEnterpriseService;
	
	@Autowired
	private WxEnterpriseUserService wxEnterpriseUserService;
	
	@Autowired
	WxworkWxMpOauth2Proxy wxworkWxMpOauth2Proxy;

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginManagerImpl.class);

	public static final int CLIENT_TYPE_ANDROID = 2;

	public static final String CLIENT_TYPE_ANDROID_STR = "android";

	public static final int CLIENT_TYPE_IOS = 3;

	public static final String CLIENT_TYPE_IOS_STR = "ios";

	public static final int CLIENT_TYPE_PC = 1;

	public static final String CLIENT_TYPE_PC_STR = "pc";

	public static final int CLIENT_TYPE_WEB = 0;

	public static final String CLIENT_TYPE_WEB_STR = "web";

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RestLoginResponse userLogin(HttpServletRequest request, RestUserLoginCreateRequest requestDomain, AuthApp authApp) throws IOException {
		String appId = requestDomain.getAppId();
		String loginName = requestDomain.getLoginName();
		String domainName = requestDomain.getDomain();
		UserLog userLog = UserLogType.getUserLog(request, appId, loginName, false);

		// 优先使用domain登录，如果没有指定，再尝试使用企业名称
		if (StringUtils.isBlank(domainName)) {
			if (StringUtils.isNotBlank(requestDomain.getEnterpriseName())) {
				Enterprise enterprise = enterpriseManager.getByName(requestDomain.getEnterpriseName());
				if (enterprise != null) {
					domainName = enterprise.getDomainName();
				}
			}
		}

		LoginInfo loginInfo = getLoginInfo(appId, loginName, domainName, request.getHeader("x-real-ip"), userLog);
		if (loginInfo == null) {
			throw new LoginAuthFailedException();
		}
		if (loginInfo.getUserId() != 0) {
			UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, loginInfo.getUserId());
			userLockService.createUserLocked(loginInfo.getUserId(), loginName, domainName, appId, tempLock);
		}
		return loginWithLockTrans(request, requestDomain, userLog, loginInfo);
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { UserLockedException.class, LoginAuthFailedException.class, NoSuchUserException.class })
	public RestLoginResponse loginWithLockTrans(HttpServletRequest request, RestUserLoginCreateRequest requestDomain, UserLog userLog, LoginInfo loginInfo) throws IOException {
		String appId = requestDomain.getAppId();
		String loginName = requestDomain.getLoginName();
		String domainName = requestDomain.getDomain();
		String password = requestDomain.getPassword();
		UserLocked userLocked = lockWithDbLock(appId, loginName, domainName, userLog, loginInfo);
		EnterpriseUser enterpriseUserByLdap = enterpriseAuthControlManager.authenticate(loginName, requestDomain.getPassword(), loginInfo, RequestUtils.getRealIP(request));

		if (StringUtils.isNotBlank(domainName)) {
			Enterprise enterprise = enterpriseManager.getByDomainName(domainName);
			EnterpriseAccount domainEn = enterpriseAccountService.getByEnterpriseApp(enterprise.getId(), appId);
			handleNoneLdap(userLog, loginInfo, userLocked, enterpriseUserByLdap, domainEn.getAccountId());
		} else {
			handleNoneLdap(userLog, loginInfo, userLocked, enterpriseUserByLdap, 0);
		}

		EnterpriseUserAccount enterpriseUserAccount = getEnterpriseUserAccount(appId, enterpriseUserByLdap);
		UserToken userToken = new UserToken();
		UserToken.buildUserToken(userToken, enterpriseUserAccount);
		Admin admin = adminService.getByLoginNameAndEnterpriseId(enterpriseUserByLdap.getName(), enterpriseUserByLdap.getEnterpriseId());
		if (admin != null) {
			userToken.setIsAdmin((byte) 1);
		}
		userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
		authAppNetRegionIpManager.setLoginMatchNetRegion(RequestUtils.getLoginRegionIp(request), appId, userToken);
		RestTerminalRsp lastAccessTerminal = loginTerminalManager.getByUserLastLogin(enterpriseUserAccount.getCloudUserId());
		RestLoginResponse restLoginResponse = buildLoginResponse(appId, enterpriseUserByLdap, userLog, enterpriseUserAccount, userToken, lastAccessTerminal);
		fillResetPwdAndDeclare(appId, userLog, userToken, restLoginResponse);
		initPassWrodLevel(request, password, enterpriseUserByLdap, restLoginResponse);
		if (null != userLocked) {
			userLockService.deleteUserLocked(userLocked);
		}
		if (admin != null) {
			restLoginResponse.setIsAdmin((byte) 1);
		}
		return restLoginResponse;
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { UserLockedException.class, LoginAuthFailedException.class, NoSuchUserException.class })
	public RestLoginResponse loginWithPassword(HttpServletRequest request, RestRobotLoginRequest robotRequest, UserLog userLog, LoginInfo loginInfo) throws IOException {
		String appId = robotRequest.getAppId();
		long enterpriseUserId = robotRequest.getEnterpriseUserId();
		long enterpriseId = robotRequest.getEnterpriseId();
		EnterpriseUser enterpriseUser = enterpriseUserService.get(enterpriseUserId, enterpriseId);
		EnterpriseUserAccount enterpriseUserAccount = getEnterpriseUserAccount(appId, enterpriseUser);
		UserToken userToken = new UserToken();

		
		userToken.setAppId(appId);
		authAppNetRegionIpManager.setLoginMatchNetRegion(RequestUtils.getLoginRegionIp(request), appId, userToken);
		RestTerminalRsp lastAccessTerminal = loginTerminalManager.getByUserLastLogin(enterpriseUserAccount.getCloudUserId());
		userToken.setCloudUserId(enterpriseUserAccount.getCloudUserId());
		userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
		RestLoginResponse restLoginResponse = RestLoginResponse.fillRestLoginResponse(userToken);
//		fillResetPwdAndDeclare(appId, userLog, userToken, restLoginResponse);
		UserToken.buildUserToken(userToken, enterpriseUserAccount);
		// 根据企业ID查找密码复杂度
		Map<String, Long> bandWidthMap = userService.fillBandWidth(enterpriseUserAccount.getDownloadBandWidth(), enterpriseUserAccount.getUploadBandWidth(), appId);
		restLoginResponse.setUploadQos(bandWidthMap.get(UserServiceImpl.UPLOAD_BANDWIDTH));
		restLoginResponse.setDownloadQos(bandWidthMap.get(UserServiceImpl.DOWNLOAD_BANDWIDTH));
		userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
		userLogService.saveUserLog(userLog, UserLogType.KEY_GET_TOKEN, null);

		restLoginResponse.setLastAccessTerminal(lastAccessTerminal);
		restLoginResponse.setAlias(enterpriseUser.getAlias());
		restLoginResponse.setMobile(enterpriseUser.getMobile());
		restLoginResponse.setAppId(appId);
		restLoginResponse.setDomain(enterpriseManager.getById(enterpriseId).getDomainName());
		return restLoginResponse;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RestLoginResponse userLogin(HttpServletRequest request, RestWxUserLoginRequest login) throws IOException {
		WxUserInfo userInfo = wxOauth2Service.getWxUserInfo(login.getCode());
		if (userInfo == null) {
			LOGGER.error("Can't get UserInfo of code {}: return value is null", login.getCode());
			throw new LoginAuthFailedException("Failed to get UserInfo: return value is null.");
		}

		if (userInfo.hasError()) {
			LOGGER.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", login.getCode(), userInfo.getErrcode(), userInfo.getErrmsg());
			throw new LoginAuthFailedException("Can't get UserInfo of code.");
		}

		WxUser wxUser = wxUserService.getByUnionId(userInfo.getUnionid());
		String appId = login.getAppId();
		if (wxUser == null) {
			LOGGER.error("No such weixin user: unionId={}, openId={}", userInfo.getUnionid(), userInfo.getOpenid());
			throw new LoginAuthFailedException("No such weixin user.");
		}

		if (login.getIdentity() == 1) {
			// 微信个人文件宝
			UserLog userLog = UserLogType.getUserLog(request, appId, wxUser.getNickName(), false);
			return loginWithWxUser(request, appId, wxUser, userLog);

		} else {
			// 微信企业文件宝
			WxUserEnterprise wxUserEnterprise = null;
			if (login.getEnterpriseId() != null) {
				// 登录时指定了企业
				wxUserEnterprise = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(wxUser.getUnionId(), login.getEnterpriseId());
				if (wxUserEnterprise == null) {
					LOGGER.error("No account of weixin user: unionId={}, openId={}", userInfo.getUnionid(), userInfo.getOpenid());
					throw new LoginAuthFailedException("No such weixin user.");
				}
				return wxUserEnterpriseLogin(request, wxUser, appId, wxUserEnterprise);

			} else {
				// 未指定企业登录
				List<WxUserEnterprise> enterpriseList = wxUserEnterpriseService.listByUnionId(wxUser.getUnionId());
				if (enterpriseList.size() == 1) {
					wxUserEnterprise = enterpriseList.get(0);
					return wxUserEnterpriseLogin(request, wxUser, appId, wxUserEnterprise);

				} else if (enterpriseList.size() > 1) {
					// 有多个企业账户，返回账户列表，让用户选择要登录的账户。
					EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
					RestWxLoginResponse response = new RestWxLoginResponse();
					response.setEnterpriseList(infoList);
					return response;

				} else {
					// enterpriseList等于0，未找到企业
					LOGGER.error("No account of weixin user: unionId={}, openId={}", userInfo.getUnionid(), userInfo.getOpenid());
					throw new LoginAuthFailedException("No such weixin user.");
				}
			}
		}
	}

	private RestLoginResponse wxUserEnterpriseLogin(HttpServletRequest request, WxUser wxUser, String appId, WxUserEnterprise wxUserEnterprise) throws IOException {
		UserLog userLog = UserLogType.getUserLog(request, appId, wxUser.getNickName(), false);
		userLoginLogService.saveLog(userLog);
		EnterpriseUser user = enterpriseUserService.get(wxUserEnterprise.getEnterpriseUserId(), wxUserEnterprise.getEnterpriseId());
		if (user == null) {
			throw new LoginAuthFailedException("Enterprise User doesn't exist, userId: " + wxUserEnterprise.getEnterpriseUserId() + "; enterpriseId: " + wxUserEnterprise.getEnterpriseId());
		}
		Enterprise enterprise = enterpriseManager.getById(wxUserEnterprise.getEnterpriseId());
		UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, wxUserEnterprise.getEnterpriseUserId());
		userLockService.createUserLocked(wxUserEnterprise.getEnterpriseUserId(), user.getName(), enterprise.getDomainName(), appId, tempLock);
		// 使用原有的异常传递机制：异常由GlobalExceptionHandler抓获，然后统一响应。使用HTTP STATUS使用403
		return loginWithLockTrans(request, appId, user, userLog);
	}

	protected EnterpriseInfo[] getEnterpriseInfoList(List<WxUserEnterprise> enterpriseList) {
		EnterpriseInfo[] infoList = new EnterpriseInfo[enterpriseList.size()];
		for (int i = 0; i < infoList.length; i++) {
			Enterprise enterprise = enterpriseManager.getById(enterpriseList.get(i).getEnterpriseId());
			if (enterprise != null) {
				infoList[i] = new EnterpriseInfo();
				infoList[i].setId(enterprise.getId());
				infoList[i].setName(enterprise.getName());
			} else {
				infoList[i] = new EnterpriseInfo();
				infoList[i].setId(0);
				infoList[i].setName("个人帐号");
			}

		}
		return infoList;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RestLoginResponse userLogin(HttpServletRequest request, RestWxworkUserLoginRequest login) throws IOException {
		String appId = login.getAppId();
		String corpId;
		String userId;

		// auth_code不为空，走第三方登录接口
		if (StringUtils.isNotBlank(login.getAuthCode())) {
			com.huawei.sharedrive.uam.weixin.rest.LoginInfo userInfo = wxWorkOauth2Service.getLoginInfo(login.getAuthCode());
			if (userInfo == null) {
				LOGGER.error("Failed to get user info from Weixin OAuth2 server: authCode={}, user=null", login.getAuthCode());
				throw new LoginAuthFailedException("Failed to get user info from Weixin OAuth2 server.");
			}

			if (userInfo.hasError()) {
				LOGGER.error("Failed to get user info from Weixin OAuth2 server: authCode={}, errorCode={}, errorMsg={}", login.getAuthCode(), userInfo.getErrcode(), userInfo.getErrmsg());
				throw new LoginAuthFailedException("Failed to get user info from Weixin OAuth2 server.");
			}

			// 根据corpId查询内部企业ID
			corpId = userInfo.getCorpInfo().getCorpid();
			userId = userInfo.getUserInfo().getUserid();
		} else {
			// 从企业微信浏览器登录
			WxWorkUserInfo userInfo = wxWorkOauth2Service.getUserInfoByCode(login.getCorpId(), login.getCode());
			if (userInfo == null) {
				LOGGER.error("Failed to get user info from Weixin OAuth2 server: corpId={}, code={}, user=null", login.getCorpId(), login.getCode());
				throw new LoginAuthFailedException("Failed to get user info from Weixin OAuth2 server.");
			}

			if (userInfo.hasError()) {
				LOGGER.error("Failed to get user info from Weixin OAuth2 server: corpId={}, code={}, errorCode={}, errorMsg={}", login.getCorpId(), login.getCode(), userInfo.getErrcode(), userInfo.getErrmsg());
				throw new LoginAuthFailedException("Failed to get user info from Weixin OAuth2 server.");
			}

			corpId = login.getCorpId();
			userId = userInfo.getUserId();
		}

		WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
		if (wxEnterprise == null) {
			LOGGER.error("WxEnterprise doesn't exist: corpId={}, userId={}", corpId, userId);
			throw new LoginAuthFailedException("WxEnterprise doesn't exist, corpId: " + corpId + "; userId: " + userId);
		}

		if (wxEnterprise.getBoxEnterpriseId() == null) {
			LOGGER.error("No Enterprise Account bound: corpId={}, userId={}, enterpriseId=null.", corpId, userId);
			throw new LoginAuthFailedException("Enterprise Account doesn't exist, corpId: " + corpId + "; userId: " + userId);
		}

		long enterpriseId = wxEnterprise.getBoxEnterpriseId();
		EnterpriseUser user = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, userId);
		if (user == null) {
			LOGGER.error("WxEnterpriseUser doesn't exist: enterpriseId={}, userId={}", enterpriseId, userId);
			throw new LoginAuthFailedException("User doesn't exist, userId: " + userId + "; enterpriseId: " + enterpriseId);
		}

		long enterpriseUserId = user.getId();
		UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, enterpriseUserId);
		userLockService.createUserLocked(enterpriseUserId, userId, corpId, appId, tempLock);

		// 记录登录信息
		UserLog userLog = UserLogType.getUserLog(request, appId, user.getAlias(), false);
		userLoginLogService.saveLog(userLog);

		// 使用原有的异常传递机制：异常由GlobalExceptionHandler抓获，然后统一响应。使用HTTP STATUS使用403
		return loginWithLockTrans(request, appId, user, userLog);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public RestLoginResponse userLogin(HttpServletRequest request, RestWxMpUserLoginRequest login) throws IOException {
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(login.getMpId(), login.getCode(), login.getIv(), login.getEncryptedData());
		if (mpUserInfo == null) {
			LOGGER.error("WxMp login Failed: wxMpUserInfo is null.");
			throw new LoginAuthFailedException("Failed to get user info.");
		}
		if (mpUserInfo.hasError()) {
			LOGGER.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", login.getCode(), mpUserInfo.getErrcode(), mpUserInfo.getErrmsg());
			throw new LoginAuthFailedException("Failed to get user info.");
		}
		

		WxUser dbWxUser = wxUserService.getByUnionId(mpUserInfo.getUnionId());
		String appId = login.getAppId();

		// 用户不存在，自动生成账户
		if (dbWxUser == null) {
			WxUser inviter = wxUserService.getCloudUserId(login.getInviterCloudUserId());
			openAccount(mpUserInfo,inviter==null?null:inviter.getUnionId());
			if(inviter!=null){
				//更新邀请用户总数
				inviter.setCountInvitByMe(inviter.getCountInvitByMe()+1);
				//更新今日邀请用户数
				inviter.setCountTodayInvitByMe(inviter.getCountTodayInvitByMe()+1);
				List<ShareLevel> shareLevelList=shareLevelService.list();
				for(ShareLevel shareLevel : shareLevelList){
					if(inviter.getCountInvitByMe()>= shareLevel.getStartRange()&&inviter.getCountInvitByMe()<shareLevel.getEndRange()){
						inviter.setShareLevel(shareLevel.getId());
						break;
					}
				}
				wxUserService.updateCountInvitByMe(inviter);
				wxUserService.updateCountTodayInvitByMe(inviter);
			}
			dbWxUser = wxUserService.getByUnionId(mpUserInfo.getUnionId());
			LOGGER.info("Wx User doesn't exist: unionId={}, nickName={}, create wxUser", mpUserInfo.getUnionId(), mpUserInfo.getNickName());
		} else {
			if (dbWxUser.getCloudUserId() == null || dbWxUser.getCloudUserId() == 0) {
				wxUserService.createWxUserAccount(dbWxUser);
				wxUserService.update(dbWxUser);
			}
		}
		WxUserEnterprise old = new WxUserEnterprise();
		old.setUnionId(dbWxUser.getUnionId());
		old.setEnterpriseId(0L);
		old.setEnterpriseUserId(0L);
		if (wxUserEnterpriseService.getByUnionIdAndEnterpriseId(old.getUnionId(), old.getEnterpriseId()) == null) {
			wxUserEnterpriseService.create(old);
		}

		WxUserEnterprise wxUserEnterprise = null;
		UserLog userLog = UserLogType.getUserLog(request, appId, dbWxUser.getNickName(), false);
		List<WxUserEnterprise> enterpriseList = wxUserEnterpriseService.listByUnionId(dbWxUser.getUnionId());
		if (login.getEnterpriseId() == null) {
			if (enterpriseList != null) {
				// 所在企业等于1
				if (enterpriseList.size() == 1) {
					if (enterpriseList.get(0).getEnterpriseId() == 0) {
						// 只有个人账户
						RestLoginResponse restLoginResponse = loginWithWxUser(request, appId, dbWxUser, userLog);
						EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
						restLoginResponse.setEnterpriseList(infoList);
						return restLoginResponse;
					} else {
						// 只有企业账户
						wxUserEnterprise = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(dbWxUser.getUnionId(), enterpriseList.get(0).getEnterpriseId());
						RestLoginResponse restLoginResponse = createLoginRespons(request, mpUserInfo, appId, wxUserEnterprise, userLog);
						EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
						restLoginResponse.setEnterpriseList(infoList);
						return restLoginResponse;
					}
				} else if (enterpriseList.size() > 1) {
					// 有多个企业账户，返回账户列表，让用户选择要登录的账户。
					EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
					RestLoginResponse response = new RestLoginResponse(GlobalErrorMessage.TOO_MANY_ACCOUNT);
					response.setEnterpriseList(infoList);
					return response;
				}
			}
		} else {
			if (login.getEnterpriseId() == 0) {
				// 登录个人能账户
				RestLoginResponse restLoginResponse = loginWithWxUser(request, appId, dbWxUser, userLog);
				EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
				restLoginResponse.setEnterpriseList(infoList);
				return restLoginResponse;
			} else {
				// 登录企业账户
				wxUserEnterprise = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(dbWxUser.getUnionId(), login.getEnterpriseId());
				RestLoginResponse restLoginResponse = createLoginRespons(request, mpUserInfo, appId, wxUserEnterprise, userLog);
				EnterpriseInfo[] infoList = getEnterpriseInfoList(enterpriseList);
				restLoginResponse.setEnterpriseList(infoList);
				return restLoginResponse;
			}
		}
		return new RestWxLoginResponse(GlobalErrorMessage.USER_DISABLED);
	}

	private RestLoginResponse createLoginRespons(HttpServletRequest request, WxMpUserInfo mpUserInfo, String appId, WxUserEnterprise wxUserEnterprise, UserLog userLog) {
		// 记录登录信息
		LOGGER.info("WxUserEnterprise has found, login as person account: unionId={}, nickName={}", mpUserInfo.getUnionId(), mpUserInfo.getNickName());
		EnterpriseUser user = enterpriseUserService.get(wxUserEnterprise.getEnterpriseUserId(), wxUserEnterprise.getEnterpriseId());
		if (user == null) {
			LOGGER.warn("No enterprise_user found for Wx User: unionId={}, nickName={}, enterpriseId={}, enterpriseUserId={}", mpUserInfo.getUnionId(), mpUserInfo.getNickName(), wxUserEnterprise.getEnterpriseId(),
					wxUserEnterprise.getEnterpriseUserId());
			return new RestWxLoginResponse(GlobalErrorMessage.ACCOUNT_NOT_EXIST);
		}

		Enterprise enterprise = enterpriseManager.getById(wxUserEnterprise.getEnterpriseId());
		UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, wxUserEnterprise.getEnterpriseUserId());
		userLockService.createUserLocked(wxUserEnterprise.getEnterpriseUserId(), user.getName(), enterprise.getDomainName(), appId, tempLock);

		// 小程序登录，使用了新的异常传递机制：所有的响应都使用200OK，错误码保存在响应数据中。此处捕获异常，然后重新封装。
		try {
			return loginWithLockTrans(request, appId, user, userLog);
		} catch (DisabledUserApiException e) {
			return new RestWxLoginResponse(GlobalErrorMessage.USER_DISABLED);
		} catch (IOException e) {
			LOGGER.error("WxMp user login failed: unionId={}, nickName={}, enterpriseId={}, enterpriseUserId={}", mpUserInfo.getUnionId(), mpUserInfo.getNickName(), wxUserEnterprise.getEnterpriseId(), wxUserEnterprise.getEnterpriseUserId());
			LOGGER.error("WxMp user login failed: ", e);
		}
		return new RestWxLoginResponse(GlobalErrorMessage.USER_DISABLED);
	}

	public void openAccount(WxMpUserInfo mpUserInfo,String inviterUnionId) {

		WxUser wxUser = new WxUser();
		wxUser.setUnionId(mpUserInfo.getUnionId());
		wxUser.setOpenId(mpUserInfo.getOpenId());
		if (mpUserInfo.getCountry() != null && mpUserInfo.getCountry().equals("CN")) {
			try {
				wxUser.setNickName(new String(mpUserInfo.getNickName().getBytes("ISO-8859-1"), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			wxUser.setNickName(mpUserInfo.getNickName());
		}
		wxUser.setGender(mpUserInfo.getGender());
		wxUser.setCountry(mpUserInfo.getCountry());
		wxUser.setProvince(mpUserInfo.getProvince());
		wxUser.setCity(mpUserInfo.getCity());
		wxUser.setLanguage(mpUserInfo.getLanguage());
		wxUser.setAvatarUrl(mpUserInfo.getAvatarUrl());
		wxUser.setEmail(new Date().getTime() + "@filepro.cn");
		wxUser.setStatus(WxUser.STATUS_NORMAL);
		wxUser.setType((byte) 0);
		wxUser.setInviterId(inviterUnionId);
		wxUser.setShareLevel(1);
		wxUserManager.openAccount(wxUser);
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { UserLockedException.class, LoginAuthFailedException.class, NoSuchUserException.class })
	public RestLoginResponse loginWithLockTrans(HttpServletRequest request, String appId, EnterpriseUser user, UserLog userLog) throws IOException {
		// TODO：暂不考虑用户锁定问题
		// 检查用户状态
		if (user.getStatus() != EnterpriseUser.STATUS_ENABLE) {
			throw new DisabledUserApiException();
		}

		EnterpriseUserAccount enterpriseUserAccount = getEnterpriseUserAccount(appId, user);
		UserToken userToken = new UserToken();
		UserToken.buildUserToken(userToken, enterpriseUserAccount);
		RestTerminalRsp lastAccessTerminal = loginTerminalManager.getByUserLastLogin(enterpriseUserAccount.getCloudUserId());
		Admin admin = adminService.getByLoginNameAndEnterpriseId(user.getName(), user.getEnterpriseId());
		if (admin != null) {
			userToken.setIsAdmin((byte) 1);
		}
		userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
		RestLoginResponse restLoginResponse = buildLoginResponse(appId, user, userLog, enterpriseUserAccount, userToken, lastAccessTerminal);
		fillResetPwdAndDeclare(appId, userLog, userToken, restLoginResponse);
		if (admin != null) {
			restLoginResponse.setIsAdmin(RestLoginResponse.TRUE);	
		}
		List<WxEnterpriseUser> wxEnterpriseUserList = wxEnterpriseUserService.getByEnterpriseIdAndUserId(enterpriseUserAccount.getEnterpriseId(), enterpriseUserAccount.getUserId());
		if(wxEnterpriseUserList.size()!=0){
			restLoginResponse.setIsWxEnterprise(RestLoginResponse.TRUE);
		}
		authAppNetRegionIpManager.setLoginMatchNetRegion(RequestUtils.getLoginRegionIp(request), appId, userToken);
		LOGGER.error("restLoginResponse cloudUserId:" + restLoginResponse.getCloudUserId());
		return restLoginResponse;
	}

	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = { UserLockedException.class, LoginAuthFailedException.class, NoSuchUserException.class })
	public RestLoginResponse loginWithWxUser(HttpServletRequest request, String appId, WxUser dbWxUser, UserLog userLog) throws IOException {
		// TODO：暂不考虑用户锁定问题

		UserToken userToken = new UserToken();

		UserToken.buildWxUserToken(userToken, dbWxUser);
		userToken.setAppId(appId);
		authAppNetRegionIpManager.setLoginMatchNetRegion(RequestUtils.getLoginRegionIp(request), appId, userToken);

		RestTerminalRsp lastAccessTerminal = loginTerminalManager.getByUserLastLogin(dbWxUser.getCloudUserId());
		EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
		enterpriseUserAccount.setCloudUserId(dbWxUser.getCloudUserId());
		enterpriseUserAccount.setAccountId(0);
		enterpriseUserAccount.setAppId(appId);
		enterpriseUserAccount.setEnterpriseId(0);
		userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
		RestLoginResponse restLoginResponse = RestLoginResponse.fillRestLoginResponse(userToken);

		userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
		userLogService.saveUserLog(userLog, UserLogType.KEY_GET_TOKEN, null);

		restLoginResponse.setLastAccessTerminal(lastAccessTerminal);
		restLoginResponse.setAppId(appId);
		restLoginResponse.setMobile(dbWxUser.getMobile());
		restLoginResponse.setType(dbWxUser.getType());

		return restLoginResponse;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private UserLocked lockWithDbLock(String appId, String loginName, String domainName, UserLog userLog, LoginInfo loginInfo) {
		UserLocked userLocked = null;
		if (loginInfo.getUserId() != 0) {
			userLocked = userLockService.checkUserLocked(userLog, loginInfo.getUserId(), domainName, appId);
			if (null == userLocked) {
				return null;
			}
			if (userLockService.isLocked(userLocked)) {
				String msg = "user has been locked from " + userLocked.getLockedAt() + ", userloginname is " + loginName;
				userLogService.saveFailLog(userLog, UserLogType.KEY_GET_TOKEN_ERR, null);
				throw new UserLockedException(msg);
			}
		}
		return userLocked;
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	private LoginInfo getLoginInfo(String appId, String loginName, String domainName, String clientIp, UserLog userLog) {
		LoginInfo loginInfo = null;
		try {
			userLoginLogService.saveLog(userLog);
			loginInfo = enterpriseAuthControlManager.getUserLoginInfo(loginName, domainName, appId);
		} catch (NoSuchUserException e)// 匿名用户
		{
			ManagerLocked managerLocked = (ManagerLocked) cacheClient.getCache(CLOUDAPP + loginName + domainName + appId);
			LOGGER.error("Cloudapp anonymous user:" + loginName + " fail to login at " + clientIp);
			if (null == managerLocked) {
				ManagerLocked newManagerLocked = new ManagerLocked();
				newManagerLocked.setCreatedAt(new Date());
				newManagerLocked.setLoginFailTimes(ONE_FAIL_TIME);
				newManagerLocked.setLoginName(loginName);
				cacheClient.setCache(CLOUDAPP + loginName + domainName + appId, newManagerLocked, userLockService.getConfigByLockWait());
			} else {
				managerLocked.setLoginFailTimes(managerLocked.getLoginFailTimes() + ONE_FAIL_TIME);
				cacheClient.replaceCache(CLOUDAPP + loginName + domainName + appId, managerLocked, userLockService.getConfigByLockWait());

				if (managerLocked.getLoginFailTimes() >= userLockService.getConfigByLockTimes()) {
					String msg = "Cloudapp anonymous user:" + loginName + " has been locked at " + clientIp;
					LOGGER.error(msg);
					throw new UserLockedException(msg);
				}
			}
			throw new LoginAuthFailedException();
		} catch (RuntimeException e) {
			userLogService.saveFailLog(userLog, UserLogType.KEY_GET_TOKEN_ERR, null);
			throw e;
		}
		return loginInfo;
	}

	private void handleNoneLdap(LoginInfo loginInfo, UserLocked userLocked, UserLog userLog, long accountId) {
		EnterpriseUser enterpriseUser = enterpriseUserService.get(loginInfo.getUserId(), loginInfo.getEnterpriseId());
		AuthServer authServer = authServerManager.getAuthServer(enterpriseUser.getUserSource());
		if (AuthServer.AUTH_TYPE_AD.equalsIgnoreCase(authServer.getType())) {
			throw new LoginAuthFailedException("AD", "AD user login failed");
		}
		if (null != userLocked) {

			boolean locked = userLockService.addUserLocked(userLocked, accountId);
			try {
				if (accountId != 0) {
					AccountConfig notice_enable = accountConfigService.get(accountId, AccountConfigAttribute.SECURITY_LOGINFAIL_NOTICE_ENABLE.getName());
					AccountConfig lockTimes = accountConfigService.get(accountId, AccountConfigAttribute.SECURITY_LOGINFAIL_TRY_TIMES.getName());
					AccountConfig mail_enable = accountConfigService.get(accountId, AccountConfigAttribute.MAIL_STMP_ENABLE.getName());

					if (notice_enable != null && notice_enable.getValue().endsWith("true")) {
						if (lockTimes != null && userLocked.getLoginFailTimes() + 1 == Integer.parseInt(lockTimes.getValue())) {
							sendEmail(enterpriseUser);
							if (mail_enable.equals("true")) {
								sendMailByAccount(enterpriseUser, accountId);
							}
						}
					}

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (locked) {

				userLogService.saveUserLog(userLog, UserLogType.KEY_LOCK_USER, null);
				throw new UserLockedException("user is locked");
			}
		}
	}

	private void sendEmail(EnterpriseUser enterpriseUser) throws IOException {
		MailServer mailServer = mailServerService.getDefaultMailServer();
		if (mailServer == null) {
			throw new BusinessException();
		}

		Enterprise enterprise = enterpriseManager.getById(enterpriseUser.getEnterpriseId());
		String domainNm = enterprise.getDomainName();

		// added by Jeffrey for new requirements
		CustomizeLogo customizeLogo = customizeLogoService.getCustomize();
		String link = "";
		if (customizeLogo != null && StringUtils.isNotBlank(customizeLogo.getDomainName())) {
			link = customizeLogo.getDomainName();
		}

		Map<String, Object> messageModel = new HashMap<String, Object>(2);
		messageModel.put("loginname", HtmlUtils.htmlEscape(enterpriseUser.getName()).replaceAll(" ", "&nbsp;"));
		messageModel.put("password", HtmlUtils.htmlEscape(enterpriseUser.getPassword()));
		messageModel.put("name", HtmlUtils.htmlEscape(enterpriseUser.getAlias()).replaceAll(" ", "&nbsp;"));
		messageModel.put("domainName", HtmlUtils.htmlEscape(domainNm).replaceAll(" ", "&nbsp;"));
		messageModel.put("LoginAddress", HtmlUtils.htmlEscape(link).replaceAll(" ", "&nbsp;"));
		String msg = mailServerService.getEmailMsgByTemplate(Constants.OPEN_ACCOUNT_CONTENT, messageModel);
		String subject = mailServerService.getEmailMsgByTemplate(Constants.OPEN_ACCOUNT_SUBJECT, new HashMap<String, Object>(1));
		mailServerService.sendHtmlMail(enterpriseUser.getName(), mailServer.getId(), enterpriseUser.getEmail(), null, null, subject, msg);
	}

	private void sendMailByAccount(EnterpriseUser enterpriseUser, long accountId) throws IOException {

		MailServer mailServer = mailServerService.getByAccountId(accountId);
		if (mailServer == null) {
			throw new BusinessException();
		}

		Enterprise enterprise = enterpriseManager.getById(enterpriseUser.getEnterpriseId());
		String domainNm = enterprise.getDomainName();

		// added by Jeffrey for new requirements
		CustomizeLogo customizeLogo = customizeLogoService.getCustomize();
		String link = "";
		if (customizeLogo != null && StringUtils.isNotBlank(customizeLogo.getDomainName())) {
			link = customizeLogo.getDomainName();
		}

		Map<String, Object> messageModel = new HashMap<String, Object>(2);
		messageModel.put("loginname", HtmlUtils.htmlEscape(enterpriseUser.getName()).replaceAll(" ", "&nbsp;"));
		messageModel.put("password", HtmlUtils.htmlEscape(enterpriseUser.getPassword()));
		messageModel.put("name", HtmlUtils.htmlEscape(enterpriseUser.getAlias()).replaceAll(" ", "&nbsp;"));
		messageModel.put("domainName", HtmlUtils.htmlEscape(domainNm).replaceAll(" ", "&nbsp;"));
		messageModel.put("LoginAddress", HtmlUtils.htmlEscape(link).replaceAll(" ", "&nbsp;"));
		String msg = mailServerService.getEmailMsgByTemplate(Constants.OPEN_ACCOUNT_CONTENT, messageModel);
		String subject = mailServerService.getEmailMsgByTemplate(Constants.OPEN_ACCOUNT_SUBJECT, new HashMap<String, Object>(1));
		mailServerService.sendHtmlMail(enterpriseUser.getName(), mailServer.getId(), enterpriseUser.getEmail(), null, null, subject, msg);

	}

	private void handleNoneLdap(UserLog userLog, LoginInfo loginInfo, UserLocked userLocked, EnterpriseUser enterpriseUserByLdap, long accountId) {
		if (null == enterpriseUserByLdap) {
			userLogService.saveFailLog(userLog, UserLogType.KEY_GET_TOKEN_ERR, null);
			if (loginInfo.getUserId() != 0) {
				handleNoneLdap(loginInfo, userLocked, userLog, accountId);
				throw new LoginAuthFailedException("Local", "Local user login failed");
			}
			throw new LoginAuthFailedException("AD", "AD user login failed");
		}
	}

	private void fillResetPwdAndDeclare(String appId, UserLog userLog, UserToken userToken, RestLoginResponse restLoginResponse) {
		boolean needChangePassword = userAccountService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId());
		UserSignDeclare declare = new UserSignDeclare();
		declare.setAccountId(userToken.getAccountId());
		declare.setCloudUserId(userToken.getCloudUserId());
		declare.setClientType(getClietType(userLog.getClientType()));
		boolean needDeclaration = userSignDeclareManager.isNeedDeclaration(declare, appId);
		userToken.setNeedChangePassword(needChangePassword && userTokenHelper.needChangePwdFromConfig());
		userToken.setNeedDeclaration(needDeclaration);
		restLoginResponse.setNeedDeclaration(needDeclaration);
		restLoginResponse.setAppId(appId);
		restLoginResponse.setNeedChangePassword(needChangePassword && userTokenHelper.needChangePwdFromConfig());
	}

	private EnterpriseUserAccount getEnterpriseUserAccount(String appId, EnterpriseUser enterpriseUserByLdap) throws IOException {
		EnterpriseUserAccount enterpriseUserAccount = loginUpdateManagerImpl.save(enterpriseUserByLdap.getUserSource(), enterpriseUserByLdap.getObjectSid(), enterpriseUserByLdap.getEnterpriseId(), appId, enterpriseUserByLdap);
		if (enterpriseUserAccount.getAccountStatus() == UserAccount.INT_STATUS_DISABLE) {
			String msg = "account user status disabled accountid:" + enterpriseUserAccount.getAccountId() + " id:" + enterpriseUserAccount.getId();
			userLogService.saveFailLog(enterpriseUserAccount.getName(), appId, null, UserLogType.KEY_GET_TOKEN_ERR);
			throw new LoginAuthFailedException(msg);
		}
		return enterpriseUserAccount;
	}

	private String getClietType(int index) {
		Map<Integer, String> map = new HashMap<Integer, String>(4);
		map.put(CLIENT_TYPE_WEB, CLIENT_TYPE_WEB_STR);
		map.put(CLIENT_TYPE_PC, CLIENT_TYPE_PC_STR);
		map.put(CLIENT_TYPE_IOS, CLIENT_TYPE_IOS_STR);
		map.put(CLIENT_TYPE_ANDROID, CLIENT_TYPE_ANDROID_STR);
		return map.get(index);
	}

	@Override
	public RestLoginResponse robotLogin(HttpServletRequest request, RestRobotLoginRequest requestDomain, AuthApp authApp) throws IOException {

		String appId = requestDomain.getAppId();
		String loginName = requestDomain.getLoginName();
		String domainName = requestDomain.getDomain();
		UserLog userLog = UserLogType.getUserLog(request, appId, loginName, false);
		LoginInfo loginInfo = getLoginInfo(appId, loginName, domainName, request.getHeader("x-real-ip"), userLog);
		if (loginInfo == null) {
			throw new LoginAuthFailedException();
		}
		if (loginInfo.getUserId() != 0) {
			UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, loginInfo.getUserId());
			userLockService.createUserLocked(loginInfo.getUserId(), loginName, domainName, appId, tempLock);
		}
		return loginWithPassword(request, requestDomain, userLog, loginInfo);

	}

	@Override
	public RestLoginResponse loginWithWxUser(HttpServletRequest request, String unionId, String appId) throws IOException {
		// TODO Auto-generated method stub
		WxUser wxUser = wxUserService.getByUnionId(unionId);
		UserLog userLog = UserLogType.getUserLog(request, appId, wxUser.getNickName(), false);

		return loginWithWxUser(request, appId, wxUser, userLog);
	}

	private RestLoginResponse buildLoginResponse(String appId, EnterpriseUser user, UserLog userLog, EnterpriseUserAccount enterpriseUserAccount, UserToken userToken, RestTerminalRsp lastAccessTerminal) {
		RestLoginResponse restLoginResponse = RestLoginResponse.fillRestLoginResponse(userToken);
		Enterprise enterprise = enterpriseManager.getById(user.getEnterpriseId());
		Map<String, Long> bandWidthMap = userService.fillBandWidth(enterpriseUserAccount.getDownloadBandWidth(), enterpriseUserAccount.getUploadBandWidth(), appId);
		restLoginResponse.setUploadQos(bandWidthMap.get(UserServiceImpl.UPLOAD_BANDWIDTH));
		restLoginResponse.setDownloadQos(bandWidthMap.get(UserServiceImpl.DOWNLOAD_BANDWIDTH));
		restLoginResponse.setDomain(enterprise.getDomainName());
		userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
		userLogService.saveUserLog(userLog, UserLogType.KEY_GET_TOKEN, null);
		restLoginResponse.setLastAccessTerminal(lastAccessTerminal);
		restLoginResponse.setAlias(user.getAlias());
		restLoginResponse.setMobile(user.getMobile());
		restLoginResponse.setAppId(appId);
		restLoginResponse.setDomain(enterprise.getDomainName());
		restLoginResponse.setEnterpriseName(enterprise.getName());
		return restLoginResponse;
	}

	private long initPassWrodLevel(HttpServletRequest request, String password, EnterpriseUser enterpriseUserByLdap, RestLoginResponse restLoginResponse) {
		long enterpriseId = enterpriseUserByLdap.getEnterpriseId();
		String pwd_Level = enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId);
		int pwdLevel = 1;
		HttpSession session = request.getSession();
		if (!StringUtils.isBlank(pwd_Level)) {
			pwdLevel = Integer.parseInt(pwd_Level);
		}
		if (!PasswordValidateUtil.isValidPassword(password, pwdLevel)) {
			session.setAttribute("ChgPwd", true);
			restLoginResponse.setPwdLevel(pwdLevel + "");
		} else {
			session.setAttribute("ChgPwd", false);
		}
		return enterpriseId;
	}

	@Override
	public RestLoginResponse userLogin(HttpServletRequest request,
			RestWxworkWxMpUserLoginRequest login) throws IOException {
		String appId = login.getAppId();
		String corpId = login.getCorpId();
		String userId;
		
		WxworkWxMpUserInfo wxworkWxMpUserInfo = wxworkWxMpOauth2Proxy.getUserInfoByCode(corpId, login.getCode());
		userId = wxworkWxMpUserInfo.getUserId();
		LOGGER.error("========= userId: " + userId);
		
		WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
		if (wxEnterprise == null) {
			LOGGER.error("WxEnterprise doesn't exist: corpId={}, userId={}", corpId, userId);
			throw new LoginAuthFailedException("WxEnterprise doesn't exist, corpId: " + corpId + "; userId: " + userId);
		}

		if (wxEnterprise.getBoxEnterpriseId() == null) {
			LOGGER.error("No Enterprise Account bound: corpId={}, userId={}, enterpriseId=null.", corpId, userId);
			throw new LoginAuthFailedException("Enterprise Account doesn't exist, corpId: " + corpId + "; userId: " + userId);
		}

		long enterpriseId = wxEnterprise.getBoxEnterpriseId();
		EnterpriseUser user = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, userId);
		if (user == null) {
			LOGGER.error("WxEnterpriseUser doesn't exist: enterpriseId={}, userId={}", enterpriseId, userId);
			throw new LoginAuthFailedException("User doesn't exist, userId: " + userId + "; enterpriseId: " + enterpriseId);
		}

		long enterpriseUserId = user.getId();
		UserLocked tempLock = this.userLockService.getUserLockWithoutLock(appId, enterpriseUserId);
		userLockService.createUserLocked(enterpriseUserId, userId, corpId, appId, tempLock);

		// 记录登录信息
		UserLog userLog = UserLogType.getUserLog(request, appId, user.getAlias(), false);
		userLoginLogService.saveLog(userLog);

		// 使用原有的异常传递机制：异常由GlobalExceptionHandler抓获，然后统一响应。使用HTTP STATUS使用403
		return loginWithLockTrans(request, appId, user, userLog);
	}

	public void openAccount(WxUserInfo mpUserInfo, String inviterUnionId) {
		// TODO Auto-generated method stub
		WxUser wxUser = new WxUser();
		wxUser.setUnionId(mpUserInfo.getUnionid());
		wxUser.setOpenId(mpUserInfo.getOpenid());
		if (mpUserInfo.getCountry() != null && mpUserInfo.getCountry().equals("CN")) {
			try {
				wxUser.setNickName(new String(mpUserInfo.getNickname().getBytes("ISO-8859-1"), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			wxUser.setNickName(mpUserInfo.getNickname());
		}
		wxUser.setGender(mpUserInfo.getSex());
		wxUser.setCountry(mpUserInfo.getCountry());
		wxUser.setProvince(mpUserInfo.getProvince());
		wxUser.setCity(mpUserInfo.getCity());
		wxUser.setLanguage(mpUserInfo.getCountry());
		wxUser.setAvatarUrl(mpUserInfo.getHeadimgurl());
		wxUser.setEmail(new Date().getTime() + "@filepro.cn");
		wxUser.setStatus(WxUser.STATUS_NORMAL);
		wxUser.setType((byte) 0);
		wxUser.setInviterId(inviterUnionId);
		wxUser.setShareLevel(1);
		wxUserManager.openAccount(wxUser);

	}

}
