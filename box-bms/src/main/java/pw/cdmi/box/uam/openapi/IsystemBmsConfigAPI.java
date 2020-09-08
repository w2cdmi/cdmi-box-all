package pw.cdmi.box.uam.openapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.uam.accountrole.service.AccountRoleService;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseAccountManager;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.httpclient.domain.RestEnterpriseAccountRequest;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.openapi.domain.AdminAccount;
import pw.cdmi.box.uam.openapi.domain.BmsUser;
import pw.cdmi.box.uam.openapi.domain.RestAdmins;
import pw.cdmi.box.uam.openapi.domain.RestEnterprise;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.AccessAddressService;
import pw.cdmi.box.uam.system.service.AppBasicConfigService;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.manager.AdminManager;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.user.service.UserService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.PasswordGenerateUtil;
import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/api/v2/isystem")
public class IsystemBmsConfigAPI {

	@Autowired
	private MailServerService mailService;

	@Autowired
	private AccessAddressService accessAddressService;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private AdminService adminService;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	private AppBasicConfigService appBasicConfigService;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountRoleService accountRoleService;

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> addUser(@RequestHeader("Authorization") String authorization,
			@RequestBody MailServer mailServer) {
		return null;
	}

	@RequestMapping(value = "/config/mail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> configBmsMail(@RequestBody MailServer request) {
		if (null == request) {
			throw new BadRquestException("null mailServer");
		}
		if (request.getAppId() != null && request.getAppId().length() > 0) {
			request.setDefaultFlag(false);
			MailServer ms = mailService.getMailServerByAppId(request.getAppId());
			if (ms == null) {
				mailService.saveMailServer(request);
			} else {
				request.setId(ms.getId());
				mailService.updateMailServer(request);
			}
		} else {
			request.setDefaultFlag(true);
			MailServer ms = mailService.getDefaultMailServer();
			if (ms == null) {
				mailService.saveMailServer(request);
			} else {
				request.setId(ms.getId());
				mailService.updateMailServer(request);
			}
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/config/accessAddress", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> configBmsAccessAddress(@RequestBody AccessAddressConfig accessAddressConfig) {

		String uamOuterAddress = accessAddressConfig.getUamOuterAddress();
		String uamInnerAddress = accessAddressConfig.getUamInnerAddress();
		String ufmInnerAddress = accessAddressConfig.getUfmInnerAddress();
		String ufmOuterAddress = accessAddressConfig.getUfmOuterAddress();
		if (!"/".equals(uamOuterAddress.substring(uamOuterAddress.length() - 1, uamOuterAddress.length()))
				&& !"\\".equals(uamOuterAddress.substring(uamOuterAddress.length() - 1, uamOuterAddress.length()))) {
			accessAddressConfig.setUamOuterAddress(uamOuterAddress + "/");
		}
		if (!"/".equals(uamInnerAddress.substring(uamInnerAddress.length() - 1, uamInnerAddress.length()))
				&& !"\\".equals(uamInnerAddress.substring(uamInnerAddress.length() - 1, uamInnerAddress.length()))) {
			accessAddressConfig.setUamInnerAddress(uamInnerAddress + "/");
		}
		if (!"/".equals(ufmInnerAddress.substring(ufmInnerAddress.length() - 1, ufmInnerAddress.length()))
				&& !"\\".equals(ufmInnerAddress.substring(ufmInnerAddress.length() - 1, ufmInnerAddress.length()))) {
			accessAddressConfig.setUfmInnerAddress(ufmInnerAddress + "/");
		}
		if (!"/".equals(ufmOuterAddress.substring(ufmOuterAddress.length() - 1, ufmOuterAddress.length()))
				&& !"\\".equals(ufmOuterAddress.substring(ufmOuterAddress.length() - 1, ufmOuterAddress.length()))) {
			accessAddressConfig.setUfmOuterAddress(ufmOuterAddress + "/");
		}
		accessAddressService.saveAccessAddress(accessAddressConfig);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/find/accessAddress", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<AccessAddressConfig> findBmsAccessAddress() {

		AccessAddressConfig access = accessAddressService.getAccessAddress();
		return new ResponseEntity<AccessAddressConfig>(access, HttpStatus.OK);
	}

	@RequestMapping(value = "/list/bms/admin", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<RestAdmins> listAllAdmin() {
		List<Admin> admins = adminService.getAllAdmin();
		RestAdmins restAdmins = new RestAdmins();
		List<AdminAccount> adminAccounts = new ArrayList<AdminAccount>();
		for (Admin admin : admins) {
			AdminAccount account = new AdminAccount();
			account.setLoginName(admin.getLoginName());
			account.setName(admin.getName());
			// admin.setPassword(roleToI18n(admin.getRoles(), locale));
			if (admin.getRoleNames().equals("ADMIN_MANAGER")) {
				account.setType((byte) 3);
			} else {
				account.setType((byte) 4);
			}
			adminAccounts.add(account);
		}
		restAdmins.setAdmins(adminAccounts);
		return new ResponseEntity<RestAdmins>(restAdmins, HttpStatus.OK);
	}

	@RequestMapping(value = "/config/bms/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> configUserLocal(@RequestBody BmsUser user) {
		MailServer mailServer = mailServerService.getDefaultMailServer();
		if (mailServer == null) {
			return new ResponseEntity<String>("MailServerNotExist", HttpStatus.BAD_REQUEST);
		}
		Admin admin = new Admin();
		admin.setLoginName(user.getLoginName());
		admin.setName(user.getName());
		admin.setEmail(user.getEmail());
		admin.setStatus(Admin.STATUS_ENABLE);
		Set<AdminRole> aRoles = new HashSet<AdminRole>();
		for (AdminRole value : AdminRole.values()) {
			if (value.equals(AdminRole.ADMIN_MANAGER) || value.equals(AdminRole.ENTERPRISE_MANAGER)) {
				continue;
			}
			aRoles.add(value);
		}
		admin.setRoles(aRoles);
		admin.setNoteDesc(user.getNoteDesc());
		String randomPassword = PasswordGenerateUtil.getRandomPassword();
		admin.setPassword(randomPassword);
		admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);
		admin.setType(Admin.ROLE_MANAGER);
		try {
			if (adminService.getAdminByLoginName(admin.getLoginName()) == null) {
				adminManager.create(admin);
				String link = Constants.SERVICE_URL + "login";
				admin.setPassword(randomPassword);
				sendEmail(admin, link);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private void sendEmail(Admin admin, String link) throws IOException {
		MailServer mailServer = mailServerService.getDefaultMailServer();
		if (mailServer == null) {
			throw new BusinessException();
		}
		Map<String, Object> messageModel = new HashMap<String, Object>(3);
		messageModel.put("username", admin.getName());
		messageModel.put("loginName", admin.getLoginName());
		messageModel.put("password", admin.getPassword());
		messageModel.put("link", link);
		String msg = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_CONTENT, messageModel);
		String subject = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_SUBJECT,
				new HashMap<String, Object>(1));
		mailServerService.sendHtmlMail(mailServer.getId(), admin.getEmail(), null, null, subject, msg);
	}

	@RequestMapping(value = "/config/bmsApp/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> configAuthApp(@RequestBody AuthApp authApp) {
		try {
			authAppService.create(authApp);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/config/enterprise/admin/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> configEnterpriseAdmin(@RequestHeader("contactEmail") String contactEmail,
			@RequestHeader("language") String language, @RequestHeader("appId") String appId) {
		Locale locale = null;
		if ("zh_CN".equals(language) || "zh".equals(language)) {
			locale = Locale.CHINA;
		} else {
			locale = Locale.ENGLISH;
		}
		long enterpriseId = 0;
		try {
			Enterprise enterprise = enterpriseManager.getByContactEmail(contactEmail);
			enterpriseManager.createEnterpriseAdmin(enterprise, locale);
			enterpriseId = enterprise.getId();
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}

		try {
			RestEnterpriseAccountRequest enterpriseAccountRequest = new RestEnterpriseAccountRequest();

			enterpriseAccountRequest.setAuthAppId(appId);
			enterpriseAccountRequest.setEnterpriseId(enterpriseId);
			enterpriseAccountRequest.setMaxMember(-1);
			enterpriseAccountRequest.setMaxTeamspace(-1);
			enterpriseAccountRequest.setMaxSpace(-1);
			EnterpriseAccount account = enterpriseAccountManager.getByEnterpriseApp(enterpriseId, appId);
			if (account == null) {
				enterpriseAccountManager.create(enterpriseAccountRequest);
				accountRoleService.configAllRoles(appId, enterpriseId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/config/enterprise/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Long> configEnterprise(@RequestBody RestEnterprise restEnterprise) {
		long enterpriseId = 0;
		try {
			Enterprise enterprise = new Enterprise();
			enterprise.setName(restEnterprise.getName());
			enterprise.setDomainName(restEnterprise.getDomainName());
			if (restEnterprise.getContactEmail() == null) {
				enterprise.setContactEmail("temp@xx.com");
			} else {
				enterprise.setContactEmail(restEnterprise.getContactEmail());
			}
			enterpriseId = enterpriseManager.createEnterprise(enterprise);
		} catch (IOException e) {
			return new ResponseEntity<Long>(HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<Long>(enterpriseId, HttpStatus.OK);
	}

	@RequestMapping(value = "/config/enterprise/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Long> updateEnterprise(@RequestBody RestEnterprise restEnterprise) {
		try {
			Enterprise enterprise = enterpriseManager.getById(restEnterprise.getId());
			enterprise.setName(restEnterprise.getName());
			enterprise.setDomainName(restEnterprise.getDomainName());
			if (restEnterprise.getContactEmail() == null) {
				enterprise.setContactEmail("temp@xx.com");
			}
			enterprise.setContactEmail(restEnterprise.getContactEmail());
			enterpriseManager.updateEnterprise(enterprise);
		} catch (IOException e) {
			return new ResponseEntity<Long>(HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<Long>(restEnterprise.getId(), HttpStatus.OK);
	}

	@RequestMapping(value = "/find/enterprise", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Enterprise> findEnterpriseById(@RequestHeader("enterpriseId") String id) {
		if(StringUtils.isBlank(id)){
			return new ResponseEntity<Enterprise>(HttpStatus.BAD_REQUEST);
		}else{
			Enterprise enterprise = enterpriseManager.getById(Long.valueOf(id));
			return new ResponseEntity<Enterprise>(enterprise, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/find/carrier", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Enterprise> findEnterpriseByOwnerId(@RequestHeader("ownerId") String ownerId) {
		if(StringUtils.isBlank(ownerId)){
			return new ResponseEntity<Enterprise>(HttpStatus.BAD_REQUEST);
		}else {
			Enterprise enterprise = enterpriseManager.getByOwnerId(Long.valueOf(ownerId));
			return new ResponseEntity<Enterprise>(enterprise, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/save/appBasic", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> save(@RequestHeader("appId") String appId) {
		List<RestRegionInfo> regionList = userService.getRegionInfo(appId);
		try {
			AppBasicConfig appBasicConfig = new AppBasicConfig();
			appBasicConfig.setAppId(appId);
			appBasicConfig.setEnableTeamSpace(true);
			appBasicConfig.setTeamSpaceQuota((long) -1);
			appBasicConfig.setTeamSpaceMaxMembers(-1);
			appBasicConfig.setMaxTeamSpaces(-1);
			appBasicConfig.setMaxFileVersions(-1);
			appBasicConfig.setUserSpaceQuota((long) -1);
			appBasicConfig.setUploadBandWidth((long) -1);
			appBasicConfig.setDownloadBandWidth((long) -1);
			if (regionList.size() > 0) {
				appBasicConfig.setUserDefaultRegion(regionList.get(0).getId());
			}
			appBasicConfigService.saveAppBasicConfig(appBasicConfig);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
