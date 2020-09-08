package com.huawei.sharedrive.uam.user.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.BadRequestException;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.logininfo.manager.LoginInfoManager;
import com.huawei.sharedrive.uam.openapi.domain.user.ForgetPwdRequest;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.FormValidateUtil;
import com.huawei.sharedrive.uam.util.OneTimePasswordAlgorithm;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;
import com.huawei.sharedrive.uam.util.custom.ForgetPwdUtils;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DigestUtil;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/syscommon")
public class ResetPasswordController extends AbstractCommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordController.class);

	private static final int RESET_LINK_EXPRISE_TIME = Integer
			.parseInt(PropertiesUtils.getProperty("session.expire", "600000"));

	@Autowired
	private AdminService adminService;

	@Autowired
	private MailServerService mailServerService;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	private LoginInfoManager loginInfoManager;

	@Autowired
	private RestClient bmsClientService;

	@Autowired
	private CacheClient cacheClient;

	@RequestMapping(value = "enterforget", method = RequestMethod.GET)
	public String enterForgetPwd() {
		if (!ForgetPwdUtils.enableForget()) {
			throw new BadRequestException("Do not enalbe the funciton to forget pwd.");
		}
		return "anon/forgetPwd";
	}

	@RequestMapping(value = "sendlink", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> sendResetLink(HttpServletRequest request, String loginName, String captcha, String email,
			String token) throws IOException {
		super.checkToken(token);
		if (!ForgetPwdUtils.enableForget()) {
			throw new BadRequestException("ForgetPwdClosedException", "Do not enalbe the funciton to forget pwd.");
		}
		if (StringUtils.isBlank(loginName) || StringUtils.isBlank(captcha) || !FormValidateUtil.isValidEmail(email)) {
			LOGGER.error("loginName or captcha or email is invalid");
			throw new BadRequestException();
		}
		Session session = SecurityUtils.getSubject().getSession();
		Object captchaInSession = session.getAttribute(Constants.HW_VERIFY_CODE_CONST);
		session.setAttribute(Constants.HW_VERIFY_CODE_CONST, "");
		if (captcha.length() != 4
				|| !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString())) {
			throw new BadRequestException();
		}
		LogOwner owner = new LogOwner();
		owner.setIp(IpUtils.getClientAddress(request));
		owner.setLoginName(loginName);
		try {

			Admin admin = adminService.getAdminByLoginName(loginName);

			if (admin == null) {
				LOGGER.error("loginName is not exist!");
				throw new BadRequestException();
			}

			if (admin.getDomainType() != Constants.DOMAIN_TYPE_LOCAL) {
				LOGGER.error("loginName is not local user,only local user can use email to reset password!");
				throw new BadRequestException();
			}
			if (admin.getType() != Constants.ROLE_ENTERPRISE_ADMIN) {
				throw new BadRequestException();
			}
			if (!email.equals(admin.getEmail())) {
				LOGGER.error("loginName and email don't match");
				throw new BadRequestException();
			}
			String validateKey = RandomKeyGUID.getSecureRandomGUID();
			Map<String, String> encodeMap = EDToolsEnhance.encode(validateKey);
			LOGGER.info("set crypt in uam.ResetPassword");
			adminService.updateValidKeyAndDynamicPwd(admin.getId(), DigestUtil.digestPassword(validateKey),
					encodeMap.get(EDToolsEnhance.ENCRYPT_KEY));
			String link = Constants.SERVICE_URL + "syscommon/reset?name=" + loginName + "&validateKey="
					+ encodeMap.get(EDToolsEnhance.ENCRYPT_CONTENT);
			sendEmail(admin, link);
		} catch (RuntimeException e) {
			LOGGER.error(loginName + " get reset link failed at ip " + owner.getIp());
			throw e;

		}
		LOGGER.error(loginName + " get reset link success at ip " + owner.getIp());
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "resetmsg", method = RequestMethod.GET)
	public String resetPwdMsg() {
		return "anon/forgetPwdMsg";
	}

	@RequestMapping(value = "initset", method = RequestMethod.GET)
	public String initSetPwd(Model model, String name, String validateKey) {
		validateLink(model, name, validateKey);
		return "anon/initset";
	}

	@RequestMapping(value = "reset", method = RequestMethod.GET)
	public String resetPwd(Model model, String name, String validateKey) {
		if (!ForgetPwdUtils.enableForget()) {
			throw new BadRequestException("Do not enalbe the funciton to forget pwd.");
		}
		validateLink(model, name, validateKey);
		return "anon/reset";
	}

	private void validateLink(Model model, String name, String validateKey) {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(validateKey)) {
			throw new BadRequestException();
		}
		String loginName = name;
		Admin admin = adminService.getAdminByLoginName(loginName);
		if (admin == null || admin.getValidateKey() == null) {
			throw new BadRequestException();
		}
		if (Constants.ROLE_ENTERPRISE_ADMIN != admin.getType()) {
			throw new BadRequestException();
		}

		String realValidateKey = EDToolsEnhance.decode(validateKey, admin.getDynamicPassword());
		if (!admin.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey))) {
        	throw new BadRequestException();
        }

		Date modifiedDate = admin.getResetPasswordAt();
		long modifiedDateSeconds = 0;
		if (null != modifiedDate) {
			modifiedDateSeconds = modifiedDate.getTime();
		}
		long lockDateSeconds = new Date().getTime() - modifiedDateSeconds;
		if (lockDateSeconds > RESET_LINK_EXPRISE_TIME) {
			throw new BadRequestException();
		}

		model.addAttribute("loginName", name);
		model.addAttribute("validateKey", validateKey);
	}

	@RequestMapping(value = "doinitset", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> doInitSet(Admin inputAdmin, String token) throws BadRequestException {
		super.checkToken(token);
		Admin localAdmin = validateNameAndKey(inputAdmin);
		adminService.initSetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "doreset", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> doReset(Admin inputAdmin, HttpServletRequest request, String token)
			throws BadRequestException {
		super.checkToken(token);
		if (!ForgetPwdUtils.enableForget()) {
			throw new BadRequestException("Do not enalbe the funciton to forget pwd.");
		}
		Admin localAdmin = validateNameAndKey(inputAdmin);
		LogOwner owner = new LogOwner();
		owner.setIp(IpUtils.getClientAddress(request));
		owner.setLoginName(inputAdmin.getLoginName());
		String[] description = new String[] { localAdmin.getLoginName(), localAdmin.getEmail() };
		try {
			adminService.resetAdminPwd(localAdmin.getId(), inputAdmin.getPassword());
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_RESET_PWD, description);
		} catch (RuntimeException e) {
			LOGGER.error("save reset password log failed ", e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_RESET_PWD_ERROR, description);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private Admin validateNameAndKey(Admin inputAdmin) throws BadRequestException {
		if (StringUtils.isBlank(inputAdmin.getName()) || StringUtils.isBlank(inputAdmin.getPassword())) {
			throw new BadRequestException();
		}
		String loginName = inputAdmin.getLoginName();
		if (loginName == null || !loginName.equals(inputAdmin.getName())) {
			throw new BadRequestException();
		}
		Admin localAdmin = adminService.getAdminByLoginName(loginName);
		if (localAdmin == null || localAdmin.getValidateKey() == null) {
			throw new BadRequestException();
		}
		String realValidateKey = EDToolsEnhance.decode(inputAdmin.getValidateKey(), localAdmin.getDynamicPassword());
		if (!localAdmin.getValidateKey().equals(DigestUtil.digestPassword(realValidateKey))) {
        	throw new BadRequestException();
        }
		return localAdmin;
	}

	private void sendEmail(Admin admin, String link) throws IOException {
		MailServer mailServer = mailServerService.getDefaultMailServer();
		if (mailServer == null) {
			throw new BusinessException();
		}
		Map<String, Object> messageModel = new HashMap<String, Object>(2);
		messageModel.put("username", admin.getName());
		messageModel.put("link", link);

		String msg = mailServerService.getEmailMsgByTemplate(Constants.RESET_PWD_MAIL_CONTENT, messageModel);
		String subject = mailServerService.getEmailMsgByTemplate(Constants.RESET_PWD_MAIL_SUBJECT,
				new HashMap<String, Object>(1));
		mailServerService.sendHtmlMail(admin.getLoginName(), mailServer.getId(), admin.getEmail(), null, null, subject,
				msg);
	}

	@RequestMapping(value = "validpwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> validPassword(Admin admin, String token) throws BadRequestException {
		super.checkToken(token);
		if (!PasswordValidateUtil.isValidPassword(admin.getPassword())) {
			throw new BadRequestException();
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "validOldpwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> validOldPassword(Admin admin, String token) throws BadRequestException {
		super.checkToken(token);
		if (!PasswordValidateUtil.isValidPassword(admin.getOldPasswd())) {
			throw new BadRequestException();
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "dynamicpwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> validDynamicPassword(String dynamicPwd) throws BadRequestException {
		if (!PasswordValidateUtil.isValidPassword(dynamicPwd)) {
			throw new BadRequestException();
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "checkName", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> checkName(HttpServletRequest request, String contactPhone, String token)
			throws IOException {
		super.checkToken(token);
		boolean checkRes = isExistLoginName(contactPhone);

		return new ResponseEntity<String>("[{isExist:" + String.valueOf(checkRes) + "}]", HttpStatus.OK);
	}

	@RequestMapping(value = "setPasswordByPhone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> resetPassword(ForgetPwdRequest request, HttpServletRequest httpServletRequest,
			String token) {
		super.checkToken(token);
		if (!ForgetPwdUtils.enableForget()) {
			throw new BadRequestException("ForgetPwdClosedException", "Do not enalbe the funciton to forget pwd.");
		}
		String contactPhone = request.getContactPhone();
		if (StringUtils.isBlank(contactPhone) || StringUtils.isBlank(request.getIdentifyCode())) {
			throw new BadRequestException();
		}
		if (contactPhone.length() != 11) {
			throw new BadRequestException();
		}
		// Reset password
		String identifyCode = request.getIdentifyCode();
		String password = request.getPassword();
		String appId = request.getAppId();
		String cache = (String) cacheClient.getCache(contactPhone);
		if (StringUtils.isBlank(contactPhone)  || StringUtils.isBlank(identifyCode)) {
			throw new BadRequestException();
		}
		if (!identifyCode.equals(cache)) {
			throw new BadRequestException();
		}
		cacheClient.deleteCache(contactPhone);

		Admin localAdmin = adminService.getAdminByLoginName(contactPhone);// 目前只支持手机号
		LogOwner owner = new LogOwner();
		owner.setIp(IpUtils.getClientAddress(httpServletRequest));
		owner.setLoginName(contactPhone);
		String[] description = new String[] { localAdmin.getLoginName(), localAdmin.getEmail() };

		try {
			adminService.resetAdminPwd(localAdmin.getId(), password);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INIT_CHANGE_PWD, description);
		} catch (RuntimeException e) {
			LOGGER.error("save reset password log failed ", e);
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INIT_CHANGE_PWD_ERROR, description);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "createAndSendIdentifyCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createIdentifyCode(ForgetPwdRequest request, HttpServletRequest httpServletRequest) {
		if (request == null || request.getContactPhone().length() != 11) {
			throw new BadRequestException("phoneNumber is error");
		}
		// 生成验证码
		String generateOTP = OneTimePasswordAlgorithm.generateOTP();
		boolean setCache = cacheClient.setCache(request.getContactPhone(), generateOTP, 125 * 1000);
		HashMap<Object, Object> map = new HashMap<>();
		map.put("identifyCode", generateOTP);
		map.put("contactPhone", request.getContactPhone());
		// 发送短信
		TextResponse response = bmsClientService.performJsonPostTextResponse("/api/v2/sms/sendIdentifyCode", null, map);
		if (setCache && response.getStatusCode() == 200) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}

	public boolean isExistLoginName(String loginNm) {
		Admin admin = null;
		if (!isEmail(loginNm)) {
			admin = adminService.getAdminByLoginName(loginNm);

		} else {
			admin = adminService.getByEmail(loginNm);
		}

		if (admin != null) {		
			return true;
		}
		return false;
	}

	public boolean isEmail(String loginName) {
		String match = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
		if (loginName.matches(match)) {
			return false;
		}
		return true;
	}

}
