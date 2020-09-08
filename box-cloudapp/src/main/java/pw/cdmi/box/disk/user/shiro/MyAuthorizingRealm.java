package pw.cdmi.box.disk.user.shiro;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.authserver.service.AuthServerService;
import pw.cdmi.box.disk.enterprise.service.EnterpriseService;
import pw.cdmi.box.disk.enterprisecontrol.EnterpriseAuthControlManager;
import pw.cdmi.box.disk.enterprisecontrol.impl.EnterpriseAuthControlManagerImpl;
import pw.cdmi.box.disk.event.domain.EventType;
import pw.cdmi.box.disk.httpclient.rest.request.*;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.box.disk.user.domain.Terminal;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.exception.*;

public class MyAuthorizingRealm extends AuthorizingRealm {

	private static String algorithm = "SHA-256";

	private static Logger logger = LoggerFactory.getLogger(MyAuthorizingRealm.class);

	private static final int INITIAL_SIZE = 10;

	private String realmName;

	private UserLoginService userLoginService;

	private UserService userService;

	private AuthAppService authAppService;

	private EnterpriseAuthControlManager enterpriseAuthControlManager;

	private AuthServerService authServerService;

	private EnterpriseService enterpriseService;

	public MyAuthorizingRealm() {
		setRealmName(getName());
	}

	@PostConstruct
	public void initCredentialsMatcher() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(algorithm);
		setCredentialsMatcher(matcher);
	}

	public void setEnterpriseAuthControlManager(EnterpriseAuthControlManager enterpriseAuthControlManager) {
		this.enterpriseAuthControlManager = enterpriseAuthControlManager;
	}

	public void setUserLoginService(UserLoginService userLoginService) {
		this.userLoginService = userLoginService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public AuthAppService getAuthAppService() {
		return authAppService;
	}

	public void setAuthAppService(AuthAppService authAppService) {
		this.authAppService = authAppService;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AuthServer authServer = (AuthServer) obj;
		if (!AuthServer.AUTH_TYPE_LOCAL.equals(authServer.getType())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		long result = 17;
		result = 17 * result + AuthServer.AUTH_TYPE_LOCAL.hashCode();
		if (result > Integer.MAX_VALUE) {
			return (int) (result % Integer.MAX_VALUE);
		}
		return (int) result;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		if(authcToken instanceof WxUserToken) {
			return doGetAuthenticationInfo((WxUserToken) authcToken);
		} else if(authcToken instanceof WxWorkUserToken) {
			return doGetAuthenticationInfo((WxWorkUserToken) authcToken);
		} else {
			return doGetAuthenticationInfo((UsernamePasswordCaptchaToken) authcToken);
		}
	}

	protected AuthenticationInfo doGetAuthenticationInfo(UsernamePasswordCaptchaToken token) throws AuthenticationException {
		String loginName = token.getUsername();
		String password = new String(token.getPassword());
		String deviceAgent = token.getDeviceAgent();
		String deviceOS = token.getDeviceOS();
		String deviceAddress = token.getDeviceAddress();
		String authToken = token.getToken();
		String refreshToken = token.getRefreshToken();
		long enterpriseId = token.getEnterpriseId();
		long accountId = token.getAccountId();
		Date expire = token.getExpired();

		checkPrincipalValid(token, loginName, password);

		UserToken userToken = new UserToken();
		userToken.setDeviceType(Terminal.CLIENT_TYPE_WEB);
		userToken.setDeviceOS(deviceOS);
		userToken.setDeviceAgent(deviceAgent);
		userToken.setDeviceAddress(deviceAddress);
		try {
			if (!token.isNtlm()) {
				UserLoginRequest userLoginRequest = new UserLoginRequest();

				String domainName = "";
				if (token.getOwnerDomain() != null && !token.getOwnerDomain().equals("")) {
					domainName = token.getOwnerDomain();
				} else {
					Map<String, String> map = enterpriseAuthControlManager.getWebDomainLoginName(loginName);
					loginName = map.get(EnterpriseAuthControlManagerImpl.LOGIN_NAME_KEY);
					domainName = map.get(EnterpriseAuthControlManagerImpl.DOMAIN_NAME_KEY);
				}

				//没有使用domain登录，检查企业名称
				if(StringUtils.isBlank(domainName)) {
					String enterpriseName = token.getEnterpriseName();
					Enterprise enterprise = enterpriseService.getByName(enterpriseName);
					if(enterprise != null) {
						domainName = enterprise.getDomainName();
					} else {
						logger.error("login fail, no such enterprise: {}", enterpriseName);
						throw new NoSuchEnterpriseException("no such enterprise");
					}
				}

				SecurityUtils.getSubject().getSession().setAttribute("tag", true);

				String appId = authAppService.getCurrentAppId();
				userLoginRequest.setAppId(appId);
				userLoginRequest.setLoginName(loginName);
				userLoginRequest.setPassword(password);
				userLoginRequest.setDomain(domainName);

				RestLoginResponse restLoginResponse = userLoginService.checkFormUser(userLoginRequest, userToken,token.getRegionIp());
				// 密码级别提高，强制修改密码
				if (restLoginResponse.isNeedChangePassword()) {
					userToken.setNeedChangePassword(true);
					userToken.setPwdLevel(restLoginResponse.getPwdLevel());
					SecurityUtils.getSubject().getSession().setAttribute("isNeedChangePassword","true");
				}
				transRestLoginResponse(userToken, restLoginResponse);
				authToken = restLoginResponse.getToken();
				refreshToken = restLoginResponse.getRefreshToken();
				expire = new Date(System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L);
				initLastLoginInfo(restLoginResponse);
			} else {
				userService.getUserTokenBydb(userToken, loginName, enterpriseId, accountId);
			}
		
			userToken.setPassword(password);
			userToken.setOldPassword(password);
			userToken.setToken(authToken);
			userToken.setExpiredAt(expire);
		} catch (UserLockedException e) {
			logger.debug("user locked.", e);
			throw new LockedAccountException(e.getMsg(), e);
		} catch (DisabledUserApiException e) {
			logger.debug("user disabled.", e);
			throw new IncorrectCredentialsException("user disabled.", e);
		} catch (SecurityMartixException e) {
			logger.debug("Security fails, the SecurityMartix deny the request.", e);
			throw new SecurityMartixException("Security fails, the SecurityMartix deny the request.", e);
		} catch (LdapLoginAuthFailedException e) {
			logger.error("AD user authentication fails, the user name or password is incorrect.", e);
			throw new ADLoginAuthFailedException(e);
		} catch (AuthenticationException e) {
			logger.error("Authentication fails.", e);
			throw e;
		} catch (Exception e) {
			logger.error("Authentication fails.", e);
			throw new IncorrectCredentialsException("Authentication fails, the user name or password is incorrect.", e);
		}

		//
		if (StringUtils.isBlank(password)) {
			try {
				userToken.setPassword(digest("".getBytes("utf8"), algorithm));
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException:", e);
			}
		} else {
			try {
				userToken.setPassword(digest(password.getBytes("utf8"), algorithm));
			} catch (UnsupportedEncodingException e) {
				logger.error("UnsupportedEncodingException:", e);
			}
		}
		if (!token.isNtlm()) {
			renewSession();
		}
		SecurityUtils.getSubject().getSession().setAttribute(User.SESSION_ID_KEY, userToken.getId());
		SecurityUtils.getSubject().getSession().setAttribute("platToken", authToken);
		SecurityUtils.getSubject().getSession().setAttribute("platRefrshToken", refreshToken);
		SecurityUtils.getSubject().getSession().setAttribute("expiredTokenTime", expire);
		SecurityUtils.getSubject().getSession().setAttribute("accountId", userToken.getAccountId());
		SecurityUtils.getSubject().getSession().setAttribute("deviceAddress", deviceAddress);
		EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(userToken.getAccountId(), userToken.getId());
		AuthServer authServer = authServerService.getAuthServer(enterpriseUser.getUserSource());
		SecurityUtils.getSubject().getSession().setAttribute("isLocalAuth", AuthServer.AUTH_TYPE_LOCAL.equals(authServer.getType()));
		SecurityUtils.getSubject().getSession().setAttribute("loadTag", true);
		userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
		logger.info("[loginLog] end login  ");
		return new SimpleAuthenticationInfo(userToken, userToken.getPassword().toCharArray(), getName());
	}

    protected AuthenticationInfo doGetAuthenticationInfo(WxWorkUserToken token) throws AuthenticationException {
        String deviceAgent = token.getDeviceAgent();
        String deviceOS = token.getDeviceOS();
        String deviceAddress = token.getDeviceAddress();

        WxWorkUser userToken = new WxWorkUser();
        userToken.setDeviceType(Terminal.CLIENT_TYPE_WEB);
        userToken.setDeviceOS(deviceOS);
        userToken.setDeviceAgent(deviceAgent);
        userToken.setDeviceAddress(deviceAddress);
        try {
            String appId = authAppService.getCurrentAppId();

            SecurityUtils.getSubject().getSession().setAttribute("tag", true);

			WxWorkUserLoginRequest userLoginRequest = new WxWorkUserLoginRequest();
            userLoginRequest.setAppId(appId);
            userLoginRequest.setCorpId(token.getCorpId());
            userLoginRequest.setCode(token.getCode());
            userLoginRequest.setAuthCode(token.getAuthCode());

            RestLoginResponse restLoginResponse = userLoginService.checkFormUser(userLoginRequest, userToken, token.getRegionIp());
            transRestLoginResponse(userToken, restLoginResponse);
			initLastLoginInfo(restLoginResponse);
			userToken.setToken(restLoginResponse.getToken());
			userToken.setRefreshToken(restLoginResponse.getRefreshToken());
			userToken.setExpiredAt(new Date(System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L));
        } catch (UserLockedException e) {
            logger.debug("user locked. corpId={}", token.getCorpId());
            throw new LockedAccountException(e.getMsg(), e);
        } catch (DisabledUserApiException e) {
            logger.debug("user disabled. corpId={}", token.getCorpId());
            throw new WxWorkAuthFailedException("user disabled.");
        } catch (SecurityMartixException e) {
            logger.debug("Security fails, the SecurityMartix deny the request. corpId={}", token.getCorpId());
            throw new SecurityMartixException("Security fails, the SecurityMartix deny the request.", e);
        } catch (LdapLoginAuthFailedException e) {
            logger.error("AD user authentication fails, the user name or password is incorrect. corpId={}", token.getCorpId());
            throw new ADLoginAuthFailedException(e);
        } catch (Exception e) {
            logger.error("Authentication fails, corpId={}, code={}", token.getCorpId(), token.getCode());
            throw new WxWorkAuthFailedException("Authentication fails.");
        }

//		renewSession();

        SecurityUtils.getSubject().getSession().setAttribute(User.SESSION_ID_KEY, userToken.getId());
        SecurityUtils.getSubject().getSession().setAttribute("platToken", userToken.getToken());
        SecurityUtils.getSubject().getSession().setAttribute("platRefrshToken", userToken.getRefreshToken());
        SecurityUtils.getSubject().getSession().setAttribute("expiredTokenTime", userToken.getExpiredAt());
        SecurityUtils.getSubject().getSession().setAttribute("accountId", userToken.getAccountId());
        SecurityUtils.getSubject().getSession().setAttribute("deviceAddress", deviceAddress);
        EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(userToken.getAccountId(), userToken.getId());
        AuthServer authServer = authServerService.getAuthServer(enterpriseUser.getUserSource());
        SecurityUtils.getSubject().getSession().setAttribute("isLocalAuth", AuthServer.AUTH_TYPE_LOCAL.equals(authServer.getType()));
        SecurityUtils.getSubject().getSession().setAttribute("loadTag", true);
        userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
        logger.info("[loginLog] end login  ");

        return new SimpleAuthenticationInfo(userToken, userToken.getLoginName(), getName());
    }

    protected AuthenticationInfo doGetAuthenticationInfo(WxUserToken token) throws AuthenticationException {
        String deviceAgent = token.getDeviceAgent();
        String deviceOS = token.getDeviceOS();
        String deviceAddress = token.getDeviceAddress();

        WxUser wxUser = new WxUser();
        wxUser.setDeviceType(Terminal.CLIENT_TYPE_WEB);
        wxUser.setDeviceOS(deviceOS);
        wxUser.setDeviceAgent(deviceAgent);
        wxUser.setDeviceAddress(deviceAddress);
        try {
            String appId = authAppService.getCurrentAppId();

            SecurityUtils.getSubject().getSession().setAttribute("tag", true);

            WxUserLoginRequest userLoginRequest = new WxUserLoginRequest();
            userLoginRequest.setAppId(appId);
            userLoginRequest.setCode(token.getCode());
			userLoginRequest.setEnterpriseId(token.getEnterpriseId());
			userLoginRequest.setIdentity(token.getIdentity());

			RestUserLoginResponse restLoginResponse = (RestUserLoginResponse)userLoginService.checkFormUser(userLoginRequest, wxUser, token.getRegionIp());
			//如果响应中企业列表不为空，说明需要用户选择企业，然后才能登录，此时返回不完整的UserToken当principal
			if(restLoginResponse.getEnterpriseList() != null) {
				wxUser.setCode(token.getCode());
				wxUser.setEnterpriseList(restLoginResponse.getEnterpriseList());
				SecurityUtils.getSubject().getSession().setAttribute(User.SESSION_ID_KEY, "100");

				return new SimpleAuthenticationInfo(wxUser, restLoginResponse.getLoginName(), getName());
			}

            transRestLoginResponse(wxUser, restLoginResponse);
			initLastLoginInfo(restLoginResponse);
			wxUser.setToken(restLoginResponse.getToken());
			wxUser.setRefreshToken(restLoginResponse.getRefreshToken());
			wxUser.setExpiredAt(new Date(System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L));
        } catch (UserLockedException e) {
            logger.debug("user locked.", e);
            throw new LockedAccountException(e.getMsg(), e);
        } catch (DisabledUserApiException e) {
            logger.debug("user disabled.", e);
            throw new WxAuthFailedException("user disabled.");
        } catch (SecurityMartixException e) {
            logger.debug("Security fails, the SecurityMartix deny the request.", e);
            throw new SecurityMartixException("Security fails, the SecurityMartix deny the request.", e);
        } catch (LdapLoginAuthFailedException e) {
            logger.error("AD user authentication fails, the user name or password is incorrect.", e);
            throw new ADLoginAuthFailedException(e);
        } catch (Exception e) {
            logger.error("Authentication fails.", e);
            throw new WxAuthFailedException("Authentication fails.");
        }

//		renewSession();

        SecurityUtils.getSubject().getSession().setAttribute(User.SESSION_ID_KEY, wxUser.getId());
        SecurityUtils.getSubject().getSession().setAttribute("platToken", wxUser.getToken());
        SecurityUtils.getSubject().getSession().setAttribute("platRefrshToken", wxUser.getRefreshToken());
        SecurityUtils.getSubject().getSession().setAttribute("expiredTokenTime", wxUser.getExpiredAt());
        SecurityUtils.getSubject().getSession().setAttribute("accountId", wxUser.getAccountId());
        SecurityUtils.getSubject().getSession().setAttribute("deviceAddress", deviceAddress);
        if(wxUser.getAccountId()!=0){
            EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(wxUser.getAccountId(), wxUser.getId());
            AuthServer authServer = authServerService.getAuthServer(enterpriseUser.getUserSource());
            SecurityUtils.getSubject().getSession().setAttribute("isLocalAuth", AuthServer.AUTH_TYPE_LOCAL.equals(authServer.getType()));
            SecurityUtils.getSubject().getSession().setAttribute("loadTag", true);
        }

        userService.createEvent(wxUser, EventType.USER_LOGIN, wxUser.getId());
        logger.info("[loginLog] end login  ");

        return new SimpleAuthenticationInfo(wxUser, wxUser.getLoginName(), getName());
    }

	private void initLastLoginInfo(RestLoginResponse restLoginResponse) {
		if (null == restLoginResponse.getLastAccessTerminal()) {
			return;
		}
		if (null == restLoginResponse.getLastAccessTerminal().getLastAccessIP()) {
			return;
		}
		if (null == restLoginResponse.getLastAccessTerminal().getLastAccessAt()) {
			return;
		}
		if (null == restLoginResponse.getLastAccessTerminal().getDeviceType()) {
			return;
		}
		SecurityUtils.getSubject().getSession().setAttribute("lastLoginTime",
				restLoginResponse.getLastAccessTerminal().getLastAccessAt());
		SecurityUtils.getSubject().getSession().setAttribute("lastLoginIP",
				restLoginResponse.getLastAccessTerminal().getLastAccessIP());
		SecurityUtils.getSubject().getSession().setAttribute("terminalType",
				restLoginResponse.getLastAccessTerminal().getDeviceType());
	}

	private void checkPrincipalValid(UsernamePasswordCaptchaToken token, String loginName, String password) {
		if (StringUtils.isBlank(loginName) || (StringUtils.isBlank(password) && !token.isNtlm())) {
			logger.error("Null usernames are not allowed by this realm.");
			throw new UnknownAccountException("Null usernames are not allowed by this realm.");
		}
		if (loginName.length() > 255 || (!token.isNtlm() && password.length() > 127)) {
			logger.error("username or password out of range.");
			throw new UnknownAccountException("Null usernames are not allowed by this realm.");
		}
	}

	@Override
	protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
		//微信登录，不检查是否匹配, 其他情况继续原来的流程
		if(!(token instanceof WxWorkUserToken) && !(token instanceof WxUserToken)) {
			super.assertCredentialsMatch(token, info);
		}
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Object obj = principals.fromRealm(getName()).iterator().next();
		if (obj == null) {
			return null;
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		info.addRole("user");
		return info;
	}

	/**
	 * 
	 * @param input
	 * @param algorithm
	 * @return
	 */
	private String digest(byte[] input, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] result = digest.digest(input);
			return Hex.encodeToString(result);
		} catch (GeneralSecurityException e) {
			logger.error("Error in digest password!", e);
			return null;
		}
	}

	private void transRestLoginResponse(UserToken userToken, RestLoginResponse restLoginResponse) {
		userToken.setLoginName(restLoginResponse.getLoginName());
		userToken.setCloudUserId(restLoginResponse.getCloudUserId());
		userToken.setEnterpriseId(restLoginResponse.getEnterpriseId());
		userToken.setAccountId(restLoginResponse.getAccountId());
		userToken.setName(restLoginResponse.getAlias());
		userToken.setId(restLoginResponse.getUserId());
		userToken.setType(restLoginResponse.getType());
		userToken.setStaffLevel(restLoginResponse.getStaffLevel());
		userToken.setMobile(restLoginResponse.getMobile());
		userToken.setDomain(restLoginResponse.getDomain());
		userToken.setAppId(restLoginResponse.getAppId());
		userToken.setWxCloudUserId(restLoginResponse.getWxCloudUserId());
		userToken.setIsAdmin(restLoginResponse.getIsAdmin());
	}

	private void renewSession() {
		Session sessionOld = SecurityUtils.getSubject().getSession(false);
		if (sessionOld != null) {
			Map<Object, Object> tmp = new HashMap<Object, Object>(INITIAL_SIZE);
			for (Object key : sessionOld.getAttributeKeys()) {
				tmp.put(key, sessionOld.getAttribute(key));
			}
			SecurityUtils.getSubject().logout();
			Session sessionNew = SecurityUtils.getSubject().getSession(true);
			for (Entry<Object, Object> entry : tmp.entrySet()) {
				sessionNew.setAttribute(entry.getKey(), entry.getValue());
			}
		}
	}

	public AuthServerService getAuthServerService() {
		return authServerService;
	}

	public void setAuthServerService(AuthServerService authServerService) {
		this.authServerService = authServerService;
	}

	public String getRealmName() {
		return realmName;
	}

	private void setRealmName(String realName) {
		realmName = realName;
	}

	public EnterpriseService getEnterpriseService() {
		return enterpriseService;
	}

	public void setEnterpriseService(EnterpriseService enterpriseService) {
		this.enterpriseService = enterpriseService;
	}
}
