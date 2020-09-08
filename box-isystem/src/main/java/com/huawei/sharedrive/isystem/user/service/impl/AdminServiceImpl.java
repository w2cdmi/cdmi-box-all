/**
 * 
 */
package com.huawei.sharedrive.isystem.user.service.impl;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.exception.AuthFailedException;
import com.huawei.sharedrive.isystem.exception.ShaEncryptException;
import com.huawei.sharedrive.isystem.exception.UserLockedException;
import com.huawei.sharedrive.isystem.sysconfig.service.SystemConfigService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.dao.AdminDAO;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.domain.ManagerLocked;
import com.huawei.sharedrive.isystem.user.domain.UserLocked;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.NtlmManagerService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.PasswordValidateUtil;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.alarm.Alarm;
import pw.cdmi.common.alarm.AlarmHelper;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.alarm.ManagerLockedAlarm;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

/**
 * @author d00199602
 * 
 */
@Component
public class AdminServiceImpl implements AdminService {
	private static Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Autowired
	private AdminDAO adminDAO;

	@Autowired
	private AlarmHelper alarmHelper;

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private ManagerLockedAlarm managerLockedAlarm;

	@Autowired
	private NtlmManagerService ntlmManager;

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private UserLogService userLogService;

	/**
	 * 娣诲姞閿佸畾鏁版嵁
	 */
	@Override
	public void addUserLocked(String userName, UserLog userLog) {

		Admin admin = adminDAO.getByLoginName(userName);
		if (null == admin) {
			addAnonUserLocked(userName);

		} else {
			addExistUserLocked(userName, userLog);
		}

	}

	@Override
	public void checkAnonUserLocked(String loginName, UserLog userLog) {
		ManagerLocked anonManagerLocked = (ManagerLocked) cacheClient.getCache(loginName);
		SystemConfig systemconfigCount = systemConfigService.getSystemConfig("lockcount");
		int lockCount = Integer.parseInt(systemconfigCount.getValue());

		if (null != anonManagerLocked) {
			if (anonManagerLocked.getLoginFailTimes() >= lockCount) {
				String msg = "Anonymous user:" + loginName + " has been locked at " + userLog.getClientAddress();
				logger.error(msg);
				throw new UserLockedException();
			}
		}
		throw new AuthFailedException();

	}

	@Override
	public void checkExistUserLocked(String userName, UserLog userLog) throws UserLockedException {
		UserLocked userLocked = ntlmManager.doReadUserLocked(userName);
		SystemConfig systemconfigCount = systemConfigService.getSystemConfig("lockcount");
		SystemConfig systemconfigTime = systemConfigService.getSystemConfig("locktime");
		int lockCount = Integer.parseInt(systemconfigCount.getValue());
		int lockTime = Integer.parseInt(systemconfigTime.getValue()) * 1000;

		if (userLocked == null) {
			return;
		}
		int loginTime = userLocked.getLoginTimes();
		Date lockDate = userLocked.getLoginDate();
		if (lockDate == null) {
			return;
		}
		Date nowDate = new Date();
		long lockDateSeconds = nowDate.getTime() - lockDate.getTime();

		// 瑙ｉ攣 骞�璁板綍鏃ュ織
		if (lockDateSeconds > lockTime && loginTime >= lockCount) {
			ntlmManager.deleteUserLocked(userName);

			logger.warn("user account unlocked for " + userName);
			UserLog unLockUserLog = new UserLog();

			unLockUserLog.setCreatedAt(new Date());
			unLockUserLog.setClientAddress(userLog.getClientAddress());
			unLockUserLog.setId(UUID.randomUUID().toString());

			unLockUserLog.setLoginName(userName);
			unLockUserLog.setLoginName(userName);
			unLockUserLog.setType(UserLogType.ADMIN_LOGIN_UNLOCKED.getValue());
			unLockUserLog.setLevel(UserLogService.SUCCESS_LEVEL);
			unLockUserLog.setDetail(UserLogType.ADMIN_LOGIN_UNLOCKED.getDetails(new String[] { userName }));
			userLogService.saveUserLog(unLockUserLog);
		}

		if (lockDateSeconds <= lockTime && loginTime >= (lockCount)) {

			logger.warn("User account locked for " + userName + ", Will be unlocked automatically in "
					+ (int) (lockTime / 1000 / 60) + " minutes ");
			throw new UserLockedException();
		}

	}

	public void checkUserLocked(String userName, UserLog userLog) {
		Admin admin = adminDAO.getByLoginName(userName);
		if (admin == null) {
			checkAnonUserLocked(userName, userLog);

		} else {
			checkExistUserLocked(userName, userLog);
		}
	}

	@Override
	public void create(Admin admin) {
		Set<AdminRole> roles = admin.getRoles();
		if (null != roles && roles.contains(AdminRole.ADMIN_MANAGER)) {
			List<Admin> adminRoles = adminDAO.getByRole(AdminRole.ADMIN_MANAGER);
			if (adminRoles.size() > 0) {
				throw new InvalidParameterException("roles Excetpion");
			}
		}
		long id = adminDAO.getNextAvailableAdminId();
		admin.setId(id);
		Date now = new Date();
		admin.setCreatedAt(now);
		admin.setModifiedAt(now);
		admin.setType(Constants.ROLE_COMMON_ADMIN);
		adminDAO.create(admin);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(long id) {
		adminDAO.delete(id);
	}

	@Override
	public Admin get(Long id) {
		return adminDAO.get(id);
	}

	@Override
	public Admin getAdminByLoginName(String loginName) {
		return adminDAO.getByLoginNameWithoutCache(loginName);
	}

	@Override
	public List<Admin> getFilterd(Admin filter, Order order, Limit limit) {
		return adminDAO.getFilterd(filter, order, limit);
	}

	@Override
	public Page<Admin> getPagedAdmins(Admin admin, PageRequest pageRequest) {
		return null;
	}

	@Override
	public void initSetAdminPwd(long id, String password) {
		setAdminPwd(id, password);
		adminDAO.updateLastLoginTime(id);
	}

	@Override
	public void isLockUser(String username, UserLocked userLocked, UserLog userLog) {
		SystemConfig systemconfigCount = systemConfigService.getSystemConfig("lockcount");
		SystemConfig systemconfigTime = systemConfigService.getSystemConfig("locktime");
		SystemConfig systemconfigPeriod = systemConfigService.getSystemConfig("lockperiod");
		int lockCount = Integer.parseInt(systemconfigCount.getValue());
		int lockTime = Integer.parseInt(systemconfigTime.getValue()) * 1000;
		int lockPeriod = Integer.parseInt(systemconfigPeriod.getValue()) * 1000;

		if (userLocked == null || userLocked.getLoginTimes() != (lockCount)) {
			return;
		}
		Date lockDate = userLocked.getLoginDate();
		if (lockDate == null) {
			return;
		}
		Date nowDate = new Date();
		long lockDateSeconds = nowDate.getTime() - lockDate.getTime();
		// 閿佸畾 骞�璁板綍鏃ュ織
		if (lockDateSeconds <= lockPeriod) {
			UserLog lockUserLog = new UserLog();
			lockUserLog.setCreatedAt(new Date());
			lockUserLog.setClientAddress(userLog.getClientAddress());
			lockUserLog.setId(UUID.randomUUID().toString());
			lockUserLog.setLoginName(username);
			lockUserLog.setLevel(UserLogService.SUCCESS_LEVEL);
			lockUserLog.setType(UserLogType.ADMIN_LOGIN_LOCKED.getValue());
			lockUserLog.setDetail(UserLogType.ADMIN_LOGIN_LOCKED.getErrorDetails(
					new String[] { username, String.valueOf(lockCount), String.valueOf(lockTime / 1000 / 60) }));
			userLogService.saveUserLog(lockUserLog);
			Alarm alarm = new ManagerLockedAlarm(managerLockedAlarm, username);
			alarmHelper.sendAlarm(alarm);
		} else {
			userLocked.setLoginDate(new Date());
			userLocked.setLoginTimes(1);
			userLocked.setUserName(username);
			ntlmManager.doCreateUserLocked(username, userLocked);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Admin login(String userName, String password, String loginIP) throws AuthFailedException {

		if (StringUtils.isEmpty(password)) {
			throw new AuthFailedException();
		}

		Admin admin = adminDAO.getByLoginNameWithoutCache(userName);

		if (null != admin && admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL) {
			// 鏈湴甯愬彿闇�鏍￠獙瀵嗙爜
			HashPassword hashPassword = new HashPassword();
			hashPassword.setHashPassword(admin.getPassword());
			hashPassword.setIterations(admin.getIterations());
			hashPassword.setSalt(admin.getSalt());
			if (!HashPasswordUtil.validatePassword(password, hashPassword)) {
				throw new AuthFailedException();
			}
			adminDAO.updateLastLoginIP(admin.getId(), loginIP);
			if (admin.getLastLoginTime() != null) {
				adminDAO.updateLastLoginTime(admin.getId());
			}
		}
		return admin;
	}

	@Override
	public void resetAdminPwd(long id, String password) {
		setAdminPwd(id, password);
	}

	private void addAnonUserLocked(String userName) {
		SystemConfig systemconfigTime = systemConfigService.getSystemConfig("locktime");
		int expiredTime = Integer.parseInt(systemconfigTime.getValue()) * 1000;
		ManagerLocked managerLocked = (ManagerLocked) cacheClient.getCache(userName);
		if (null == managerLocked) {
			ManagerLocked newManagerLocked = new ManagerLocked();
			newManagerLocked.setCreatedAt(new Date());
			newManagerLocked.setLoginFailTimes(1);
			newManagerLocked.setLoginName(userName);
			cacheClient.setCache(userName, newManagerLocked, expiredTime);
		} else {
			managerLocked.setLoginFailTimes(managerLocked.getLoginFailTimes() + 1);
			cacheClient.replaceCache(userName, managerLocked, expiredTime);
		}
	}

	private void addExistUserLocked(String userName, UserLog userLog) {
		UserLocked ntUserLocked = ntlmManager.doReadUserLocked(userName);
		if (ntUserLocked != null) {
			ntUserLocked.setLoginTimes(ntUserLocked.getLoginTimes() + 1);
			isLockUser(userName, ntUserLocked, userLog);// 璁板綍鏃ュ織
			ntlmManager.doCreateUserLocked(userName, ntUserLocked);

		} else {
			UserLocked userLocked = new UserLocked();
			userLocked.setLoginDate(new Date());
			userLocked.setLoginTimes(1);
			userLocked.setUserName(userName);
			ntlmManager.doCreateUserLocked(userName, userLocked);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void setAdminPwd(long id, String password) {
		if (!PasswordValidateUtil.isValidPassword(password)) {
			throw new ValidationException();
		}
		try {
			adminDAO.updatePassword(id, HashPasswordUtil.generateHashPassword(password));
		} catch (Exception e) {
			logger.error("update faild", e);
			throw new ShaEncryptException(e);
		}
		adminDAO.updateValidKeyAndDynamicPwd(id, null, null);
	}

}
