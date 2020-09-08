package com.huawei.sharedrive.app.oauth2.service;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountAccessKey;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.account.mamager.AccountAccessKeyManager;
import com.huawei.sharedrive.app.account.mamager.AccountManager;
import com.huawei.sharedrive.app.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.oauth2.domain.DataServerToken;
import com.huawei.sharedrive.app.oauth2.domain.PreviewObjectToken;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.INodeLinkDynamic;
import com.huawei.sharedrive.app.share.domain.LinkAccessCodeMode;
import com.huawei.sharedrive.app.share.service.LinkServiceV2;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.RandomKeyGUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.Resource;
import java.util.*;

@Component("userTokenHelper")
public class UserTokenHelper {
	public static enum TokenType {
		CreatedForDataServer, MessageListener, RefreshAble;
	}

	/** account 鉴权前缀 */
	public final static String APP_ACCOUNT_PREFIX = "account";

	/** account 鉴权前缀（old） */
	public final static String APP_PREFIX = "app,";

	/** 应用鉴权前缀 */
	public final static String APPLICATION_PREFIX = "application,";

	/** DC 回调返回的参数 */
	public final static String CALLBACK_TYPE_OBJECT = "Object";

	public final static String CALLBACK_TYPE_PREVIEW_OBJECT = "PreviewObject";

	public final static String KEY_CALLBACK_ACCOUNT_ID = "accountId";

	public final static String KEY_CALLBACK_CONVERT_REAL_START_TIME = "convertRealStartTime";

	public final static String KEY_CALLBACK_LAST_MODIFIED = "nodeLastModified";

	public final static String KEY_CALLBACK_OWNERID = "ownerId";

	public final static String KEY_CALLBACK_RESOURCE_GROUP_ID = "resourceGroupId";

	public final static String KEY_CALLBACK_SOURCE_OBJECT_ID = "sourceObjectId";

	public final static String KEY_CALLBACK_TYPE = "type";

	/** 应用鉴权前缀 */
	public final static String LINK_PREFIX = "link,";

	private static final ThreadLocal<UserToken> CURRENT_TOKEN = new ThreadLocal<>();

	private static final int DEFAULT_ACCESS_CODE_EXPIRE_TIME = 1800;

	/** 应用Authorization的数组长度 */
	private final static int LENG_ARRAY_APP = 3;

	/** 应用账户鉴权cloudUserId设置为-1 */
	public final static Long ACCOUNT_CLOUD_USER_ID = -1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenHelper.class);

	private static final String UAS_REGION_NAME = PropertiesUtils.getProperty("uas.region.name", null,
			PropertiesUtils.BundleName.HWIT);

	@Autowired
	private AccountAccessKeyManager accessKeyManager;

	@Autowired
	private AccountIdAdapter accountIdAdapter;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private AppAccessKeyService appAccessKeyService;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private LinkServiceV2 linkServiceV2;

	@Value("${auth2.token.message.listener.expire.second}")
	private int messageListenerExpired;

	/** Token 刷新间隔 */
	@Value("${auth2.token.refresh.interval.second}")
	private int refreshInterval;

	@Autowired
	private SystemConfigDAO systemConfigDAO;

	/** temp token失效时间 */
	@Value("${auth2.token.temp.expire.second}")
	private long tempTokenExpired;

	@Resource
	private RestClient uamClientService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserTokenService userTokenService;

	public void assembleUserToken(Long ownerId, UserToken userToken) {
		if (userToken == null) {
			return;
		}
		try {
			User user = userService.get(null, ownerId);
			if (user != null) {
				userToken.setAppId(user.getAppId());
				userToken.setRegionId(user.getRegionId());
				userToken.setAccountId(user.getAccountId());
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public Account checkAccountToken(String authorization, String date) {
		if (!authorization.startsWith(APP_ACCOUNT_PREFIX) && !authorization.startsWith(APP_PREFIX)) {
			throw new AuthFailedException("Bad app authorization: " + authorization);
		}
		return accountToken(authorization, date);
	}

	public String checkAppAndAccountToken(String authorization, String date) {
		if (StringUtils.isBlank(authorization)) {
			throw new AuthFailedException("Bad app authorization: " + authorization);
		}
		if (authorization.startsWith(APPLICATION_PREFIX)) {
			return appToken(authorization, date);
		} else if (authorization.startsWith(APP_ACCOUNT_PREFIX) || authorization.startsWith(APP_PREFIX)) {
			String[] strArr = TokenChecker.checkLength(authorization);
			AccountAccessKey accessKey = null;
			try {
				accessKey = accessKeyManager.getAccountKeyById(null, StringUtils.trimToEmpty(strArr[1]));
				checkSignature(accessKey.getSecretKey(), date, strArr[2]);
			} catch (Exception e) {
				throw new AuthFailedException(e);
			}
			return null;
		} else {
			throw new AuthFailedException("Bad app authorization: " + authorization);
		}
	}

	public String checkAppSystemToken(String authorization, String date) {
		if (StringUtils.isBlank(authorization)) {
			throw new AuthFailedException("Bad app authorization: " + authorization);
		}
		if (!authorization.startsWith(APPLICATION_PREFIX)) {
			throw new AuthFailedException("Bad app authorization: " + authorization);
		}
		return appToken(authorization, date);
	}

	/**
	 * 校验外链权限
	 * 
	 * @param appToken
	 * @throws BaseRunException
	 */
	public INodeLink checkLinkToken(String str, String date) throws BaseRunException {
		if (!str.startsWith(LINK_PREFIX)) {
			throw new AuthFailedException("Bad link string " + str);
		}
		String[] strArr = str.split(",");
		if (strArr.length != LENG_ARRAY_APP && strArr.length != 2) {
			throw new AuthFailedException("Bad link string " + str);
		}
		INodeLink link = linkServiceV2.getLinkByLinkCodeForClient(StringUtils.trimToEmpty(strArr[1]));
		if (null == link) {
			throw new NoSuchLinkException("Can not find the link " + str);
		}
		if (StringUtils.isEmpty(link.getPlainAccessCode())
				&& link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
			return link;
		}
		if (strArr.length != 3) {
			throwLinkException(str, link.getStatus());
		}
		if (link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
			checkSignature(link.getPlainAccessCode(), date, strArr[2]);
		} else {
			checkDynamicLinkSignature(date, strArr, link.getStatus());
		}

		return link;
	}

	/**
	 * 校验外链权限
	 * 
	 * @param appToken
	 * @throws BaseRunException
	 */
	public INodeLink checkLinkTokenForWeb(String authorization, String date) throws BaseRunException {
		if (!authorization.startsWith(LINK_PREFIX)) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}
		String[] strArr = authorization.split(",");
		if (strArr.length != LENG_ARRAY_APP && strArr.length != 2) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}
		INodeLink link = linkServiceV2.getLinkByLinkCodeForClient(StringUtils.trimToEmpty(strArr[1]));
		if (null == link) {
			throw new NoSuchLinkException("Can not find the link" + authorization);
		}
		if (StringUtils.isEmpty(link.getPlainAccessCode())
				&& link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
			return link;
		}
		if (strArr.length != 3) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}
		try {
			if (link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
				checkSignature(link.getPlainAccessCode(), date, strArr[2]);
			} else {
				checkDynamicLinkSignature(date, strArr, link.getStatus());
			}
			return link;
		} catch (RuntimeException e) {
			LOGGER.info(e.getMessage());
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}
		INodeLink result = new INodeLink(link.getId(), link.getAccess(), link.getStatus(), link.getUrl());
		return result;

	}

	public UserToken checkMessageListenToken(String token, String receiverId, AuthorityMethod method) {
		DataServerToken tokenValue = userTokenService.getUserToken(token);
		if (tokenValue instanceof UserToken) {
			boolean authorized = isAuthorizedRequest(new Authorize(method, receiverId), tokenValue, token);
			if (!authorized) {
				throw new AuthFailedException();
			}
			tokenValue.setToken(token);
			return (UserToken) tokenValue;
		}
		LOGGER.error("token exists, but its type is " + tokenValue.getClass().getName());
		throw new AuthFailedException();
	}

	/**
	 * 获取Token 相关的用户。
	 * 
	 * @param token
	 * @return
	 * @throws BaseRunException
	 */
	public DataServerToken checkTokenAndGetUser(String token, String objectId, AuthorityMethod method)
			throws BaseRunException {
		DataServerToken tokenValue = userTokenService.getUserToken(token);
		boolean authorized = isAuthorizedRequest(new Authorize(method, objectId), tokenValue, token);
		if (!authorized) {
			throw new AuthFailedException();
		}
		tokenValue.setToken(token);
		return tokenValue;
	}

	/**
	 * Auth <br/>
	 * 如果找不到数据，抛出异常，不可能返回null
	 * 
	 * @param auth
	 * @return
	 * @throws BaseRunException
	 */
	public UserToken checkTokenAndGetUserForV2(String authToken, Map<String, String> customHeaderMap) throws AuthFailedException {
		//本地配置了ecm地址，直接使用此地址
		String authUrl = PropertiesUtils.getProperty("token.check.address.uam", null);
		if (StringUtils.isBlank(authUrl)) {
			//查询App定义的Auth URL.
			String appId = getAppIdFromToken(authToken);
			AuthApp authApp = authAppService.getByAuthAppID(appId);
			if (authApp == null) {
				LOGGER.error("authApp is null, appId:{}", appId);
				throw new AuthFailedException();
			}

			authUrl = authApp.getAuthUrl();
		}

		if (StringUtils.isEmpty(authUrl)) {
			throw new AuthFailedException();
		}

		if (!authUrl.endsWith("/")) {
			authUrl = authUrl + '/';
		}

		Map<String, String> headerMap = new HashMap<>(BusinessConstants.INITIAL_CAPACITIES);
		headerMap.put("Authorization", authToken);
		if (null != customHeaderMap) {
			headerMap.putAll(customHeaderMap);
		}

		TextResponse response = uamClientService.performGetTextByUri(authUrl + "api/v2/users/me", headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String content = response.getResponseBody();
			UserToken rspUser = JsonUtils.stringToObject(content, UserToken.class);

			if (rspUser != null) {
				// l90003768 在ufm中，用户的id和cloudId一致
				rspUser.setId(rspUser.getCloudUserId());
				rspUser.setToken(authToken);

				if (rspUser.getAccountId() != null && rspUser.getAccountId() != 0) {
					Account account = accountManager.getById(rspUser.getAccountId());
					rspUser.setAccountVistor(account);
				}

				setCurrentToken(rspUser);

				// 兼容老版本UAM，老版本UAM没有多企业，返回的accountID为空,需要自己查询数据库，此场景数据库中应该只有一条记录
				if (rspUser.getAccountId() == null) {
					rspUser.setAccountId(accountIdAdapter.getAccountId(rspUser.getAppId()));
				}
				if (rspUser.isNeedDeclaration()) {
					throw new DeclarationException(StringUtils.trimToEmpty(response.getResponseBody()));
				}
				if (rspUser.isNeedChangePassword()) {
					throw new PasswordInitException(StringUtils.trimToEmpty(response.getResponseBody()));
				}

				return rspUser;
			}
			throw new InternalServerErrorException("System exception, user is null");
		}
		throw new AuthFailedException(StringUtils.trimToEmpty(response.getResponseBody()));
	}

	public UserToken getUserToken(String token, String date) {
		UserToken userToken;
		if (token.startsWith(UserTokenHelper.APP_PREFIX) || token.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX)) {
			Account account = checkAccountToken(token, date);
			userToken = new UserToken();
			userToken.setAppId(account.getAppId());
			userToken.setId(User.APP_USER_ID);
			userToken.setCloudUserId(UserTokenHelper.ACCOUNT_CLOUD_USER_ID);
			userToken.setAccountVistor(account);
		} else {
			userToken = checkTokenAndGetUserForV2(token, null);
		}

		return userToken;
	}

	public void checkUserStatus(long userId) {
		User user = userService.get(userId);
		if (null == user || user.getStatus().equals(User.USER_DELETING)) {
			throw new NoSuchUserException("No such user: " + userId);
		}
		if (user.getStatus().equals(User.STATUS_DISABLE_INTEGER)) {
			throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
		}
		Account account = accountManager.getById(user.getAccountId());
		if (AccountConstants.STATUS_DISABLE == account.getStatus()) {
			throw new InvalidSpaceStatusException("Bad account status: " + account.getStatus());
		}
	}

	// 获取 用户类型
	public byte getUserType(long userId) {
		User user = userService.get(userId);
		if (null == user) {
			return -1;
		}
		return user.getType();
	}

	public void checkUserStatus(String appId, long userId) {
		User user = userService.get(userId);
		if (null == user || user.getStatus().equals(User.USER_DELETING)) {
			throw new NoSuchUserException("No such user:" + userId);
		}
		if (user.getStatus().equals(User.STATUS_DISABLE_INTEGER)) {
			throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
		}
		if(user.getAccountId()!=null&&user.getAccountId()!=0){
			Account account = accountManager.getById(user.getAccountId());
			if (AccountConstants.STATUS_DISABLE == account.getStatus()) {
				throw new InvalidSpaceStatusException("Bad account status: " + account.getStatus());
			}
		}
	}

	public void checkUserStatusAndSpace(long userId) {
		User user = userService.get(null, userId);
		if (null == user) {
			throw new NoSuchUserException("No such user:" + userId);
		}
		if (StringUtils.equals(User.STATUS_DISABLE_INTEGER, user.getStatus())) {
			throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
		}
		if (User.STATUS_TEAMSPACE_INTEGER == user.getType()) {
			throw new NoSuchUserException();
		}
	}

	public void clearCurrentToken() {
		CURRENT_TOKEN.remove();
	}

	/**
	 * 给客户端登陆用户创建Token.
	 * 
	 * @param user
	 * @return
	 * @throws InternalServerErrorException
	 */
	public void createToken(UserToken userToken) throws InternalServerErrorException {

		Authorize authorize = new Authorize();
		userToken.setAuth(authorize.toString());
		userToken.setTokenType(TokenType.RefreshAble.name());
		String token = RandomKeyGUID.getSecureRandomGUID();
		userToken.setToken(token);
		Calendar now = Calendar.getInstance();
		userToken.setCreatedAt(now.getTime());
		now.add(Calendar.SECOND, refreshInterval);
		userToken.setExpiredAt(now.getTime());
		userToken.setRefreshToken(RandomKeyGUID.getSecureRandomGUID());
		userTokenService.saveUserToken(userToken);
	}

	/**
	 * 为预览对象上传创建token
	 * 
	 * @param sourceObjectId
	 * @param accountId
	 * @param convertRealStartTime
	 * @param method
	 * @param storageObjectId
	 * @param resourceGroupId
	 * @param blockMd5
	 * @return
	 * @throws InternalServerErrorException
	 */
	public PreviewObjectToken createTokenDataServer(AuthorityMethod method, String storageObjectId, PreviewObjectToken previewObjectToken) throws InternalServerErrorException {

		Date expireTime = new Date(System.currentTimeMillis() + tempTokenExpired);
		Authorize authorize = new Authorize(method, storageObjectId, 0);
		PreviewObjectToken tempPreviewObjectToken = new PreviewObjectToken(TokenType.CreatedForDataServer.name(),
				authorize.toString(), RandomKeyGUID.getSecureRandomGUID(), expireTime,
				previewObjectToken.getSourceObjectId(), previewObjectToken.getAccountId(),
				previewObjectToken.getConvertRealStartTime(), previewObjectToken.getResourceGroupId());
		userTokenService.saveUserToken(tempPreviewObjectToken);
		return tempPreviewObjectToken;
	}

	/**
	 * 给DataServer 创建Token。
	 * 
	 * @param user
	 * @param method
	 * @param objectId
	 * @return
	 * @throws InternalServerErrorException
	 */
	public UserToken createTokenDataServer(long userId, String objectId, AuthorityMethod method, long ownerID) throws InternalServerErrorException {
		return createTokenDataServer(userId, objectId, method, ownerID, null);
	}

	/**
	 * 给DataServer 创建Token。
	 * 
	 * @param user
	 * @param method
	 * @param objectId
	 * @param nodeLastModified
	 * @return
	 * @throws InternalServerErrorException
	 */
	public UserToken createTokenDataServer(long userId, String objectId, AuthorityMethod method, long ownerId, String nodeLastModified) throws InternalServerErrorException {
		return createTokenDataServer(userId, objectId, method, ownerId, nodeLastModified, null);
	}

	public UserToken createTokenDataServer(long userId, String objectId, AuthorityMethod method, long ownerId,
			String nodeLastModified, Long tokenTimeout) throws InternalServerErrorException {
		Authorize authorize = new Authorize(method, objectId, ownerId);
		String token = RandomKeyGUID.getSecureRandomGUID();
		if (StringUtils.isNotBlank(UAS_REGION_NAME)) {
			token = UAS_REGION_NAME + token;
		}
		long timeout = tokenTimeout != null ? tokenTimeout : tempTokenExpired;
		Date expireTime = new Date(System.currentTimeMillis() + timeout);
		UserToken userToken = new UserToken(TokenType.CreatedForDataServer.name(), authorize.toString(), token, userId,
				expireTime, nodeLastModified);
		userTokenService.saveUserToken(userToken);
		LOGGER.info("createTokenDataServer: " + token + ", timeout: " + timeout);
		return userToken;
	}

	/**
	 * 给DataServer 创建Token。
	 * 
	 * @param user
	 * @param method
	 * @param objectId
	 * @param nodeLastModified
	 * @return
	 * @throws InternalServerErrorException
	 */
	public UserToken createTokenForMessageListener(long userId, AuthorityMethod method) throws InternalServerErrorException {
		Authorize authorize = new Authorize(method, String.valueOf(userId));
		String token = RandomKeyGUID.getSecureRandomGUID();
		Date expireTime = new Date(System.currentTimeMillis() + messageListenerExpired);
		UserToken userToken = new UserToken(TokenType.MessageListener.name(), authorize.toString(), token, userId, expireTime, null);
		userTokenService.saveUserToken(userToken);
		return userToken;
	}

	public int getAccessCodeExpiredAt() {
		try {
			SystemConfig config = systemConfigDAO.get("link.accesscode.expiredTime");
			if (config != null) {
				return Integer.parseInt(config.getValue());
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());

		}
		return DEFAULT_ACCESS_CODE_EXPIRE_TIME;
	}

	public UserToken getCurrentToken() {
		return CURRENT_TOKEN.get();
	}

	/**
	 * 校验应用app权限
	 * 
	 * @param appToken
	 * @throws BaseRunException
	 */
	public UserToken getLinkToken(String authorization, String date) throws BaseRunException {
		UserToken userToken;
		if (!authorization.startsWith(LINK_PREFIX)) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}
		String[] strArr = authorization.split(",");
		if (strArr.length != LENG_ARRAY_APP && strArr.length != 2) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}
		INodeLink link = linkServiceV2.getLinkByLinkCodeForClient(StringUtils.trimToEmpty(strArr[1]));
		if (null == link) {
			throw new NoSuchLinkException("Can not find the link" + authorization);
		}
		userToken = new UserToken();
		userToken.setLinkCode(link.getId());
		if (StringUtils.isEmpty(link.getPlainAccessCode()) && link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
			return userToken;
		}
		if (strArr.length != 3) {
			throw new AuthFailedException("Bad link authorization: " + authorization);
		}

		userToken = new UserToken();
		userToken.setLinkCode(link.getId());
		userToken.setDate(date);

		if(date != null) {
			//携带了Date，对accessCode解密
			if (link.getStatus() == LinkAccessCodeMode.TYPE_STATIC_VALUE) {
				checkSignature(link.getPlainAccessCode(), date, strArr[2]);
				userToken.setPlainAccessCode(link.getPlainAccessCode());
			} else {
				userToken.setPlainAccessCode(checkDynamicLinkSignature(date, strArr, link.getStatus()));
			}
		} else {
			//未携带Date,直接使用明文作为accessCode
			userToken.setPlainAccessCode(strArr[2]);
		}
		return userToken;
	}

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void isAppUser(String appId, Long userId) {
		User user = userService.get(null, userId);
		if (user == null) {
			throw new NoSuchUserException();
		}
		if (StringUtils.equals(User.STATUS_DISABLE_INTEGER, user.getStatus())) {
			throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
		}
		if (!StringUtils.equals(appId, user.getAppId())) {
			throw new ForbiddenException();
		}
	}

	/**
	 * 终端权限校验
	 * 
	 * @param auth
	 * @param tokenStr
	 * @return
	 * @throws InternalServerErrorException
	 */
	public boolean isAuthorizedRequest(Authorize auth, DataServerToken userToken, String tokenStr)
			throws InternalServerErrorException {
		if (auth == null || userToken == null) {
			LOGGER.error("auth == null || userToken == null");
			return false;
		}
		return isAuthorizedRequest(auth, userToken.getTokenType(), userToken.getExpiredAt(), userToken.getAuth(),
				tokenStr);
	}

	public boolean isAuthorizedRequest(Authorize auth, String tokenType, Date expiredAt, String authString,
			String tokenStr) throws InternalServerErrorException {
		TokenType type = TokenType.valueOf(tokenType);
		if (TokenType.RefreshAble.equals(type)) {
			Calendar now = Calendar.getInstance();
			if (expiredAt != null && expiredAt.before(now.getTime())) {
				LOGGER.error("userToken expired, tokenStr: " + tokenStr + ", expiredAt:" + expiredAt);
				return false;
			}
		}
		Authorize realAuth = Authorize.valueOf(authString);
		boolean authorized = realAuth.contain(auth);
		if (TokenType.CreatedForDataServer.equals(type)) {
			switch (auth.getAuth()) {
			case PUT_PART:
				userTokenService.updateTempToken(tokenStr);
				break;
			case GET_PARTS:
				break;
			case OPTION_OBJECT:
				break;
			default:
				userTokenService.deleteUserToken(tokenStr);
				break;
			}
		} else if (TokenType.MessageListener.equals(type)) {
			userTokenService.deleteUserToken(tokenStr);
		}
		return authorized;
	}

	public void setCurrentToken(UserToken userToken) {
		CURRENT_TOKEN.set(userToken);
	}

	private Account accountToken(String authorization, String date) {
		String[] strArr = TokenChecker.checkLength(authorization);
		AccountAccessKey accessKey = null;
		try {
			accessKey = accessKeyManager.getAccountKeyById(null, StringUtils.trimToEmpty(strArr[1]));
			checkSignature(accessKey.getSecretKey(), date, strArr[2]);
			Account account = accountManager.getById(accessKey.getAccountId());
			if (AccountConstants.STATUS_DISABLE == account.getStatus()) {
				throw new AuthFailedException("Bad account status");
			}
			return account;
		} catch (Exception e) {
			throw new AuthFailedException(e);
		}
	}

	private String appToken(String authorization, String date) {
		String[] strArr = TokenChecker.checkLength(authorization);
		AppAccessKey key = appAccessKeyService.getById(StringUtils.trimToEmpty(strArr[1]));
		if (null == key) {
			throw new AuthFailedException("Can not find the key " + authorization);
		}
		checkSignature(key.getSecretKey(), date, strArr[2]);
		AuthApp app = authAppService.getByAuthAppID(key.getAppId());
		if (null == app) {
			throw new AuthFailedException("Can not find the app " + authorization);
		}
		if (app.getStatus() != 0) {
			throw new AuthFailedException("Bad app status");
		}
		return key.getAppId();
	}

	private String checkDynamicLinkSignature(String date, String[] strArr, byte linkType) throws BaseRunException {
		List<INodeLinkDynamic> dynamicis = linkServiceV2.getLinkDynamicCode(strArr[1]);

		Calendar ca = Calendar.getInstance();
		String calcuRes = null;
		for (INodeLinkDynamic dynamici : dynamicis) {
			if (ca.after(dynamici.getExpiredAt())) {
				linkServiceV2.deleteLinkDynamicCode(dynamici.getId(), dynamici.getIdentity());
				continue;
			}
			if (StringUtils.isBlank(dynamici.getPassword())) {
				continue;
			}
			calcuRes = SignatureUtils.getSignature(StringUtils.trimToEmpty(dynamici.getPassword()), date);
			if (StringUtils.equals(strArr[2], calcuRes)) {
				ca.add(Calendar.SECOND, getAccessCodeExpiredAt());
				dynamici.setExpiredAt(ca.getTime());
				linkServiceV2.updateExpiredAt(dynamici);
				return dynamici.getPassword();
			}
		}
		throwLinkException(strArr[2], linkType);
		return null;
	}

	private void checkSignature(String securityKey, String date, String result) throws AuthFailedException {
		String calcuRes = SignatureUtils.getSignature(StringUtils.trimToEmpty(securityKey), date);
		if (!StringUtils.equals(result, calcuRes)) {
			throw new AuthFailedException("signature result is false. calcuRes is " + calcuRes);
		}
	}

	private String getAppIdFromToken(String authToken) {
		if (StringUtils.isEmpty(authToken)) {
			return null;
		}
		int sperate = authToken.indexOf("/");
		if (sperate > 0) {
			return authToken.substring(0, sperate);
		}
		return null;
	}

	private void throwLinkException(String authorization, byte linkType) {
		if (linkType == LinkAccessCodeMode.TYPE_MAIL_VALUE) {
			throw new DynamicMailForbidden("Bad link authorization: " + authorization);
		}
		if (linkType == LinkAccessCodeMode.TYPE_PHONE_VALUE) {
			throw new DynamicPhoneForbidden("Bad link authorization: " + authorization);
		}
		throw new AuthFailedException("Bad link authorization: " + authorization);
	}

	public DataServerToken checkTokenAndGetUser(String token, String objectId, AuthorityMethod method, boolean isDeleteToken) {
		// TODO Auto-generated method stub

		DataServerToken tokenValue = userTokenService.getUserToken(token);
		boolean authorized = isAuthorizedRequest(new Authorize(method, objectId), tokenValue, token,isDeleteToken);
		if (!authorized) {
			throw new AuthFailedException();
		}
		tokenValue.setToken(token);
		return tokenValue;
	
	}

	private boolean isAuthorizedRequest(Authorize auth, DataServerToken userToken, String token,
			boolean isDeleteToken) {
		if (auth == null || userToken == null) {
			LOGGER.error("auth == null || userToken == null");
			return false;
		}
		return isAuthorizedRequest(auth, userToken.getTokenType(), userToken.getExpiredAt(), userToken.getAuth(),
				token,isDeleteToken);
	}

	private boolean isAuthorizedRequest(Authorize auth, String tokenType, Date expiredAt, String authString,
			String tokenStr,boolean isDeleteToken) {
		TokenType type = TokenType.valueOf(tokenType);
		if (TokenType.RefreshAble.equals(type)) {
			Calendar now = Calendar.getInstance();
			if (expiredAt != null && expiredAt.before(now.getTime())) {
				LOGGER.error("userToken expired, tokenStr: " + tokenStr + ", expiredAt:" + expiredAt);
				return false;
			}
		}
		Authorize realAuth = Authorize.valueOf(authString);
		boolean authorized = realAuth.contain(auth);
		if (TokenType.CreatedForDataServer.equals(type)) {
			switch (auth.getAuth()) {
			case PUT_PART:
				userTokenService.updateTempToken(tokenStr);
				break;
			case GET_PARTS:
				break;
			case OPTION_OBJECT:
				break;
			default:
				if(isDeleteToken){
					userTokenService.deleteUserToken(tokenStr);
				}
				break;
			}
		} else if (TokenType.MessageListener.equals(type)) {
			userTokenService.deleteUserToken(tokenStr);
		}
		return authorized;
	}


	public void checkAdminUser(Long ownerId, Long adminId, UserToken user) {
		// TODO Auto-generated method stub
		
		if(user.getIsAdmin()==0){
			throw new AuthFailedException();
		}
		User owner = userService.get(ownerId);
		if(owner.getAccountId().longValue()!= user.getAccountId().longValue()){
			throw new AuthFailedException();
		}
		
		
	}

}
