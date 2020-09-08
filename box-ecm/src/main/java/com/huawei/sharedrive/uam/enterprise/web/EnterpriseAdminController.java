package com.huawei.sharedrive.uam.enterprise.web;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.authapp.manager.AuthAppManager;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import pw.cdmi.common.domain.AccountConfig;
import com.huawei.sharedrive.uam.enterprise.domain.WebIconPcLogo;
import com.huawei.sharedrive.uam.enterprise.manager.AccountBasicConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.AccountConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.openapi.domain.account.AccountAttribute;
import com.huawei.sharedrive.uam.user.domain.Admin;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping(value = "/enterprise/admin")
public class EnterpriseAdminController extends AbstractCommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseAdminController.class);

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private AuthAppManager authAppManager;

	@Autowired
	private AuthAppService authAppService;

	@Autowired
	private AccountConfigManager accountConfigManager;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	private AccountBasicConfigManager basicConfigManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;
	
	@RequestMapping(value = "enterpriseManage", method = RequestMethod.GET)
	public String enterpriseManage() {

		return "enterprise/admin/enterpriseManageMain";
	}

	@RequestMapping(value = "appManage", method = RequestMethod.GET)
	public String appManage() {

		return "enterprise/admin/app/appManageMain";
	}

	@RequestMapping(value = "enterpriseInfo", method = RequestMethod.GET)
	public String enterList(Model model) {
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		Enterprise enterprise = enterpriseManager.getById(sessAdmin.getEnterpriseId());
		model.addAttribute("enterprise", enterprise);
		return "enterprise/admin/info/enterpriseInfo";
	}
	
	@RequestMapping(value = "storageSetting", method = RequestMethod.GET)
	public String enterAtorageSetting(Model model) {
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		Enterprise enterprise = enterpriseManager.getById(sessAdmin.getEnterpriseId());
		model.addAttribute("enterprise", enterprise);
		return "enterprise/storage/storageSetting";
	}

	@RequestMapping(value = "listAppByAuthentication", method = { RequestMethod.GET })
	public String getByAuthentication() {
		return "enterprise/admin/app/base_config";
	}
	
	@RequestMapping(value = "versionInfo", method = { RequestMethod.GET })
	public String versionInfo() {
		
		return "enterprise/admin/version/version_info";
	}
	
	@RequestMapping(value = "sercurity", method = { RequestMethod.GET })
	public String sercurity() {
		
		return "enterprise/admin/sercurity/sercurity";
	}

	@RequestMapping(value = "listAppByAuthentication", method = { RequestMethod.POST })
	public ResponseEntity<Page<AuthApp>> getByAuthentication(Integer size, Integer page, String token) {
		super.checkToken(token);

		PageRequest request = new PageRequest();
		if (size <= 0) {
			size = 1;
		}
		if (page != null && page <= 0) {
			page = 1;
		}
		request.setSize(size);
		if (page != null) {
			request.setPage(page.intValue());
		}

		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		Page<AuthApp> authAppList = authAppManager.getByAuthentication(sessAdmin.getEnterpriseId(), request);
		AuthApp.htmlEscape(authAppList.getContent());
		return new ResponseEntity<Page<AuthApp>>(authAppList, HttpStatus.OK);
	}

	@RequestMapping(value = "changeEnterpriseInfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changeEnterpriseInfo(Enterprise enterprise, String token, HttpServletRequest req) {
		String[] newCompanyInfo = null;
		String[] oldCompanyInfo = null;
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		Enterprise oldInfo = enterpriseManager.getById(sessAdmin.getEnterpriseId());
		if (oldInfo == null) {
			throw new ConstraintViolationException(null);
		}
		oldCompanyInfo = getDescriptionArray(oldInfo);
		newCompanyInfo = getDescriptionArray(enterprise);
		String validate = enterpriseManager.paramValidate(oldInfo, enterprise);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(req));
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		owner.setLoginName(admin.getLoginName());
		if (null != validate) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_INFO, oldCompanyInfo);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ENTERPRISE_INFO_ERROR, newCompanyInfo);
			return new ResponseEntity(validate, HttpStatus.BAD_REQUEST);
		}
		Set violations = validator.validate(enterprise);
		if (!violations.isEmpty()) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_INFO, oldCompanyInfo);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ENTERPRISE_INFO_ERROR, newCompanyInfo);
			throw new ConstraintViolationException(violations);
		}

		super.checkToken(token);

		Long enterpriseId = checkAdminAndGetId();
		enterprise.setId(enterpriseId);
		try {
			String errMessages = enterpriseManager.updateEnterpriseInfo(enterprise);
			if (null != errMessages) {
				return new ResponseEntity(errMessages, HttpStatus.BAD_REQUEST);
			}
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_INFO, oldCompanyInfo);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ENTERPRISE_INFO, newCompanyInfo);
		} catch (RuntimeException e) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_INFO, oldCompanyInfo);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ENTERPRISE_INFO_ERROR, newCompanyInfo);
		} catch (Exception e) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ENTERPRISE_INFO, oldCompanyInfo);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_ENTERPRISE_INFO_ERROR, newCompanyInfo);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	@RequestMapping(value = "accountEmail/{appId}", method = RequestMethod.GET)
	public String accountEmail(@PathVariable(value = "appId") String appId, Model model) {
		long accountId = getAccoutId(appId);
		List<AccountConfig> list = accountConfigManager.list(accountId);
		AccountConfig configValue;
		for (AccountConfig iter : list) {
			configValue = accountConfigManager.get(accountId, iter.getName());
			if (AccountAttribute.SERVER_RECEIVE.getName().equals(iter.getName())) {
				model.addAttribute("serverReceive", configValue);
				continue;
			}
			if (AccountAttribute.PROTOCOL_RECEIVE.getName().equals(iter.getName())) {
				configValue.setValue(configValue.getValue().toLowerCase(Locale.ENGLISH));
				model.addAttribute("protocolReceive", configValue);
				continue;
			}
			if (AccountAttribute.PORT_RECEIVE.getName().equals(iter.getName())) {
				model.addAttribute("portReceive", configValue);
				continue;
			}
			if (AccountAttribute.SERVER_SEND.getName().equals(iter.getName())) {
				model.addAttribute("serverSend", configValue);
				continue;
			}
			if (AccountAttribute.PROTOCOL_SEND.getName().equals(iter.getName())) {
				configValue.setValue(configValue.getValue().toLowerCase(Locale.ENGLISH));
				model.addAttribute("protocolSend", configValue);
				continue;
			}
			if (AccountAttribute.PORT_SEND.getName().equals(iter.getName())) {
				model.addAttribute("portSend", configValue);
				continue;
			}
		}
		model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
		return "enterprise/admin/app/accountEmail";
	}
	
	
	
	@RequestMapping(value = "accountEmail", method = RequestMethod.POST)
	public ResponseEntity<?> accountEmailReceive(String emailConfig, String appId, boolean isReceive, String token,
			HttpServletRequest req) {
		super.checkToken(token);
		String[] description = new String[] { getEnterpriseName(), appId, emailConfig };
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setAppId(appId);
		owner.setIp(IpUtils.getClientAddress(req));
		String[] arrEmailConfig = emailConfig.split(",");
		if (arrEmailConfig[0].split(";").length != 3||arrEmailConfig[1].split(";").length!=3) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCOUNT_ATTRIBUTES_EMAIL_ERROR, description);
			return new ResponseEntity<String>(ErrorCode.INVALID_PARAMTER.name(), HttpStatus.BAD_REQUEST);
		}
		AdminLogType adminlogType;
		try {
			
				accountConfigManager.configEmailReceive(getAccoutId(appId), arrEmailConfig[0].split(";"));
				adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_RECA_ATTRBUTES_EMAIL.getTypeCode());
			
				accountConfigManager.configEmailSend(getAccoutId(appId),  arrEmailConfig[1].split(";"));
				adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_SEND_ATTRBUTES_EMAIL.getTypeCode());
			
			adminLogManager.saveAdminLog(owner, adminlogType, description);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (RuntimeException e) {
			LOGGER.error("save arr fail");
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ACCOUNT_ATTRIBUTES_EMAIL_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}
    //基础配置信息
	@RequestMapping(value = "basicconfig/config/{appId}", method = RequestMethod.GET)
	public ResponseEntity<AccountBasicConfig> basicConfig(@PathVariable(value = "appId") String appId, Model model) {
        long accountId = getAccoutId(appId);
        AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
        accountBasicConfig.setAccountId(accountId);
        AccountBasicConfig basicConfig = basicConfigManager.queryAccountBasicConfig(accountBasicConfig,appId);
        appId = HtmlUtils.htmlEscape(appId);
        AccountBasicConfig.htmlEscape(basicConfig);
       // basicConfigManager.convertToNull(basicConfig);
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        model.addAttribute("appBasicConfig", basicConfig);
        return new ResponseEntity<AccountBasicConfig>(basicConfig,HttpStatus.OK);
    }

	@RequestMapping(value = "basicconfig/save", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> saveBasicConfig(AccountBasicConfig appBasicConfig, String appId, String token,
			HttpServletRequest request) {
		long accountId = getAccoutId(appId);
		appBasicConfig.setAccountId(accountId);
		String[] description = new String[] { String.valueOf(accountId),
				String.valueOf(appBasicConfig.getUserSpaceQuota()), String.valueOf(appBasicConfig.getUserVersions()),
				String.valueOf(appBasicConfig.isEnableTeamSpace()), String.valueOf(appBasicConfig.getMaxTeamSpaces()),
				String.valueOf(appBasicConfig.getTeamSpaceQuota()),
				String.valueOf(appBasicConfig.getTeamSpaceVersions()) };

		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(checkAdminAndGetId());
		owner.setIp(IpUtils.getClientAddress(request));
		owner.setAppId(appId);
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		owner.setLoginName(admin.getLoginName());

//		EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(checkAdminAndGetId(), appId);
//		if (enterpriseAccount.getMaxSpace() < Long.parseLong(appBasicConfig.getUserSpaceQuota())*1024
//				|| enterpriseAccount.getMaxTeamspace() < Long.parseLong(appBasicConfig.getMaxTeamSpaces())
//				|| enterpriseAccount.getMaxSpace() < Long.parseLong(appBasicConfig.getTeamSpaceQuota())*1024) {
//			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
//			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//		}
		Set<ConstraintViolation<AccountBasicConfig>> violations = validator.validate(appBasicConfig);
		if (!violations.isEmpty()) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
//		if (!checkAppBasicConfig(appBasicConfig)) {
//			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
//			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//		}
		if (!checkParam(appId)) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
//		if (basicConfigManager.paramCheck(appBasicConfig)) {
//			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
//			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
//		}
		super.checkToken(token);
		try {
			// enter
			basicConfigManager.addAccountBasicConfig(appBasicConfig);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG, description);
		} catch (Exception e) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BASIC_CONFIG_ERROR, description);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	
	  @RequestMapping(value = "openOrganization", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> openOrganization(String token){
	    	  super.checkToken(token);
	    	  Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
	          Enterprise enterprise = enterpriseManager.getById(sessAdmin.getEnterpriseId());
	          if (null == enterprise)
	          {
	        	  throw new InvalidParameterException();
	          }
	    	enterprise.setIsdepartment(true);
			enterpriseManager.updateEnterprise(enterprise);
			return new ResponseEntity<>(HttpStatus.OK);
	    }
	  
	  
	  @RequestMapping(value = "isOpenOrganization", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> isOpenOrganization(String token){
	    	  super.checkToken(token);
	    	  Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
	          Enterprise enterprise = enterpriseManager.getById(sessAdmin.getEnterpriseId());
	          if (null == enterprise)
	          {
	        	  throw new InvalidParameterException();
	          }
			return new ResponseEntity<>(enterprise.isIsdepartment(),HttpStatus.OK);
	    }
	  
	  @RequestMapping(value = "basicconfig/defaultAppId", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> basicconfigAppId(String token){
	    	  super.checkToken(token);
	    	  Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
	    	  AuthApp  authApp =authAppService.getDefaultWebApp();
	          if (null == authApp)
	          {
	        	  throw new InvalidParameterException();
	          }
			return new ResponseEntity<>(authApp.getAuthAppId(),HttpStatus.OK);
	    }
	  

	private boolean checkParam(String appId) {
		AuthApp authApp = authAppService.getByAuthAppID(appId);
		if (authApp == null) {
			return false;
		}
		return true;
	}

	private boolean checkAppBasicConfig(AccountBasicConfig appBasicConfig) {
		if ("0".equals(appBasicConfig.getUserSpaceQuota()) || appBasicConfig.getUserSpaceQuota() == null) {
			return false;
		}

		if ("0".equals(appBasicConfig.getUserVersions()) || appBasicConfig.getUserVersions() == null) {
			return false;
		}

		if (appBasicConfig.isEnableTeamSpace()) {
			if (appBasicConfig.getMaxTeamSpaces() == null || appBasicConfig.getTeamSpaceQuota() == null
					|| appBasicConfig.getTeamSpaceVersions() == null) {
				return false;
			}

			if ("0".equals(appBasicConfig.getTeamSpaceQuota()) || "0".equals(appBasicConfig.getTeamSpaceVersions())) {
				return false;
			}
		}
		return true;
	}

	private String[] getDescriptionArray(Enterprise enterprise) {
		String[] description = null;
		String name = enterprise.getName();
		String domain = enterprise.getDomainName();
		String email = enterprise.getContactEmail();
		String person = enterprise.getContactPerson();
		String phone = enterprise.getContactPhone();
		description = new String[] { name, domain, email, person, phone };
		return description;
	}
	 

}
