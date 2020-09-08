/**
 * 
 */
package com.huawei.sharedrive.isystem.user.web;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.ExceptionUtil;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.core.utils.IpUtils;

/**
 * @author s00108907
 * 
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController extends AbstractCommonController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AdminUpdateService adminUpdateService;

	/**
	 * 打开首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "enterChange", method = RequestMethod.GET)
	public String enter() {
		return "common/localUserChgPwd";
	}

	/**
	 * 初次登陆修改密码
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "initChangePwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> initChangePwd(Admin inputAdmin, HttpServletRequest request, String token) {
		super.checkToken(token);
		UserLog userLog = userLogService.initUserLog(request, UserLogType.USER_PASSWORD, null);
		userLogService.saveUserLog(userLog);
		if (!FormValidateUtil.isValidEmail(inputAdmin.getEmail())) {
			userLog.setDetail(UserLogType.USER_PASSWORD.getCommonErrorParamDetails(null));
			userLog.setType(UserLogType.USER_PASSWORD.getValue());
			userLogService.update(userLog);
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}

		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		String loginIP = IpUtils.getClientAddress(request);
		if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles())
				&& (admin.getLastLoginTime() == null || (admin.getEmail() == null || admin.getEmail().length() == 0))) {
			inputAdmin.setId(admin.getId());
			inputAdmin.setLoginName(admin.getLoginName());
			try {
				adminUpdateService.changeAdminPwdByInitLogin(inputAdmin, request, loginIP);
			} catch (Exception e) {
				return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e), HttpStatus.BAD_REQUEST);
			}
		} else {
			userLog.setDetail(UserLogType.USER_PASSWORD.getErrorDetails(null));
			userLog.setType(UserLogType.USER_PASSWORD.getValue());
			userLogService.update(userLog);
			// 越权
			throw new ConstraintViolationException(new HashSet(1));
		}
		userLog.setDetail(UserLogType.USER_PASSWORD.getDetails(null));
		userLog.setLevel(UserLogService.SUCCESS_LEVEL);
		userLogService.update(userLog);
		SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
		return new ResponseEntity(HttpStatus.OK);
	}

	/**
	 * 登陆后修改密码
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "changePwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changePwd(Admin inputAdmin, HttpServletRequest request, String token) {
		UserLog userLog = userLogService.initUserLog(request, UserLogType.USER_PASSWORD, null);
		userLogService.saveUserLog(userLog);
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();

		super.checkToken(token);

		if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles())) {
			inputAdmin.setId(admin.getId());
			inputAdmin.setLoginName(admin.getLoginName());
			try {
				adminUpdateService.changeAdminPwd(inputAdmin, request);
			} catch (Exception e) {
				return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e), HttpStatus.BAD_REQUEST);
			}
		} else {
			userLog.setDetail(UserLogType.USER_PASSWORD.getCommonErrorParamDetails(null));
			userLog.setType(UserLogType.USER_PASSWORD.getValue());
			userLogService.update(userLog);
			// 越权
			throw new ConstraintViolationException(new HashSet(1));
		}
		userLog.setDetail(UserLogType.USER_PASSWORD.getDetails(null));
		userLog.setLevel(UserLogService.SUCCESS_LEVEL);
		userLogService.update(userLog);
		return new ResponseEntity(HttpStatus.OK);
	}

	private boolean isRoleValid(Set<AdminRole> roleSet) {
		AdminRole[] roles = AdminRole.values();
		for (AdminRole role : roles) {
			if (roleSet.contains(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 进入修改管理员邮箱
	 */
	@RequestMapping(value = "enteremail", method = RequestMethod.GET)
	public String enterSetEmail(Model model) {
		Admin cacheAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		Admin localAdmin = adminService.get(cacheAdmin.getId());
		model.addAttribute("email", localAdmin.getEmail());
		return "common/changeEmail";
	}

	/**
	 * 修改管理员邮箱
	 * 
	 * @param admin
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "setemail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> setEmail(String email, String password, HttpServletRequest request, String token) {

		UserLog userLog = userLogService.initUserLog(request, UserLogType.USER_MAIL, null);
		userLogService.saveUserLog(userLog);
		Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
		super.checkToken(token);
		if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles())) {
			boolean bool = StringUtils.isNotEmpty(password);
			if (bool) {
				HashPassword hashPassword = new HashPassword();
				hashPassword.setHashPassword(admin.getPassword());
				hashPassword.setIterations(admin.getIterations());
				hashPassword.setSalt(admin.getSalt());
				bool = HashPasswordUtil.validatePassword(password, hashPassword);
			}
			if (!FormValidateUtil.isValidEmail(email) || !bool) {
				userLog.setDetail(UserLogType.USER_MAIL.getCommonErrorParamDetails(null));
				userLog.setType(UserLogType.USER_MAIL.getValue());
				userLogService.update(userLog);
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			}
			adminUpdateService.updateEmail(admin.getId(), email);
			admin.setEmail(email);
		} else {
			userLog.setDetail(UserLogType.USER_MAIL.getCommonErrorParamDetails(null));
			userLog.setType(UserLogType.USER_MAIL.getValue());
			userLogService.update(userLog);
			// 越权
			throw new ConstraintViolationException(new HashSet(1));
		}

		userLog.setDetail(UserLogType.USER_MAIL.getDetails(null));
		userLog.setLevel(UserLogService.SUCCESS_LEVEL);
		userLogService.update(userLog);
		return new ResponseEntity(HttpStatus.OK);
	}

}
