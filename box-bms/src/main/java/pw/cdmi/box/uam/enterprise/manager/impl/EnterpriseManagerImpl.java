package pw.cdmi.box.uam.enterprise.manager.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.authserver.manager.AuthServerManager;
import pw.cdmi.box.uam.enterprise.dao.impl.EnterpriseIdGenerator;
import pw.cdmi.box.uam.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.uam.enterprise.service.EnterpriseService;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.ExistEnterpriseConflictException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.domain.SMSInfo;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.system.service.SMSService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.manager.AdminManager;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.PasswordGenerateUtil;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Component
public class EnterpriseManagerImpl implements EnterpriseManager {
	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private AdminManager adminManager;

	@Autowired
	private AdminService adminService;

	@Autowired
	private EnterpriseIdGenerator enterpriseIdGenerator;

	@Autowired
	private AuthServerManager authServerManager;
	
	@Autowired
	private SMSService smsService;

	@Override
	public long create(Enterprise enterprise, Locale locale) throws IOException {

		if (enterpriseService.isDuplicateValues(enterprise)) {
			throw new ExistEnterpriseConflictException();
		}
		enterprise.setId(enterpriseIdGenerator.getNextId());
		long id = enterpriseService.create(enterprise);

		// Create enterprise bussines administrator
		createEnterpriseAdmin(enterprise, id);

		// Create enterprise local Auth
		createLocalAuth(id, locale);

		return id;

	}

	@Override
	public long createEnterprise(Enterprise enterprise) throws IOException {
		if (enterpriseService.isDuplicateValues(enterprise)) {
			throw new ExistEnterpriseConflictException();
		}
		enterprise.setId(enterpriseIdGenerator.getNextId());
		long id = enterpriseService.create(enterprise);
		return id;
	}
	
	public void updateEnterprise(Enterprise enterprise) throws IOException {
		
		enterpriseService.updateEnterpriseInfo(enterprise);
	}

	@Override
	public void createEnterpriseAdmin(Enterprise enterprise, Locale locale) throws IOException {
		// Create enterprise bussines administrator
		Admin admin = adminService.getAdminByLoginName(enterprise.getContactEmail());
		if (admin == null) {
			createEnterpriseAdmin(enterprise, enterprise.getId());
		}

		// Create enterprise local Auth
		createLocalAuth(enterprise.getId(), locale);
	}

	@Override
	public Page<Enterprise> getFilterd(String filter, Integer status, String appId, PageRequest pageRequest) {
		return enterpriseService.getFilterd(filter, status, appId, pageRequest);
	}

	@Override
	public Enterprise getById(long id) {
		return enterpriseService.getById(id);
	}

	@Override
	public Enterprise getByOwnerId(long id) {
		return enterpriseService.getByOwnerId(id);
	}
	
	@Override
	public long getByDomainExclusiveId(Enterprise enterprise) {

		return enterpriseService.getByDomainExclusiveId(enterprise);
	}

	private void createEnterpriseAdmin(Enterprise enterprise, long id) throws IOException {
		Admin admin = new Admin();
		HashSet<AdminRole> roles = new HashSet<AdminRole>(1);
		roles.add(AdminRole.ENTERPRISE_MANAGER);
		admin.setLoginName(enterprise.getContactEmail());
		admin.setName(enterprise.getContactEmail());
		admin.setEmail(enterprise.getContactEmail());
		admin.setRoles(roles);
		admin.setEnterpriseId(id);
		admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);

		String randomPassword = PasswordGenerateUtil.getRandomPassword();
		admin.setPassword(randomPassword);
		admin.setType(Constants.ROLE_ENTERPRISE_ADMIN);
		Admin oldAdmin = adminService.getAdminByLoginName(enterprise.getContactEmail());
		if (oldAdmin != null) {
			enterpriseService.deleteById(id);
			throw new BadRquestException();
		}
		adminManager.create(admin);

		MailServer mailServer = mailServerService.getDefaultMailServer();
		if (mailServer == null) {
			throw new InvalidParamterException("MailServerNotExist");
		}
		String link = Constants.SERVICE_URL + "login";
		admin.setPassword(randomPassword);
		sendEmail(admin, link);
		
//发送短信
//		smsService.sendSMS("a4a89f483a334227abc033ef509a27db",enterprise.getContactPhone(),password,null);
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

	private void createLocalAuth(long id, Locale locale) {
		AuthServer oldAuthServer = authServerManager.getByEnterpriseIdType(id, AuthServer.AUTH_TYPE_LOCAL);
		String name = OperateDescription.AUTHSERVER_TYPE_LOCAL.getDetails(locale, null);
		String description = OperateDescription.AUTHSERVER_DESCRIPTION_LOCAL.getDetails(locale, null);

		if (null == oldAuthServer) {
			AuthServer authServer = new AuthServer();
			authServer.setType(AuthServer.AUTH_TYPE_LOCAL);
			authServer.setEnterpriseId(id);
			authServer.setName(name);
			authServer.setDescription(description);
			authServerManager.createAuthServer(authServer);
		} else {
			oldAuthServer.setName(name);
			oldAuthServer.setDescription(description);
			authServerManager.updateLocalAuth(oldAuthServer);
		}
	}

	@Override
	public Enterprise getByContactEmail(String email) {
		return enterpriseService.getByContactEmail(email);
	}

}
