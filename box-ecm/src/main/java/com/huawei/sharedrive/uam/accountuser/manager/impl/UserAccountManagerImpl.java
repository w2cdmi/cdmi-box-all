package com.huawei.sharedrive.uam.accountuser.manager.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountCheckManager;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.adminlog.domain.OperateDescriptionType;
import com.huawei.sharedrive.uam.authapp.manager.AuthAppManager;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.manager.AccountBasicConfigManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.dao.UserLdapDAO;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.AccountAuthFailedException;
import com.huawei.sharedrive.uam.exception.BusinessErrorCode;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ExistUserConflictException;
import com.huawei.sharedrive.uam.exception.InvalidAppIdException;
import com.huawei.sharedrive.uam.exception.LocalAuthException;
import com.huawei.sharedrive.uam.exception.NoSuchAuthServerException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.oauth2.service.UserTokenCacheService;
import com.huawei.sharedrive.uam.openapi.domain.RestUserUpdateRequest;
import com.huawei.sharedrive.uam.plugin.Netease.service.IMAccountService;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.SqlUtils;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class UserAccountManagerImpl implements UserAccountManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserManager.class);

	private static final String EXCEL_2003 = ".xls";

	private static final String LOCAL_ZH_CN = "zh_CN";

	private static final String LOCAL_ZH = "zh";

	private static final String CHARSET_UTF8 = "UTF-8";

	private static final int DEFAULT_EXPORT_USER_SIZE = 50000;

	private static final int MAX_DEFAULT_EXPORT_USER_SIZE = 500000;

	private static final int MINVALUE = -1;

	private static final int MAXVERSION = 10000;

	private static final int MAXTEAMSPACE = 10000;

	private static final long MAXSPACE = 104857600L;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private EnterpriseAccountService enterpriseAccountService;

	@Autowired
	private AppBasicConfigService appBasicConfigService;

	@Autowired
	private UserTokenCacheService userTokenCacheService;

	@Resource
	private RestClient ufmClientService;

	@Autowired
	private AccountAuthserverService accountAuthserverService;

	@Autowired
	private UserAccountCheckManager userAccountCheckManager;

	@Autowired
	private UserLdapDAO userLdapDAO;

	@Autowired
	private LdapUserManager ldapUserManager;
	
	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private AuthServerService authServerService;

	@Autowired
	private AccountAuthserverManager accountAuthserverManager;

	@Autowired
	private AuthAppManager authAppManager;

	@Autowired
	private AccountBasicConfigManager accountBasicConfigManager;
	
	@Autowired
	private IMAccountService IMService;

	@Override
	public void create(long userId, long enterpriseId, String authAppId,String loginName) {
	  create(userId, enterpriseId, authAppId, false,loginName);
	}

	@Override
	public void create(long userId, long enterpriseId, String authAppId, boolean changePassword,String loginName) {
		EnterpriseUser enterpriseUser = enterpriseUserService.get(userId, enterpriseId);
		if (null == enterpriseUser) {
			LOGGER.error(
					"[userAccount] find enterpriseUser failed" + " userId:" + userId + " enterpriseId:" + enterpriseId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseUser failed");
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId + " authAppId:"
					+ authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
			LOGGER.error(
					"enterpriseAccount status is disable, enterpriseId: " + enterpriseId + "authAppId: " + authAppId);
			throw new AccountAuthFailedException("enterpriseAccount status is disable");
		}

		UserAccount dbUserAccount = userAccountService.get(userId, enterpriseAccount.getAccountId());
		if (null != dbUserAccount) {
			return;
		}

		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		UserAccount userAccount = new UserAccount(userId, enterpriseId, enterpriseAccount.getAccountId());
		fillUserAccountByApp(userAccount, authAppId);
		if(loginName!=null){
			enterpriseUser.setName(loginName);
		}
		long cloudUserId = userHttpClient.createUser(userAccount, enterpriseUser, enterpriseAccount, true);
		userAccount.setCloudUserId(cloudUserId);
		userAccount.setResourceType(enterpriseUser.getUserSource());

		//不强制修改密码，直接将firstLogin置为STATUS_NONE_FIRST_LOGIN
		if(!changePassword) {
			userAccount.setFirstLogin(UserAccount.STATUS_NONE_FIRST_LOGIN);
		} else {
			userAccount.setFirstLogin(UserAccount.INT_STATUS_ENABLE);
		}
		//创建临时账号时，账号云存储空间大小设置为0
		if(enterpriseUser.getType() == EnterpriseUser.TYPE_TEMPORARY){
			userAccount.setSpaceQuota((long)0);
			userAccount.setTeamSpaceQuota((long)(0));
		}
		try {
			userAccountService.create(userAccount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//用户开户成功后添加im帐号
//		try {
//			Enterprise enterprise=enterpriseService.getById(enterpriseId);
//			Map<String, Object> paramter=new HashMap<>();
//			paramter.put("accountId", userAccount.getAccountId());
//			paramter.put("cloudUserId", userAccount.getCloudUserId());
//			paramter.put("accid",userAccount.getCloudUserId()+"@"+ enterprise.getDomainName());
//			paramter.put("name",enterpriseUser.getAlias());
//			paramter.put("token",userAccount.getCloudUserId()+"@"+ enterprise.getDomainName());
//			paramter.put("enterpriseId",enterpriseId);
//			paramter.put("userId", userId);
//			IMService.createIMAccount(paramter);
//		} catch (Exception e) {
//			LOGGER.error("[userAccount]  create Im failed" + " enterpriseId:" + enterpriseId + " authAppId:"+ authAppId);
//		}
		
	}

	@Override
	public void createByApi(long userId, long enterpriseId, String authAppId, RestUserUpdateRequest ruser) {
		EnterpriseUser enterpriseUser = enterpriseUserService.get(userId, enterpriseId);
		if (null == enterpriseUser) {
			LOGGER.error(
					"[userAccount] find enterpriseUser failed" + " userId:" + userId + " enterpriseId:" + enterpriseId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseUser failed");
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId + " authAppId:"
					+ authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
			LOGGER.error(
					"enterpriseAccount status is disable, enterpriseId: " + enterpriseId + "authAppId: " + authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseAccount status disalbe");
		}

		UserAccount dbUserAccount = userAccountService.get(userId, enterpriseAccount.getAccountId());
		if (null != dbUserAccount) {
			throw new ExistUserConflictException();
		}

		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		UserAccount userAccount = new UserAccount(userId, enterpriseId, enterpriseAccount.getAccountId());
		fillUserAccountByApp(userAccount, authAppId);

		if (null == ruser.getSpaceQuota()) {
			userAccount.setSpaceQuota(User.SPACE_QUOTA_UNLIMITED);
		} else {
			userAccount.setSpaceQuota(ruser.getSpaceQuota());
		}
		if (null == ruser.getRegionId()) {
			userAccount.setRegionId(User.REGION_ID_UNDEFINED);
		} else {
			userAccount.setRegionId(ruser.getRegionId());
		}
		if (User.STATUS_DISABLE.equalsIgnoreCase(ruser.getStatus())) {
			userAccount.setStatus(UserAccount.INT_STATUS_DISABLE);
		} else {
			userAccount.setStatus(UserAccount.INT_STATUS_ENABLE);
		}
		if (null == ruser.getMaxVersions()) {
			userAccount.setMaxVersions(User.VERSION_NUM_UNLIMITED);
		} else {
			userAccount.setMaxVersions(ruser.getMaxVersions());
		}

		long cloudUserId = userHttpClient.createUser(userAccount, enterpriseUser, enterpriseAccount, true);
		userAccount.setCloudUserId(cloudUserId);
		userAccount.setResourceType(enterpriseUser.getUserSource());
		userAccountService.create(userAccount);
	}

	@Override
	public void update(UserAccount userAccount, String authAppId) {
		EnterpriseUser enterpriseUser = enterpriseUserService.get(userAccount.getUserId(),
				userAccount.getEnterpriseId());
		if (null == enterpriseUser) {
			LOGGER.error("[userAccount] find enterpriseUser failed" + " userId:" + userAccount.getUserId()
					+ " enterpriseId:" + userAccount.getEnterpriseId());
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseUser failed");
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(userAccount.getEnterpriseId(),
				authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:"
					+ userAccount.getEnterpriseId() + " authAppId:" + authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
			LOGGER.error("enterpriseAccount status is disable, enterpriseId: " + userAccount.getEnterpriseId()
					+ "authAppId: " + authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseAccount status disable");
		}
		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		userHttpClient.updateUser(userAccount, enterpriseUser, enterpriseAccount);
		userAccountService.update(userAccount);
	}

	@Override
	public boolean update(RestUserUpdateRequest ruser, UserAccount selUserAccount) {
		boolean isUpdate = userAccountCheckManager.isUpdateAndSetUserAccount(selUserAccount, ruser);
		if (isUpdate) {
			userAccountService.update(selUserAccount);
		}
		return isUpdate;
	}

	@Override
	public UserAccount getUserAccountByApp(long userId, long enterpriseId, String authAppId) {
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId + " authAppId:"
					+ authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		UserAccount userAccount = userAccountService.get(userId, enterpriseAccount.getAccountId());
		if (userAccount != null){
			UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
			RestUserCreateRequest userInfo = userHttpClient.getUserInfo(userAccount.getCloudUserId(), enterpriseAccount);
			userAccount.setSpaceUsed(userInfo.getSpaceUsed());
		}
		return userAccount;
	}

	@Override
	public UserAccount getById(long id, long enterpriseId, String authAppId) {
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId + " authAppId:"
					+ authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		UserAccount userAccount = userAccountService.getById(id, enterpriseAccount.getAccountId());
		return userAccount;
	}

	@Override
	public UserAccount getByImAccount(String imAccount, long accountId) {
		return userAccountService.getByImAccount(imAccount, accountId);
	}

	@Override
	public void fillUserAccountByApp(UserAccount userAccount, String authAppId) {

		AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(authAppId);
		if (null == appBasicConfig) {
			LOGGER.error("[userAccount] find appBasicConfig failed" + " authAppId:" + authAppId);
			throw new BusinessException(BusinessErrorCode.NotFoundException, "find userAccount failed");
		}
		AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
		accountBasicConfig.setAccountId(userAccount.getAccountId());
		accountBasicConfig = accountBasicConfigManager.queryAccountBasicConfig(accountBasicConfig,authAppId);
		if (!accountBasicConfig.getUserSpaceQuota().equals("-1")) {
			appBasicConfig.setUserSpaceQuota(Long.parseLong(accountBasicConfig.getUserSpaceQuota()));
		}
		if (!accountBasicConfig.getUserVersions().equals("-1")) {
			appBasicConfig.setMaxFileVersions(Integer.parseInt(accountBasicConfig.getUserVersions()));
		}
		if (accountBasicConfig.isEnableTeamSpace()) {
			appBasicConfig.setEnableTeamSpace(true);
		}else {
			appBasicConfig.setEnableTeamSpace(false);
		}
		if (!accountBasicConfig.getMaxTeamSpaces().equals("-1")) {
			appBasicConfig.setMaxTeamSpaces(Integer.parseInt(accountBasicConfig.getMaxTeamSpaces()));
		}
		if (!accountBasicConfig.getTeamSpaceQuota().equals("-1")) {
			appBasicConfig.setTeamSpaceQuota(Long.parseLong(accountBasicConfig.getTeamSpaceQuota()));
		}
		userAccountService.bulidUserAccount(userAccount, appBasicConfig);
	}

	@Override
	public UserAccount get(long userId, long accountId) {
		return userAccountService.get(userId, accountId);
	}

	@Override
	public Page<UserAccount> getPagedUserAccount(UserAccount userAccount, String appId, long authServerId,
			PageRequest pageRequest, String filter) {
		int accountId = accountAuthserverService.getAccountId(authServerId, userAccount.getEnterpriseId(), appId);
		if (AccountAuthserver.UNDEFINED_OPEN_ACCOUNT == accountId) {
			return new PageImpl<UserAccount>(new ArrayList<UserAccount>(10), pageRequest, 0);
		}

		int total = userAccountService.getFilterdCount(accountId, userAccount.getEnterpriseId(), authServerId, filter,
				userAccount.getStatus());
		UserAccount tmpUserAccount = new UserAccount();
		tmpUserAccount.setEnterpriseId(userAccount.getEnterpriseId());
		tmpUserAccount.setStatus(userAccount.getStatus());
		tmpUserAccount.setAccountId(accountId);
		List<UserAccount> content = userAccountService.getFilterd(tmpUserAccount, authServerId, pageRequest.getLimit(),
				filter);
		Page<UserAccount> page = new PageImpl<UserAccount>(content, pageRequest, total);
		return page;
	}

	@Override
	public void deleteUserAccount(long userId, long accountId) {
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(accountId);
		if (null == enterpriseAccount) {
			return;
		}
		UserAccount userAccount = userAccountService.get(userId, enterpriseAccount.getAccountId());
		if (null != userAccount) {
			UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
			userHttpClient.deleteUserInfo(userAccount.getCloudUserId(), enterpriseAccount);
			userAccountService.delByUserAccountId(userAccount);

			userTokenCacheService.deleteUserToken(userAccount.getCloudUserId());
		}
	}

	@Override
	public String exportUserList(HttpServletRequest request, HttpServletResponse response, UserAccount userAccount)
			throws IOException {
		processExportUserInfo(userAccount);
		Integer status = getExportUserStatus(userAccount);

		Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
		String language = locale.getLanguage();
		String filename = "";
		if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language)) {
			locale = Locale.CHINA;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
			filename = java.net.URLEncoder.encode(filename, CHARSET_UTF8);
		} else {
			locale = Locale.ENGLISH;
			filename = OperateDescriptionType.USER_INFORMATION_LIST.getDetails(locale, null);
		}
		String fileHeaderStr = OperateDescriptionType.USER_BULB.getDetails(locale, null);
		String[] fileHeader = fileHeaderStr.split("@");
		response.setContentType("application/msexcel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + getUtcTime() + EXCEL_2003);
		int length = fileHeader.length;
		List<UserAccount> userAccounts = new ArrayList<UserAccount>(10);
		Long accountId = (long) accountAuthserverService.getAccountId(userAccount.getAccountId(),
				userAccount.getEnterpriseId(), userAccount.getName());
		int total;
		UserLdap userLdap = new UserLdap();
		if (StringUtils.isNotBlank(userAccount.getAlias())) {
			HttpSession session = request.getSession();
			String sessionId = session.getId();
			ldapUserManager.insertLdapUser(sessionId, userAccount.getAlias(), userAccount.getAccountId());
			userLdap.setSessionId(sessionId);
			userLdap.setDn(userAccount.getAlias());
			userLdap.setAuthServerId(userAccount.getAccountId());
			total = userLdapDAO.getUserAccountFilterdCount(userLdap, userAccount.getEnterpriseId(), accountId,
					userAccount.getDescription(), status);
		} else {
			total = userAccountService.getFilterdCount(accountId, userAccount.getEnterpriseId(),
					userAccount.getAccountId(), userAccount.getDescription(), status);
		}

		if (total > MAX_DEFAULT_EXPORT_USER_SIZE) {
			total = MAX_DEFAULT_EXPORT_USER_SIZE;
		}
		doExportUsers(response, userAccount, status, fileHeader, length, accountId, total, userLdap);
		StringBuffer accountIds = new StringBuffer();
		if (!userAccounts.isEmpty()) {
			UserAccount uAccount;
			for (int i = 0; i < userAccounts.size(); i++) {
				uAccount = userAccounts.get(i);
				if (uAccount != null) {
					accountIds.append(uAccount.getId());
				}
				if (i < userAccounts.size() - 1) {
					accountIds.append(',');
				}
			}
			return accountIds.toString();
		}
		return null;
	}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	private void doExportUsers(HttpServletResponse response, UserAccount userAccount, Integer status,
			String[] fileHeader, int length, Long accountId, int total, UserLdap userLdap) throws IOException {
		HSSFWorkbook hwb = new HSSFWorkbook();
		HSSFSheet sheet;
		List<UserAccount> content;
		Limit limit;
		UserAccount tmpUserAccount = new UserAccount();
		for (long i = 0; i < total; i += DEFAULT_EXPORT_USER_SIZE) {
			sheet = setExcelHeader(hwb, fileHeader, length);
			limit = new Limit();
			limit.setOffset(i);
			limit.setLength(DEFAULT_EXPORT_USER_SIZE);
			if (StringUtils.isNotBlank(userAccount.getAlias())) {
				content = userLdapDAO.getUserAccountFilterd(userLdap, userAccount.getEnterpriseId(), accountId,
						userAccount.getDescription(), status, null, limit);
			} else {
				tmpUserAccount.setAccountId(accountId);
				tmpUserAccount.setEnterpriseId(userAccount.getEnterpriseId());
				tmpUserAccount.setStatus(status);
				content = userAccountService.getFilterd(tmpUserAccount, userAccount.getAccountId(), limit,
						userAccount.getDescription());
			}
			userToExcel(content, sheet);
		}

		hwb.write(response.getOutputStream());
		response.getOutputStream().flush();
	}

	private Integer getExportUserStatus(UserAccount userAccount) {
		Integer status = null;
		if (userAccount.getStatus() == 0 || userAccount.getStatus() == 1) {
			status = userAccount.getStatus();
		}
		return status;
	}

	private void processExportUserInfo(UserAccount userAccount) throws UnsupportedEncodingException {
		byte[] filterByte = userAccount.getDescription().getBytes("ISO8859-1");
		String filterStr = new String(filterByte, "UTF-8");
		byte[] dnByte = userAccount.getAlias().getBytes("ISO8859-1");
		String dnStr = new String(dnByte, "UTF-8");

		userAccount.setDescription(null);
		userAccount.setAlias(null);

		if (StringUtils.isNotBlank(filterStr) && !"null".equalsIgnoreCase(filterStr)) {
			userAccount.setDescription(SqlUtils.stringToSqlLikeFields(filterStr));
		}
		if (StringUtils.isNotBlank(dnStr) && !"null".equalsIgnoreCase(dnStr)) {
			userAccount.setAlias(SqlUtils.stringToSqlLikeFields(dnStr));
		}
	}

	@Override
	public void updateLoginTime(UserAccount userAccount) {
		userAccountService.updateLoginTime(userAccount);

	}

	private String getUtcTime() {
		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = sdf.format(currentDate);
		return utcTime;
	}

	private HSSFSheet setExcelHeader(HSSFWorkbook hwb, String[] fileHeader, int length) {
		HSSFSheet sheet = hwb.createSheet();
		HSSFRow hRow = sheet.createRow(0);
		for (int i = 0; i < length; i++) {
			createCell(hwb, hRow, i, fileHeader[i]);
		}

		return sheet;
	}

	private void createCell(HSSFWorkbook wb, HSSFRow row, int col, String val) {
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(val);
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
		cell.setCellStyle(cellStyle);
	}

	private void userToExcel(List<UserAccount> userList, HSSFSheet sheet) {
		if (null == userList || userList.size() <= 0) {
			return;
		}
		int i = 1;
		HSSFRow hRow;
		for (UserAccount user : userList) {
			hRow = sheet.createRow(i);
			createUserCell(hRow, user);
			i++;
		}
	}

	private void createUserCell(HSSFRow row, UserAccount user) {
		if (user != null) {
			row.createCell(0).setCellValue(user.getName());
			row.createCell(1).setCellValue(user.getAlias());
			row.createCell(2).setCellValue(user.getEmail());
			row.createCell(3).setCellValue(user.getDescription());
			if (user.getRegionId() != UserAccount.PARAMETER_UNDEFINED) {
				row.createCell(4).setCellValue(user.getRegionId());
			}
			if (user.getSpaceQuota() != UserAccount.PARAMETER_UNDEFINED) {
				row.createCell(5).setCellValue(user.getSpaceQuota() == UserAccount.SPACE_QUOTA_UNLIMITED
						? UserAccount.SPACE_QUOTA_UNLIMITED
						: user.getSpaceQuota()
								/ (double) (UserAccount.SPACE_UNIT * UserAccount.SPACE_UNIT * UserAccount.SPACE_UNIT));
			}
			if (user.getMaxVersions() != UserAccount.PARAMETER_UNDEFINED) {
				row.createCell(6).setCellValue(user.getMaxVersions());
			}
			if (user.getTeamSpaceFlag() != UserAccount.PARAMETER_UNDEFINED) {
				row.createCell(7).setCellValue(user.getTeamSpaceFlag());
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createUserAccount(EnterpriseUser user, String authAppId, UserAccount account) {
		if (null == authAppId) {
			LOGGER.error("authAppId=null");
			throw new InvalidAppIdException();
		}

		AuthApp authApp = authAppManager.getByAuthAppID(authAppId);
		if (authApp == null) {
			LOGGER.warn("no such authApp:  {}.", authAppId);
			throw new NoSuchAuthServerException("no such authApp: " + authAppId);
		}

		boolean isLocalAuth = isLocalAuth(user.getEnterpriseId(), authAppId);
		if (!isLocalAuth) {
			throw new LocalAuthException();
		}

		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(user.getEnterpriseId(), authAppId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + user.getEnterpriseId() + " authAppId:" + authAppId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}

		UserAccount dbUserAccount = userAccountService.get(user.getId(), enterpriseAccount.getAccountId());

//		boolean flag = checkUserAccountParam(account);
//		if (!flag) {
//			throw new InvalidAppParamterException();
//		}
		fillUserAccountParamByApp(account, authAppId);
		if (null != dbUserAccount) {
			dbUserAccount.setMaxVersions(account.getMaxVersions());
			dbUserAccount.setSpaceQuota(account.getSpaceQuota());
			dbUserAccount.setTeamSpaceMaxNum(account.getTeamSpaceMaxNum());
			update(dbUserAccount, authAppId);
			return false;
		}

		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);
		long cloudUserId = userHttpClient.createUser(account, user, enterpriseAccount, true);
		account.setCloudUserId(cloudUserId);
		account.setResourceType(user.getUserSource());
		account.setAccountId(enterpriseAccount.getAccountId());
		userAccountService.create(account);
		return true;
	}

	@Override
	public void fillUserAccountParamByApp(UserAccount userAccount, String authAppId) {
		AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(authAppId);
		if (null == appBasicConfig) {
			LOGGER.error("[userAccount] find appBasicConfig failed" + " authAppId:" + authAppId);
			throw new BusinessException(BusinessErrorCode.NotFoundException, "find userAccount failed");
		}

		AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
		accountBasicConfig = accountBasicConfigManager.queryAccountBasicConfig(accountBasicConfig,authAppId);
		if (!accountBasicConfig.getUserSpaceQuota().equals("-1")) {
			appBasicConfig.setUserSpaceQuota(Long.parseLong(accountBasicConfig.getUserSpaceQuota()));
		}
		if (!accountBasicConfig.getUserVersions().equals("-1")) {
			appBasicConfig.setMaxFileVersions(Integer.parseInt(accountBasicConfig.getUserVersions()));
		}
		if (accountBasicConfig.isEnableTeamSpace()) {
			appBasicConfig.setEnableTeamSpace(true);
		}
		if (!accountBasicConfig.getMaxTeamSpaces().equals("-1")) {
			appBasicConfig.setMaxTeamSpaces(Integer.parseInt(accountBasicConfig.getMaxTeamSpaces()));
		}
		if (!accountBasicConfig.getTeamSpaceQuota().equals("-1")) {
			appBasicConfig.setTeamSpaceQuota(Long.parseLong(accountBasicConfig.getTeamSpaceQuota()));
		}

		userAccountService.bulidUserAccountParam(userAccount, appBasicConfig);
	}

	private boolean checkUserAccountParam(UserAccount account) {
		Integer maxVersions = account.getMaxVersions();
		Long spaceQuota = account.getSpaceQuota();
		Integer teamSpaceMaxNum = account.getTeamSpaceMaxNum();
		int enableTeamSpace = account.getTeamSpaceFlag();
		if (maxVersions == null || (maxVersions <= 0 && maxVersions != User.VERSION_NUM_UNLIMITED)
				|| maxVersions > MAXVERSION) {
			return false;
		}
		if (spaceQuota == null || (spaceQuota <= 0 && spaceQuota != MINVALUE) || spaceQuota > MAXSPACE) {
			return false;
		}
		if (enableTeamSpace==0&&(teamSpaceMaxNum == null || (teamSpaceMaxNum <= 0 && teamSpaceMaxNum != MINVALUE)
				|| teamSpaceMaxNum > MAXTEAMSPACE)) {
			return false;
		}
		return true;
	}

	private boolean isLocalAuth(long enterpriseId, String authAppId) {
		boolean isLocalAuth = false;
		List<AuthServer> authServer = authServerService.getByNoStatus(enterpriseId, null);
		long authServerId = 0L;
		for (AuthServer auth : authServer) {
			if ("LocalAuth".equalsIgnoreCase(auth.getType())) {
				authServerId = auth.getId();
			}
		}
		List<AccountAuthserver> listAuthservers = accountAuthserverManager.listBindApp(enterpriseId, authServerId);
		for (AccountAuthserver accountAuthserver : listAuthservers) {
			if (authAppId.equals(accountAuthserver.getAuthAppId())) {
				isLocalAuth = true;
			}
		}
		return isLocalAuth;
	}

	@Override
	public UserAccount getUserByCloudUserId(long accountId, long cloudUserId) {
		UserAccount userAccount=new UserAccount();
		userAccount.setAccountId(accountId);
		userAccount.setCloudUserId(cloudUserId);
		return userAccountService.getBycloudUserAccountId(userAccount);
	}
}
