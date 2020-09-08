package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseSecurityPrivilegeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.dao.impl.MigrationRecordIdGenerator;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserEntity;
import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationResponseDto;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserCheckManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EmployeesExcelService;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuser.service.IMigrationRecordService;
import com.huawei.sharedrive.uam.exception.AdAuthUserConflictException;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.ExistUserConflictException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.exception.ShaEncryptException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.logininfo.service.LoginInfoService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.CustomizeLogoService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.teamspace.domain.ChangeOwnerRequest;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import com.huawei.sharedrive.uam.util.PatternRegUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.core.utils.SqlUtils;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class EnterpriseUserManagerImpl implements EnterpriseUserManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserManager.class);

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private UserLockService userLockService;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private LoginInfoManager loginInfoManager;

	@Autowired
	private LdapUserManager ldapUserManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	private LoginInfoService loginInfoService;

	@Autowired
	private EmployeesExcelService employeesExcelService;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private EnterpriseUserCheckManager enterpriseUserCheckManager;

	public static final String AD_AUTH = "AdAuth";

	@Autowired
	private CustomizeLogoService customizeLogoService;
	
 	@Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private IMigrationRecordService migrationRecordService;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private DepartmentAccountService  departmentAccountService;

	@Autowired
	private AuthAppService authAppService;

    @Autowired
    private AuthServerService authServerService;

	@Resource
    private RestClient ufmClientService;
	
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private MigrationRecordIdGenerator migrationRecordIdGenerator;

	@Autowired
	private EnterpriseSecurityPrivilegeService privilegeService;

	private static final String CONST_DEPARTURE_FILE_EXPIRE_DAYS_KEY = "departure.file.default.expire.days";
	
//	@Autowired
//	private UserDepartmentManager userDepartmentManager;
	
	@Override
	public void updateLdap(EnterpriseUser enterpriseUser, Long userId, Long enterpriseId) {
		if (null == enterpriseUser) {
			LOGGER.error("user is null userId:" + userId + " enterpriseId:" + enterpriseId);
			return;
		}
		EnterpriseUser selEnterpriseUser = enterpriseUserService.get(userId, enterpriseId);

		Enterprise enterprise = enterpriseManager.getById(selEnterpriseUser.getEnterpriseId());
		if (null == enterprise) {
			LOGGER.error("enterprise is null enterpriseId:" + selEnterpriseUser.getEnterpriseId());
			return;
		}
		checkLoginNameLength(enterpriseUser);
		LoginInfo logininfo = new LoginInfo();
		logininfo.setEnterpriseId(selEnterpriseUser.getEnterpriseId());
		logininfo.setDomainName(enterprise.getDomainName());
		logininfo.setUserId(selEnterpriseUser.getId());
		if (enterpriseUserCheckManager.isLoginNameChanged(selEnterpriseUser.getName(), enterpriseUser.getName())) {
			logininfo.setLoginName(enterpriseUser.getName());
			logininfo.setLoginType(LoginInfo.LOGININGO_NAME);
			loginInfoManager.updateByNameType(selEnterpriseUser.getName(), logininfo);
		}
		if (enterpriseUserCheckManager.isLoginNameChanged(selEnterpriseUser.getEmail(), enterpriseUser.getEmail())) {
			logininfo.setLoginName(enterpriseUser.getEmail());
			logininfo.setLoginType(LoginInfo.LOGININGO_EMAIL);
			loginInfoManager.updateByNameType(selEnterpriseUser.getEmail(), logininfo);
		}

		selEnterpriseUser.setAlias(enterpriseUser.getAlias());
		selEnterpriseUser.setEmail(enterpriseUser.getEmail());
		selEnterpriseUser.setName(enterpriseUser.getName());
		selEnterpriseUser.setDescription(enterpriseUser.getDescription());
		selEnterpriseUser.setModifiedAt(enterpriseUser.getModifiedAt());
		enterpriseUserService.update(selEnterpriseUser);
	}

	@Override
	public boolean update(BasicUserUpdateRequest ruser, EnterpriseUser selEnterpriseUser, String domain) {
		boolean isUpdateEnterpriseUser = false;
		String selEnterpriseUserName = selEnterpriseUser.getName();
		String selEnterpriseUserMail = selEnterpriseUser.getEmail();
		String selEnterpriseUserMobile = selEnterpriseUser.getMobile();
		if (StringUtils.isNotBlank(ruser.getNewPassword()) && StringUtils.isNotBlank(ruser.getOldPassword())) {
			enterpriseUserCheckManager.checkAndSetPassword(selEnterpriseUser, ruser.getNewPassword(),
					ruser.getOldPassword());
			isUpdateEnterpriseUser = true;
		}
		EnterpriseUser enterpriseUser = new EnterpriseUser();
		enterpriseUser.setName(selEnterpriseUserName);
		BasicUserUpdateRequest.copyBasicToEnterpriseUser(enterpriseUser, ruser);
		if (enterpriseUserCheckManager.isUpdateAndSetEnterpriseUser(selEnterpriseUser, enterpriseUser)) {
			isUpdateEnterpriseUser = true;
		}
		if (isUpdateEnterpriseUser) {
			LoginInfo logininfo = new LoginInfo();
			logininfo.setEnterpriseId(selEnterpriseUser.getEnterpriseId());
			logininfo.setDomainName(domain);
			logininfo.setUserId(selEnterpriseUser.getId());
			if (enterpriseUserCheckManager.isLoginNameChanged(selEnterpriseUserName, enterpriseUser.getName())) {
				logininfo.setLoginName(enterpriseUser.getName());
				logininfo.setLoginType(LoginInfo.LOGININGO_NAME);
				loginInfoManager.updateByNameType(selEnterpriseUserName, logininfo);
			}
			if (enterpriseUserCheckManager.isLoginNameChanged(selEnterpriseUserMail, enterpriseUser.getEmail())) {
				logininfo.setLoginName(enterpriseUser.getEmail());
				logininfo.setLoginType(LoginInfo.LOGININGO_EMAIL);
				loginInfoManager.updateByNameType(selEnterpriseUserMail, logininfo);
			}
			if (enterpriseUserCheckManager.isLoginNameChanged(selEnterpriseUserMobile, enterpriseUser.getMobile())) {
				logininfo.setLoginName(enterpriseUser.getMobile());
				logininfo.setLoginType(LoginInfo.LOGININGO_EMAIL);
				loginInfoManager.updateByNameType(selEnterpriseUserMobile, logininfo);
			}
			selEnterpriseUser.setModifiedAt(enterpriseUser.getModifiedAt());
			enterpriseUserService.update(selEnterpriseUser);
		}
		return isUpdateEnterpriseUser;
	}

	@Override
	public void updatePassword(EnterpriseUser enterpriseUser) {

		enterpriseUserService.update(enterpriseUser);
	}

	@Override
	public EnterpriseUser getByObjectSid(String objectSid, long enterpriseId) {
		return enterpriseUserService.getByObjectSid(objectSid, enterpriseId);
	}

	@Override
	public EnterpriseUser getEUserByLoginName(String loginName, String domain) throws LoginAuthFailedException {
		LoginInfo loginInfo;
		if (StringUtils.isBlank(domain)) {
			List<LoginInfo> loginInfoList = loginInfoManager.getByLoginName(loginName);
			if (CollectionUtils.isEmpty(loginInfoList)) {
				LOGGER.error("loginInfo is null " + loginName);
				return null;
			}
			if (loginInfoList.size() > 1) {
				LOGGER.error("loginInfo table has too many user:" + loginName);
				throw new LoginAuthFailedException();
			}
			loginInfo = loginInfoList.get(0);
		} else {
			loginInfo = loginInfoManager.getByDomain(loginName, domain);
			if (null == loginInfo) {
				LOGGER.error("loginInfo is null " + loginName);
				return null;
			}
		}
		long enterpriseId = loginInfo.getEnterpriseId();
		long userId = loginInfo.getUserId();
		return enterpriseUserService.get(userId, enterpriseId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public long createLocal(EnterpriseUser enterpriseUser, boolean createLoginInfo) throws BusinessException, IOException {
		return create(enterpriseUser, createLoginInfo, true);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public long createWeixin(EnterpriseUser enterpriseUser, boolean createLoginInfo) throws BusinessException, IOException {
		return create(enterpriseUser, createLoginInfo, false);
	}

	protected long create(EnterpriseUser enterpriseUser, boolean createLoginInfo, boolean forceToChangePassword) throws IOException {
		enterpriseUser.setName(enterpriseUser.getName().trim());
		Enterprise enterprise = enterpriseManager.getById(enterpriseUser.getEnterpriseId());
		if (enterprise == null) {
			LOGGER.error("enterprise is null id:" + enterpriseUser.getEnterpriseId());
			throw new BusinessException();
		}

		//未指定authServer
		if(enterpriseUser.getUserSource() == 0) {
			AuthServer authServer = null;
			List<AuthServer> authServerList = authServerService.getByEnterpriseId(enterprise.getId(), new Limit(0L, 1));
			if(authServerList != null && !authServerList.isEmpty()) {
				authServer = authServerList.get(0);
			}

			if(authServer == null) {
				authServer = authServerService.getDefaultAuthServer();
			}

			if (authServer == null) {
				LOGGER.error("no available auth server for enterprise:" + enterpriseUser.getEnterpriseId());
				throw new BusinessException();
			}

			enterpriseUser.setUserSource(authServer.getId());
		}

		Date date = new Date();
		enterpriseUser.setCreatedAt(date);
		enterpriseUser.setLastLoginAt(date);
		enterpriseUser.setModifiedAt(date);
		enterpriseUser.setObjectSid(enterpriseUser.getName());

		String password = enterpriseUser.getPassword();
		if (StringUtils.isBlank(password)) {
			//未指定密码，随机生成。
			password = PasswordGenerateUtil.getRandomPassword(6);
			//password = "ecs@" + enterpriseUser.getMobile().substring(mobine.length() - 6, mobine.length());
		}

		//不允许密码与手机号码相同
		if (password.equals(enterpriseUser.getMobile())) {
			throw new ShaEncryptException();
		}

		try {
			HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
			enterpriseUser.setPassword(hashPassword.getHashPassword());
			enterpriseUser.setIterations(hashPassword.getIterations());
			enterpriseUser.setSalt(hashPassword.getSalt());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOGGER.error("digest exception", e);
			throw new ShaEncryptException();
		}

		long id = enterpriseUserService.create(enterpriseUser);

		//恢复明文，供调用方使用
		enterpriseUser.setPassword(password);

		//新建用户自动开通默认应用
		AuthApp app = authAppService.getDefaultWebApp();
		userAccountManager.create(id, enterpriseUser.getEnterpriseId(), app.getAuthAppId(), enterpriseUser.getName());

		if(createLoginInfo) {
			//生成登录信息
			createLoginInfo(enterprise.getDomainName(), enterpriseUser);
		}

/*
		if(enterpriseUser.getStatus()==EnterpriseUser.STATUS_ENABLE){
			//此处使用原来的明文密码，发送邮件
			enterpriseUser.setPassword(password);
			sendEmail(enterpriseUser);
		}
*/
		enterpriseUser.setId(id);
		return id;
	}

	protected void createLoginInfo(String domain, EnterpriseUser enterpriseUser) {
		// 登录名在同一个公司（即同一个domain）下唯一
		LoginInfo loginInfoName = loginInfoManager.getByDomain(enterpriseUser.getName(), domain);
		if (null != loginInfoName) {
            LOGGER.error("Create user login info fail, email already exists. domain={}, name={}", domain, enterpriseUser.getName());
			throw new ExistUserConflictException("duplicated user name: " + enterpriseUser.getName());
		}

		//生成使用name的登录信息
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setDomainName(domain);
		loginInfo.setLoginName(enterpriseUser.getName());
		loginInfo.setEnterpriseId(enterpriseUser.getEnterpriseId());
		loginInfo.setUserId(enterpriseUser.getId());
		loginInfo.setLoginType(LoginInfo.LOGININGO_NAME);
		loginInfoManager.create(loginInfo);

		//生成使用email的登录信息
		if (StringUtils.isNotBlank(enterpriseUser.getEmail())) {
            LoginInfo emailLoginInfo = loginInfoManager.getByDomain(enterpriseUser.getEmail(), domain);
			//email在该企业下未被占用
			if (emailLoginInfo == null) {
				emailLoginInfo = new LoginInfo();
                emailLoginInfo.setDomainName(domain);
                emailLoginInfo.setLoginName(enterpriseUser.getEmail());
                emailLoginInfo.setEnterpriseId(enterpriseUser.getEnterpriseId());
                emailLoginInfo.setUserId(enterpriseUser.getId());
                emailLoginInfo.setLoginType(LoginInfo.LOGININGO_EMAIL);
				loginInfoManager.create(emailLoginInfo);
			} else {
				//正常情况下，应该不会出现重复（因为domain是新的）
				LOGGER.error("Create user login info fail, email already exists. domain={}, email={}", domain, enterpriseUser.getEmail());
//				throw new ExistUserConflictException();
			}
		}

		//生成使用手机号码的登录信息
		if(StringUtils.isNotBlank(enterpriseUser.getMobile())){
            LoginInfo mobileLoginInfo = loginInfoManager.getByDomain(enterpriseUser.getMobile(), domain);
			if(mobileLoginInfo == null) {
				mobileLoginInfo = new LoginInfo();
				mobileLoginInfo.setDomainName(domain);
				mobileLoginInfo.setLoginName(enterpriseUser.getMobile());
				mobileLoginInfo.setEnterpriseId(enterpriseUser.getEnterpriseId());
				mobileLoginInfo.setUserId(enterpriseUser.getId());
				mobileLoginInfo.setLoginType(LoginInfo.LOGININGO_MOBILE);
				loginInfoManager.create(mobileLoginInfo);
			} else {
				//正常情况下，应该不会出现重复（因为domain是新的）
				LOGGER.error("Create user login info fail, mobile already exists. domain={}, mobile={}", domain, enterpriseUser.getMobile());
//				throw new ExistUserConflictException();
			}
		}

        //使用工号登录
		if(StringUtils.isNotBlank(enterpriseUser.getStaffNo())){
            LoginInfo staffNoLoginInfo = loginInfoManager.getByDomain(enterpriseUser.getStaffNo(), domain);
			if(staffNoLoginInfo == null){
				staffNoLoginInfo = new LoginInfo();
				staffNoLoginInfo.setDomainName(domain);
				staffNoLoginInfo.setLoginName(enterpriseUser.getStaffNo());
				staffNoLoginInfo.setEnterpriseId(enterpriseUser.getEnterpriseId());
				staffNoLoginInfo.setUserId(enterpriseUser.getId());
				staffNoLoginInfo.setLoginType(LoginInfo.LOGININGO_SATFF);
				loginInfoManager.create(staffNoLoginInfo);
			} else {
                //正常情况下，应该不会出现重复（因为domain是新的）
				LOGGER.error("Create user login info fail, staffNo already exists. domain={}, staffNo={}", domain, enterpriseUser.getStaffNo());
//				throw new ExistUserConflictException();
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public long createLdap(EnterpriseUser enterpriseUser) throws BusinessException {
		Enterprise enterprise = enterpriseManager.getById(enterpriseUser.getEnterpriseId());
		if (null == enterprise) {
			LOGGER.error("enterprise is null id:" + enterpriseUser.getEnterpriseId());
			throw new BusinessException();
		}
		checkLoginNameLength(enterpriseUser);
		Date date = new Date();
		enterpriseUser.setCreatedAt(date);
		enterpriseUser.setLastLoginAt(date);
		enterpriseUser.setModifiedAt(date);
		long id = enterpriseUserService.create(enterpriseUser);

		LoginInfo loginInfoByName = new LoginInfo();
		loginInfoByName.setLoginName(enterpriseUser.getName());
		loginInfoByName.setEnterpriseId(enterpriseUser.getEnterpriseId());
		loginInfoByName.setUserId(id);
		loginInfoByName.setLoginType(LoginInfo.LOGININGO_NAME);
		loginInfoByName.setDomainName(enterprise.getDomainName());
		loginInfoManager.create(loginInfoByName);
		if (StringUtils.isNotBlank(enterpriseUser.getEmail())) {
			LoginInfo loginInfoByEmail = new LoginInfo();
			loginInfoByEmail.setDomainName(enterprise.getDomainName());
			loginInfoByEmail.setLoginName(enterpriseUser.getEmail());
			loginInfoByEmail.setEnterpriseId(enterpriseUser.getEnterpriseId());
			loginInfoByEmail.setUserId(id);
			loginInfoByEmail.setLoginType(LoginInfo.LOGININGO_EMAIL);
			loginInfoManager.create(loginInfoByEmail);
		}
		return id;
	}

	@Override
	public Page<EnterpriseUser> getPagedEnterpriseUser(String filter, Long authServerId,String deptId, Long enterpriseId,
			PageRequest pageRequest) {
		Long departmentId = null;
		if(StringUtils.isNotEmpty(deptId)){
			departmentId=Long.parseLong(deptId);
		}
		int total = enterpriseUserService.getFilterdCount(filter, authServerId, enterpriseId,departmentId);
		
		List<EnterpriseUser> content = enterpriseUserService.getFilterd(filter, authServerId, departmentId,enterpriseId,
				pageRequest.getOrder(), pageRequest.getLimit());
		Page<EnterpriseUser> page = new PageImpl<EnterpriseUser>(content, pageRequest, total);
		return page;
	}

	@Override
	public void delete(long enterpriseId, long authServerId, String dn, String filter, String ids, String sessionId) {

		String[] idArray = ids.split(",");

		if ("all".equalsIgnoreCase(ids)) {
			idArray = getAllUserId(enterpriseId, authServerId, dn, filter, sessionId);
		}
		for (String iter : idArray) {
			delete(Long.parseLong(iter), enterpriseId);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(long userId, long enterpriseId) {

		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		if (null == enterprise) {
			LOGGER.error("enterprise is null enterpriseId:" + enterpriseId);
			throw new InternalServerErrorException();
		}
		List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
		if (null == enterpriseAccountList || enterpriseAccountList.size() < 1) {
			LOGGER.info("enterprise is null enterpriseId:" + enterpriseId);
			return;
		}
		UserLocked userLocked;
		for (EnterpriseAccount enterpriseAccount : enterpriseAccountList) {
			// 删除用户的锁定信息 user_locked 表
			userLocked = new UserLocked();
			userLocked.setAppId(enterpriseAccount.getAuthAppId());
			userLocked.setUserId(userId);
			userLockService.deleteUserLocked(userLocked);

			userAccountManager.deleteUserAccount(userId, enterpriseAccount.getAccountId());
		}
		EnterpriseUser enterpriseUser = enterpriseUserService.get(userId, enterpriseId);
		loginInfoService.delByNameEnterId(enterpriseUser.getName(), enterpriseId);
		if (StringUtils.isNotBlank(enterpriseUser.getEmail())) {
			loginInfoService.delByNameEnterId(enterpriseUser.getEmail(), enterpriseId);
		}
		if (StringUtils.isNotBlank(enterpriseUser.getMobile())) {
			loginInfoService.delByNameEnterId(enterpriseUser.getMobile(), enterpriseId);
		}
		if (StringUtils.isNotBlank(enterpriseUser.getStaffNo())) {
			loginInfoService.delByNameEnterId(enterpriseUser.getStaffNo(), enterpriseId);
		}
		enterpriseUserService.deleteById(enterpriseId, userId);
	}

	@Override
	public String[] getAllUserId(long enterpriseId, long authServerId, String dn, String filter, String sessionId) {
		String[] idArray = null;
		PageRequest request = new PageRequest();
		request.setPage(1);
		request.setSize(Integer.MAX_VALUE);
		Page<EnterpriseUser> page = null;

		if (StringUtils.isNotBlank(dn)) {
			ldapUserManager.insertLdapUser(sessionId, dn, authServerId);
			page = ldapUserManager.getPagedUserExtend(sessionId, dn, authServerId, enterpriseId,
					SqlUtils.stringToSqlLikeFields(filter), request);
		} else {
			page = getPagedEnterpriseUser(SqlUtils.stringToSqlLikeFields(filter),authServerId, null, enterpriseId, request);
		}
		if (null != page) {
			List<EnterpriseUser> list = page.getContent();
			idArray = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				idArray[i] = String.valueOf(list.get(i).getId());
			}
		}

		return idArray;
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
		String subject = mailServerService.getEmailMsgByTemplate(Constants.OPEN_ACCOUNT_SUBJECT,
				new HashMap<String, Object>(1));
		mailServerService.sendHtmlMail(enterpriseUser.getName(), mailServer.getId(), enterpriseUser.getEmail(), null,
				null, subject, msg);
	}

	@Override
	public void exportEmployeeList(HttpServletRequest request, HttpServletResponse response,
			EnterpriseUser enterpriseUser, String id) throws IOException {
		byte[] filterByte = enterpriseUser.getDescription().getBytes("ISO8859-1");
		String filterStr = new String(filterByte, "UTF-8");
		byte[] dnByte = enterpriseUser.getUserDn().getBytes("ISO8859-1");
		String dnStr = new String(dnByte, "UTF-8");

		enterpriseUser.setDescription(null);
		enterpriseUser.setUserDn(null);
		if (StringUtils.isNotBlank(filterStr) && !"null".equalsIgnoreCase(filterStr)) {
			enterpriseUser.setDescription(SqlUtils.stringToSqlLikeFields(filterStr));
		}
		if (StringUtils.isNotBlank(dnStr) && !"null".equalsIgnoreCase(dnStr)) {
			enterpriseUser.setUserDn(SqlUtils.stringToSqlLikeFields(dnStr));
		}
		employeesExcelService.exportEmployeeList(request, response, enterpriseUser, id);
	}

	@Override
	public void downloadEmployeeInfoTemplateFile(HttpServletRequest request, HttpServletResponse response,long enterpriseId)
			throws IOException {

		employeesExcelService.downloadEmployeeInfoTemplateFile(request, response,enterpriseId);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public EnterpriseUserEntity createLocalUser(EnterpriseUser enterpriseUser) throws IOException {
		EnterpriseUserEntity userEntity = new EnterpriseUserEntity();
		enterpriseUser.setName(enterpriseUser.getName().trim());
		checkLoginNameLength(enterpriseUser);
		Enterprise enterprise = enterpriseManager.getById(enterpriseUser.getEnterpriseId());
		if (null == enterprise) {
			LOGGER.error("enterprise is null id:" + enterpriseUser.getEnterpriseId());
			throw new BusinessException();
		}
		userEntity.setEnterpriseId(enterprise.getId());

		LoginInfo loginInfoName = loginInfoManager.getByDomain(enterpriseUser.getName(), enterprise.getDomainName());
		LoginInfo loginInfoEmail = loginInfoManager.getByDomain(enterpriseUser.getEmail(), enterprise.getDomainName());

		isAdAuthUser(loginInfoName, loginInfoEmail);

		if (null != loginInfoName && null != loginInfoEmail) {
			enterpriseUser.setId(loginInfoName.getUserId());
			enterpriseUser.setModifiedAt(new Date());
			enterpriseUserService.updateEnterpriseUser(enterpriseUser);
			userEntity.setId(loginInfoName.getUserId());
			userEntity.setFlag(false);
			LOGGER.info("update enterprise user basic info");
			return userEntity;
		}
		if (null != loginInfoName || null != loginInfoEmail) {
			LOGGER.error("create enterprise user fail, name or email already exists");
			throw new ExistUserConflictException();
		}

		List<LoginInfo> loginInfoEmails = loginInfoManager.getByLoginName(enterpriseUser.getEmail());
		List<LoginInfo> loginInfoPhones = loginInfoManager.getByLoginName(enterpriseUser.getMobile());
		if (loginInfoEmails.size() > 0 || loginInfoPhones.size() > 0) {
			LOGGER.error("Create enterprise user fail, mobile or email already exists");
			throw new ExistUserConflictException();
		}

		String password = enterpriseUser.getPassword();
		Date date = new Date();
		enterpriseUser.setCreatedAt(date);
		enterpriseUser.setLastLoginAt(date);
		enterpriseUser.setModifiedAt(date);
		enterpriseUser.setObjectSid(enterpriseUser.getName());
		if (StringUtils.isBlank(password)) {
			password = PasswordGenerateUtil.getRandomPassword();
			if (password.equals(enterpriseUser.getName())) {
				throw new ShaEncryptException();
			}
			try {
				HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
				enterpriseUser.setPassword(hashPassword.getHashPassword());
				enterpriseUser.setIterations(hashPassword.getIterations());
				enterpriseUser.setSalt(hashPassword.getSalt());
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error("create user fail");
				throw new ShaEncryptException();
			} catch (InvalidKeySpecException e) {
				LOGGER.error("create user fail");
				throw new ShaEncryptException();
			}
		}

		long id = enterpriseUserService.create(enterpriseUser);
		userEntity.setId(id);
		userEntity.setFlag(true);

		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setDomainName(enterprise.getDomainName());
		loginInfo.setLoginName(enterpriseUser.getName());
		loginInfo.setEnterpriseId(enterpriseUser.getEnterpriseId());
		loginInfo.setUserId(id);
		loginInfo.setLoginType(LoginInfo.LOGININGO_NAME);
		loginInfoManager.create(loginInfo);

		if (StringUtils.isNotBlank(enterpriseUser.getEmail())) {
			LoginInfo loginInfoByEmail = new LoginInfo();
			loginInfoByEmail.setDomainName(enterprise.getDomainName());
			loginInfoByEmail.setLoginName(enterpriseUser.getEmail());
			loginInfoByEmail.setEnterpriseId(enterpriseUser.getEnterpriseId());
			loginInfoByEmail.setUserId(id);
			loginInfoByEmail.setLoginType(LoginInfo.LOGININGO_EMAIL);
			loginInfoManager.create(loginInfoByEmail);
		}

		if (StringUtils.isNotBlank(enterpriseUser.getMobile())) {
			LoginInfo loginInfoByMobile = new LoginInfo();
			loginInfoByMobile.setDomainName(enterprise.getDomainName());
			loginInfoByMobile.setLoginName(enterpriseUser.getMobile());
			loginInfoByMobile.setEnterpriseId(enterpriseUser.getEnterpriseId());
			loginInfoByMobile.setUserId(id);
			loginInfoByMobile.setLoginType(LoginInfo.LOGININGO_MOBILE);
			loginInfoManager.create(loginInfoByMobile);
		}
		enterpriseUser.setPassword(password);
		sendEmail(enterpriseUser);
		return userEntity;
	}

	private void isAdAuthUser(LoginInfo loginInfoName, LoginInfo loginInfoEmail) {
		EnterpriseUser adUserInfoName = null;
		EnterpriseUser adUserInfoEmail = null;
		if (loginInfoName != null) {
			adUserInfoName = enterpriseUserService.getUserInfo(loginInfoName.getUserId(),
					loginInfoName.getEnterpriseId(), AD_AUTH);
		}
		if (loginInfoEmail != null) {
			adUserInfoEmail = enterpriseUserService.getUserInfo(loginInfoEmail.getUserId(),
					loginInfoEmail.getEnterpriseId(), AD_AUTH);
		}
		if (adUserInfoName != null || adUserInfoEmail != null) {
			LOGGER.error("create enterprise user fail, is adAuth user");
			throw new AdAuthUserConflictException();
		}
	}

	private void checkLoginNameLength(EnterpriseUser enterpriseUser) {
		if (null != enterpriseUser.getName() && (enterpriseUser.getName().length() > EnterpriseUser.NAME_MAX_LENGTH
				)) {
			throw new InvalidParamterException("invalid name length:" + enterpriseUser.getName());
		}
        if (StringUtils.isNotBlank(enterpriseUser.getEmail())) {
			PatternRegUtil.checkMailLegal(enterpriseUser.getEmail());
		}
	}

    @Override
    public void updateEnterpriseUserStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId) {
        enterpriseUserService.updateStatus(userIds, status, enterpriseId);
    }
    
	@Override
	public void migrateData(DataMigrationRequestDto migrationInfo, long enterpriseId, List<String> appIdList) {
		UserAccount departureUserAccount;
		EnterpriseAccount enterpriseAccountInfo = null;
		UserHttpClient userHttpClient = new UserHttpClient(ufmClientService);

		// 离职用户信息
		EnterpriseUser departureEnterpriseUser = enterpriseUserService.get(migrationInfo.getDepartureUserId(),enterpriseId);
		String departureUserLoginName = departureEnterpriseUser.getName();
		String departureUserEmail = departureEnterpriseUser.getEmail();
		String departureMobile = departureEnterpriseUser.getMobile();

		for (String appId : appIdList) {
			try {
				enterpriseAccountInfo = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId); // 企业账户信息
				departureUserAccount  = userAccountService.get(migrationInfo.getDepartureUserId(),enterpriseAccountInfo.getAccountId()); // 离职用户账户信息
				MigrationRecord migrationRecord;
				if (migrationInfo.getMigrationType() == 1) {
					migrateToNewUser(migrationInfo, enterpriseId, departureEnterpriseUser, enterpriseAccountInfo,userHttpClient);
					
					// 移交离职用户所有信息
					migrationRecord = new MigrationRecord(enterpriseId, departureUserAccount.getCloudUserId(),
							DataMigrationRequestDto.CONST_MIGRATION_TO_NEW_USER,
							MigrationRecord.CONST_MIGRATION_TYPE_INIT_SUCCESS);
					migrationRecord.setRecipientCloudUserId(departureUserAccount.getCloudUserId());
					migrationRecord.setId(migrationRecordIdGenerator.getNextRecordId());
				} else {
					UserAccount recipientUserAccount;
					DataMigrationResponseDto responseDto;

					// 接收用戶信息
					recipientUserAccount = userAccountService.get(migrationInfo.getRecipientUserId(),enterpriseAccountInfo.getAccountId());

					if (recipientUserAccount == null) { // 有用户信息，无账户信息，执行目标用户与离职用户数据进行关联
						responseDto = migrateToNoAccountUser(migrationInfo, enterpriseId, departureUserAccount,enterpriseAccountInfo, userHttpClient);

						migrationRecord = new MigrationRecord(enterpriseId, departureUserAccount.getCloudUserId(),
								DataMigrationRequestDto.CONST_MIGRATION_TO_UNOPEN_ACCOUNT_USER,
								MigrationRecord.CONST_MIGRATION_TYPE_INIT_SUCCESS);
						migrationRecord.setRecipientCloudUserId(departureUserAccount.getCloudUserId());
						migrationRecord.setId(migrationRecordIdGenerator.getNextRecordId());
					} else { // 有账户信息，执行数据迁移
						responseDto = migrateToExistAccountUser(migrationInfo, enterpriseAccountInfo, enterpriseId,
								departureUserAccount.getCloudUserId(), departureEnterpriseUser.getName(),
								recipientUserAccount.getCloudUserId(), userHttpClient);

						migrationRecord = new MigrationRecord(enterpriseId, departureUserAccount.getCloudUserId(),
								DataMigrationRequestDto.CONST_MIGRATION_TO_UNOPEN_ACCOUNT_USER,
								MigrationRecord.CONST_MIGRATION_TYPE_INIT_SUCCESS);
						migrationRecord.setRecipientCloudUserId(recipientUserAccount.getCloudUserId());
						migrationRecord.setId(migrationRecordIdGenerator.getNextRecordId());
						migrationRecord.setInodeId(responseDto.getInodeId());
					}
					
					// 新增迁移记录
					Calendar expireDate = Calendar.getInstance();
					int defaultExpireDays = Integer
							.parseInt(PropertiesUtils.getProperty(CONST_DEPARTURE_FILE_EXPIRE_DAYS_KEY, "1"));
					expireDate.add(Calendar.DAY_OF_MONTH, defaultExpireDays);
					migrationRecord.setExpiredDate(expireDate.getTime());
					migrationRecord.setAppId(appId);
					migrationRecord.setDepartureUserId(migrationInfo.getDepartureUserId());
					migrationRecord.setRecipientUserId(migrationInfo.getRecipientUserId());
					migrationRecordService.save(migrationRecord);
				}
			} catch (Exception e) {
				LOGGER.error("[Migration] migrate to user error:" + e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}

		// 删除离职用户登陆信息
		deleteDepartureUserLoginInfo(departureUserEmail, enterpriseId);
		deleteDepartureUserLoginInfo(departureUserLoginName, enterpriseId);
		deleteDepartureUserLoginInfo(departureMobile, enterpriseId);
	}
	
	private void deleteDepartureUserLoginInfo(String loginName, long enterpriseId){
		try {
			loginInfoService.delByNameEnterId(loginName, enterpriseId);
		} catch (Exception e) {
			LOGGER.error("[Migration] deleteDepartureUserLoginInfo error:" + e.getMessage(), e);
		}
	}

	/**
	 * 迁移给已有账户用户
	 * @param migrationInfo
	 * @param enterpriseAccountInfo
	 * @param enterpriseId
	 * @param departureUserId
	 * @param departureUserName
	 * @param recipientUserId
	 * @param userHttpClient
	 * @return
	 */
	private DataMigrationResponseDto migrateToExistAccountUser(DataMigrationRequestDto migrationInfo,
			EnterpriseAccount enterpriseAccountInfo, long enterpriseId, long departureUserId, String departureUserName,
			long recipientUserId, UserHttpClient userHttpClient) {
		migrationInfo.setDepartureCloudUserId(departureUserId);
		migrationInfo.setRecipientCloudUserId(recipientUserId);
		migrationInfo.setDepartureUserName(departureUserName);
		DataMigrationResponseDto responseDto = userHttpClient.migrateData(migrationInfo, enterpriseAccountInfo);

		List<Long> userIdList = new ArrayList<Long>();
		userIdList.add(migrationInfo.getDepartureUserId());
		updateEnterpriseUserStatus(userIdList, EnterpriseUserStatus.DATA_MIGRATED, enterpriseId);

		return responseDto;
	}

	/**
	 * 迁移给没有账户信息的用户
	 * @param migrationInfo
	 * @param enterpriseId
	 * @param departureUserAccount
	 * @param enterpriseAccountInfo
	 * @param userHttpClient
	 * @return
	 */
	private DataMigrationResponseDto migrateToNoAccountUser(DataMigrationRequestDto migrationInfo, long enterpriseId,
			UserAccount departureUserAccount, EnterpriseAccount enterpriseAccountInfo, UserHttpClient userHttpClient) {
		EnterpriseUser recipientEnterpriseUser = enterpriseUserService.get(migrationInfo.getRecipientUserId(),
				enterpriseId);
		/** 接受用户企业信息 */
		migrationInfo.setAlias(recipientEnterpriseUser.getAlias());
		migrationInfo.setName(recipientEnterpriseUser.getName());
		migrationInfo.setEmail(recipientEnterpriseUser.getEmail());
		migrationInfo.setDepartureCloudUserId(departureUserAccount.getCloudUserId());
		migrationInfo.setDescription(recipientEnterpriseUser.getDescription());
		
		DataMigrationResponseDto responseDto = userHttpClient.migrateAccount(migrationInfo, enterpriseAccountInfo);

		departureUserAccount.setUserId(migrationInfo.getRecipientUserId());
		userAccountService.updateUserIdById(departureUserAccount);

		List<Long> userIdList = new ArrayList<Long>();
		userIdList.add(migrationInfo.getDepartureUserId());
		updateEnterpriseUserStatus(userIdList, EnterpriseUserStatus.ACCOUNT_MIGRATED, enterpriseId);

		return responseDto;
	}

	/**
	 * 迁移给一个完全的新用户
	 * @param migrationInfo
	 * @param enterpriseId
	 * @param departureEnterpriseUser
	 * @param enterpriseAccountInfo
	 * @param userHttpClient
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void migrateToNewUser(DataMigrationRequestDto migrationInfo, long enterpriseId,
			EnterpriseUser departureEnterpriseUser, EnterpriseAccount enterpriseAccountInfo,
			UserHttpClient userHttpClient) throws Exception {
		// 更新企业用户基本信息
		updateEnterpriseUserBaseInfo(migrationInfo, departureEnterpriseUser);
		// 更新企业用户状态
		updateEnterpriseUserStatus(migrationInfo, enterpriseId, enterpriseAccountInfo, userHttpClient);

		// 新增登陆记录
		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		createLoginInfo(enterprise, departureEnterpriseUser.getId(), LoginInfo.LOGININGO_NAME, migrationInfo.getName());
		if (StringUtils.isNotBlank(migrationInfo.getEmail())) {
			createLoginInfo(enterprise, departureEnterpriseUser.getId(), LoginInfo.LOGININGO_EMAIL, migrationInfo.getEmail());
		}

		// 发送邮件
		sendEmail(departureEnterpriseUser);
	}

	private void updateEnterpriseUserStatus(DataMigrationRequestDto migrationInfo, long enterpriseId,
			EnterpriseAccount enterpriseAccountInfo, UserHttpClient userHttpClient) {
		UserAccount departureUserAccount = userAccountService.get(migrationInfo.getDepartureUserId(),
				enterpriseAccountInfo.getAccountId());
		migrationInfo.setDepartureCloudUserId(departureUserAccount.getCloudUserId());
		
		DataMigrationResponseDto responDto = userHttpClient.migrateAccount(migrationInfo, enterpriseAccountInfo);
		if (null != responDto && !responDto.getRetCode().equalsIgnoreCase(HttpStatus.OK.name())){
			LOGGER.error("[Migration] updateEnterpriseUserStatus error:" + responDto.getRetCode());
			throw new RuntimeException("Update user status error");
		}

		List<Long> userIds = new ArrayList<Long>(1);
		userIds.add(migrationInfo.getDepartureUserId());
		enterpriseUserService.updateStatus(userIds, EnterpriseUserStatus.NORMAL, enterpriseId);
	}

	/**
	 * 新增用户登陆信息
	 * @param enterprise
	 * @param departureUserId
	 * @param loginType
	 * @param loginName
	 * @return
	 */
	private Enterprise createLoginInfo(Enterprise enterprise, long departureUserId, byte loginType, String loginName) {
		LoginInfo logininfoByName = new LoginInfo();
		logininfoByName.setDomainName(enterprise.getDomainName());
		logininfoByName.setLoginName(loginName);
		logininfoByName.setEnterpriseId(enterprise.getId());
		logininfoByName.setUserId(departureUserId);
		logininfoByName.setLoginType(loginType);

		loginInfoManager.create(logininfoByName);

		return enterprise;
	}

	/**
	 * 更新企业用户信息
	 * @param migrationInfo
	 * @param departureEnterpriseUser
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws UnsupportedEncodingException
	 */
	private void updateEnterpriseUserBaseInfo(DataMigrationRequestDto migrationInfo, EnterpriseUser departureEnterpriseUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException {
		String password = PasswordGenerateUtil.getRandomPassword();
		HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
		
		departureEnterpriseUser.setPassword(hashPassword.getHashPassword());
		departureEnterpriseUser.setIterations(hashPassword.getIterations());
		departureEnterpriseUser.setSalt(hashPassword.getSalt());
		departureEnterpriseUser.setName(migrationInfo.getName());
		departureEnterpriseUser.setAlias(migrationInfo.getAlias());
		departureEnterpriseUser.setEmail(migrationInfo.getEmail());
		departureEnterpriseUser.setMobile(migrationInfo.getMobile());
		departureEnterpriseUser.setObjectSid(migrationInfo.getName());
		
		enterpriseUserService.update(departureEnterpriseUser);
	}

	@Override
	public boolean isMigratedForUser(long enterpriseId, long departureUserId) {
		return migrationRecordService.isMigratedForUser(enterpriseId, departureUserId);
	}

	@Override
	public void updateDepartmentInfo(long enterpriseId, long authServerId, long deptId, String dn, String filter,
			String enterpriseUserIds, String sessionId,String appId) {
		try {
			enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		} catch (Exception e) {
			throw e;
		}
		String[] idArray = enterpriseUserIds.split(",");
		if ("all".equalsIgnoreCase(enterpriseUserIds)) {
			idArray = getAllUserId(enterpriseId, authServerId, dn, filter, sessionId);
		}

		for (String id : idArray) {
			enterpriseUserService.changeDepartment(Long.parseLong(id), enterpriseId, deptId);
		}
	}

	@Override
	public boolean checkOrganizeEnabled(long enterpriseId) {
		return enterpriseManager.checkOrganizeEnabled(enterpriseId);
	}

	@Override
	public EnterpriseSecurityPrivilege getDeptManager(long enterpriseId, long deptId) {
		return privilegeService.getDeptDirector(enterpriseId, deptId);
	}

	@Override
	public void addDeptManager(long enterpriseId, long deptId, long enterpriseUserId,String appId) {
		teamspaceChangeOwer(enterpriseUserId,enterpriseId,deptId,appId);
		privilegeService.addDeptDirector(enterpriseId, deptId, enterpriseUserId);
		
	}

	@Override
	public void updateDepartment(long userId, long enterpriseId, long deptId,String appId) {
		enterpriseUserService.changeDepartment(userId, enterpriseId, deptId);
	}

	@Override
	public List<EnterpriseUser> listInfoSecurityManager(long enterpriseId) {
		return privilegeService.listInfoSecurityManager(enterpriseId);
	}

	@Override
	public void setInfoSecurityManager(long enterpriseId, long enterpriseUserId,long deptId) {
		privilegeService.setInfoSecurityManager(enterpriseId, enterpriseUserId,deptId);
	}

	@Override
	public EnterpriseUser getArchiveOwner(long enterpriseId, Long deptId) {
		List<EnterpriseUser> list = privilegeService.getWithFilter(enterpriseId, deptId, null, null, null,null, EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER);

		//正常情况下，一个部门下只存在一个知识管理员
		if(list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}
	
	@Override
	public EnterpriseSecurityPrivilege getPrivilege(EnterpriseSecurityPrivilege  securityPrivilege) {

		return privilegeService.get(securityPrivilege);
	}


	
	@Override
	public Page<EnterpriseUser> getPagedSecurityPrivilege(long enterpriseId, Long deptId, String authServerId, String filter, PageRequest pageRequest,byte roleType) {
		int total = privilegeService.countWithFilter(enterpriseId, deptId, authServerId, filter,roleType);
		List<EnterpriseUser> content = privilegeService.getWithFilter(enterpriseId, deptId, authServerId, filter, pageRequest.getOrder(), pageRequest.getLimit(),roleType);
        return new PageImpl<>(content, pageRequest, total);
	}


    @Override
    public void addArchiveOwner(long enterpriseId, long deptId, long enterpriseUserId,UserToken userToken) {
        privilegeService.addArchiveOwner(enterpriseId, deptId, enterpriseUserId,userToken);
    }
    
    @Override
    public void deletePrivilege(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege,UserToken userToken) {
    	 //到ufm删除空间权限
    	UserAccount userAccount = userAccountService.get(enterpriseSecurityPrivilege.getEnterpriseUserId(), userToken.getAccountId());
    	DepartmentAccount departmentAccount =departmentAccountService.getByDeptIdAndAccountId(enterpriseSecurityPrivilege.getDepartmentId(),  userToken.getAccountId());
    	try {
//    		 teamspaceDeleteManager(userAccount,enterpriseSecurityPrivilege,departmentAccount,userToken.getAppId());
    			teamSpaceService.deleteTeamSpaceMemberByCloudUserId(departmentAccount.getEnterpriseId(), userToken.getAppId(), departmentAccount.getCloudUserId(), userAccount.getCloudUserId());
		} catch (Exception e) {
			// TODO: handle exception
		}
        privilegeService.deletePrivilege(enterpriseSecurityPrivilege);
    }
    
    @Override
    public void deletePrivilegeOwner(long enterpriseId,long enterpriseUserId,byte roleType) {
        privilegeService.deletePrivilegeOwner(enterpriseId,enterpriseUserId,roleType);
    }

	@Override
	public Page<EnterpriseUser> getPagedEnterpriseUser(String filter, Long authServerId, String deptId,
			long enterpriseId, PageRequest pageRequest, long type,String status) {
		Long departmentId = null;
		if(StringUtils.isNotEmpty(deptId)){
			departmentId=Long.parseLong(deptId);
		}
		int total = enterpriseUserService.getFilterdCount(filter, authServerId, enterpriseId,departmentId,type,status);
		
		List<EnterpriseUser> content = enterpriseUserService.getFilterd(filter, authServerId, departmentId,enterpriseId,
				pageRequest.getOrder(), pageRequest.getLimit(),type,status);
		Page<EnterpriseUser> page = new PageImpl<EnterpriseUser>(content, pageRequest, total);
		return page;
	}
	
	
	private void teamspaceChangeOwer(long enterpriseUserId,long enterpriseId,long deptId,String appId){
		
		long accountId = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId).getAccountId();
		UserAccount userAccount = userAccountService.get(enterpriseUserId, accountId);
		DepartmentAccount departmentAccount =departmentAccountService.getByDeptIdAndAccountId(deptId, accountId);
		ChangeOwnerRequest changeOwnerRequest=new ChangeOwnerRequest();
		changeOwnerRequest.setNewOwnerId(userAccount.getCloudUserId());
		teamSpaceService.changeOwner(departmentAccount.getCloudUserId(),appId, changeOwnerRequest);
	}
	
   private void teamspaceChangeOwer(long enterpriseUserId,long enterpriseId,long deptId,UserToken userToken){
		
		UserAccount userAccount = userAccountService.get(enterpriseUserId, userToken.getAccountId());
		DepartmentAccount departmentAccount =departmentAccountService.getByDeptIdAndAccountId(deptId, userToken.getAccountId());
		ChangeOwnerRequest changeOwnerRequest=new ChangeOwnerRequest();
		changeOwnerRequest.setNewOwnerId(userAccount.getCloudUserId());
		try {
			teamSpaceService.changeOwner(departmentAccount.getCloudUserId(),userToken, changeOwnerRequest);
		} catch (Exception e) {
		}
		
	}

	@Override
	public List<EnterpriseSecurityPrivilege> listSecurityPrivilege(EnterpriseSecurityPrivilege filter,Limit limit) {
		// TODO Auto-generated method stub
		return privilegeService.listSecurityPrivilege(filter,limit);
	}

	@Override
	public void addDeptManager(long enterpriseId, long deptid, long employeeid,UserToken userToken) {
		// TODO Auto-generated method stub
		teamspaceChangeOwer(employeeid,enterpriseId,deptid,userToken);
		privilegeService.addDeptDirector(enterpriseId, deptid, employeeid);
	}

	@Override
	public int listSecurityPrivilegeTotal(EnterpriseSecurityPrivilege filter, Limit limitObj) {
		// TODO Auto-generated method stub
		return privilegeService.listSecurityPrivilegeTotal(filter,limitObj);
	}

}
