package com.huawei.sharedrive.uam.user.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.user.dao.AdminAppPermissionDAO;
import com.huawei.sharedrive.uam.user.dao.AdminDAO;
import com.huawei.sharedrive.uam.user.dao.ManagerLockedDao;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AccountRetryCountService;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.ManagerLocked;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

@Component
public class AdminServiceImpl implements AdminService {
	private static final int INITIAL_FAIL_TIME = 1;

	@Autowired
	private AdminDAO adminDAO;

	@Autowired
	private AdminAppPermissionDAO adminAppPermissionDAO;

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private ManagerLockedDao managerLockedDao;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	private EnterpriseService enterpriseService;

	@Autowired
	private UserLockService userLockService;

	@Autowired
	private AccountRetryCountService accountRetryCountService;

	private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

	@Override
	public List<String> getNameByIds(Long[] ids) {
		List<String> list = new ArrayList<String>(10);
		List<Admin> adminList = adminDAO.getAdminByIds(ids);
		if (adminList != null) {
			for (Admin admin : adminList) {
				list.add(admin.getName());
			}
		}
		return list;
	}

	@Override
	public List<Admin> getAdminByIds(Long[] ids) {
		return adminDAO.getAdminByIds(ids);
	}

	@Override
	public List<Admin> getAdminExcludeIds(Long[] ids) {
		return adminDAO.getAdminExcludeIds(ids);
	}

	@Override
	public Admin getAdminByLoginName(String loginName) {
		return adminDAO.getByLoginNameWithoutCache(loginName);
	}

	@Override
	public Admin getByLoginNameAndEnterpriseId(String loginName, long enterpriseId) {
		return adminDAO.getByLoginNameAndEnterpriseIdWithoutCache(loginName, enterpriseId);
	}

	@Override
	public Page<Admin> getPagedAdmins(Admin admin, PageRequest pageRequest) {
		return null;
	}

	@Override
	public Page<Admin> getFilterd(Admin filter, PageRequest pageRequest) {
		int total = adminDAO.getFilterdCount(filter);
		List<Admin> content = adminDAO.getFilterd(filter, pageRequest.getOrder(), pageRequest.getLimit());
		Page<Admin> page = new PageImpl<Admin>(content, pageRequest, total);
		return page;
	}

	@Override
	public long getAll(Admin filter) {
		long total = adminDAO.getFilterdCount(filter);
		return total;
	}

	@Override
	public Admin get(Long id) {
		return adminDAO.get(id);
	}

	@Override
	public void create(Admin admin) {
		String password = admin.getPassword();
		try {
			if(StringUtils.isNotBlank(password)) {
				HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);
				admin.setPassword(hashPassword.getHashPassword());
				admin.setIterations(hashPassword.getIterations());
				admin.setSalt(hashPassword.getSalt());
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("hash NoSuchAlgorithmException", e);
			throw new ShaEncryptException("hash NoSuchAlgorithmException", e);
		} catch (InvalidKeySpecException e) {
			LOGGER.error("hash InvalidKeySpecException", e);
			throw new ShaEncryptException("hash InvalidKeySpecException", e);
		}
		long id = adminDAO.getNextAvailableAdminId();
		admin.setId(id);
		Date now = new Date();
		admin.setCreatedAt(now);
		admin.setModifiedAt(now);
		if (0 == admin.getType()) {
			admin.setType(Constants.ROLE_COMMON_ADMIN);
		}
		adminDAO.create(admin);

		//恢复原来的明文密码
		admin.setPassword(password);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(long id) {
		adminDAO.delete(id);
		adminAppPermissionDAO.deleteByAdminId(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(Byte status, Long id) {

		adminDAO.updateStatus(status, id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void changeAdminPwdByInitLogin(Admin inputAdmin, HttpServletRequest req, String loginIP) {
		changeAdminPwd(inputAdmin, req, false);
/*
		updateEmail(inputAdmin.getId(), inputAdmin.getEmail());
		Enterprise enterprise = enterpriseService.getById(inputAdmin.getEnterpriseId());
		enterprise.setContactEmail(inputAdmin.getEmail());
		enterpriseService.updateEnterpriseInfo(enterprise);
*/
		adminDAO.updateLastLoginInfo(inputAdmin.getId(), loginIP);
	}

	@Override
	public void resetAdminPwd(long id, String password) {
		setAdminPwd(id, password);
	}

	@Override
	public void initSetAdminPwd(long id, String password) {
		setAdminPwd(id, password);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	protected void setAdminPwd(long id, String password) {
		if (!PasswordValidateUtil.isValidPassword(password)) {
			throw new ValidationException();
		}

		Admin admin = adminDAO.get(id);

		if (admin.getLoginName().equalsIgnoreCase(password)) {
			LOGGER.error("loginName and password cannot be the same");
			throw new InvalidParamterException("namePwdSameError", "loginName and password cannot be the same");
		}
		try {
			adminDAO.updatePassword(id, HashPasswordUtil.generateHashPassword(password));
		} catch (Exception e) {
			LOGGER.error("update faild", e);
			throw new ShaEncryptException("update faild", e);
		}

		adminDAO.updateValidKeyAndDynamicPwd(id, null, null);
	}

	@Override
	public void changeLoginName(Admin inputAdmin) {
		Admin admin = adminDAO.get(inputAdmin.getId());
		HashPassword hashPassword = new HashPassword();
		hashPassword.setHashPassword(admin.getPassword());
		hashPassword.setIterations(admin.getIterations());
		hashPassword.setSalt(admin.getSalt());
		if (HashPasswordUtil.validatePassword(inputAdmin.getLoginName(), hashPassword)) {
			LOGGER.error("loginName and password cannot be the same");
			throw new PasswordSameException();
		}

		adminDAO.updateLoginName(inputAdmin.getId(), inputAdmin.getLoginName());
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	@Override
	public void changeAdminPwd(Admin inputAdmin, HttpServletRequest req) {
		changeAdminPwd(inputAdmin, req, true);
	}

	private void changeAdminPwd(Admin inputAdmin, HttpServletRequest req, boolean checkOldPassword) {
		accountRetryCountService.checkUserLocked(inputAdmin.getLoginName(), req);
		if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()) /*|| !PasswordValidateUtil.isValidPassword(inputAdmin.getOldPasswd())*/) {
			accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
			throw new PasswordInvalidException();
		}

		long adminId = inputAdmin.getId();
		Admin admin = adminDAO.get(adminId);
		HashPassword hashPassword = new HashPassword();
		hashPassword.setHashPassword(admin.getPassword());
		hashPassword.setIterations(admin.getIterations());
		hashPassword.setSalt(admin.getSalt());

		if (admin.getLoginName().equalsIgnoreCase(inputAdmin.getPassword())) {
			LOGGER.error("loginName and password cannot be the same");
			accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
			throw new InvalidParamterException("loginName and password cannot be the same");
		}

		if(checkOldPassword) {
			if (!HashPasswordUtil.validatePassword(inputAdmin.getOldPasswd(), hashPassword)) {
				accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
				throw new OldPasswordErrorException();
			}
		}

		if (HashPasswordUtil.validatePassword(inputAdmin.getPassword(), hashPassword)) {
			accountRetryCountService.addUserLocked(inputAdmin.getLoginName());
			throw new PasswordSameException();
		}
		try {
			HashPassword newHashPassword = HashPasswordUtil.generateHashPassword(inputAdmin.getPassword());
			adminDAO.updatePassword(adminId, newHashPassword);
		} catch (Exception e) {
			LOGGER.error("update faild", e);
			throw new ShaEncryptException();
		}
	}

	@Override
	public Admin getAdminByObjectSID(String objectSID) {
		return adminDAO.getAdminByObjectSID(objectSID);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAdmin(Admin admin) {
		adminDAO.update(admin);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Admin login(String enterpriseName, String userName, String password, SystemLog systemLog, AuthenticationToken authcToken) throws AuthFailedException, UserLockedWebException {
		Admin admin = null;
		long enterpriseId = 0;
		try {
			if (StringUtils.isEmpty(password)) {
                throw new AuthFailedException("password is null.");
            }

			Enterprise enterprise = enterpriseService.getByName(enterpriseName);
			if (enterprise == null) {
                LOGGER.error("enterprise doesn't exist: name={}" , enterpriseName);
                throw new AuthFailedException("enterprise doesn't exist");
            }

			enterpriseId = enterprise.getId();

			//检查用户是否已经被锁定
			checkUserLocked(enterpriseId, userName);

			admin = adminDAO.getByLoginNameAndEnterpriseIdWithoutCache(userName, enterpriseId);
			if (admin == null) {
                LOGGER.error("enterprise doesn't exist: name={}" , enterpriseName);
                throw new AuthFailedException();
            }

			if (Constants.ROLE_ENTERPRISE_ADMIN != admin.getType()) {
                LOGGER.error("enterprise admin[{}] is not enterprise manager: type={}" , userName, admin.getType());
                throw new AuthFailedException("wrong admin type.");
            }

			HashSet<AdminRole> roles = admin.getRoles();
			if (roles.size() > 1 || !roles.contains(AdminRole.ENTERPRISE_MANAGER)) {
                LOGGER.error("enterprise admin[{}] hasn't enterprise manager role." , userName);
                throw new AuthFailedException("hasn't enterprise manager role.");
            }

			if (admin.getStatus() == Admin.STATUS_DISABLE) {
                LOGGER.error("admin disabled " + admin.getName());
                throw new DisabledUserException("user is disabled.");
            }

			if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL) {
                HashPassword hashPassword = new HashPassword();
                hashPassword.setHashPassword(admin.getPassword());
                hashPassword.setIterations(admin.getIterations());
                hashPassword.setSalt(admin.getSalt());
                adminDAO.updateLastLoginIP(admin.getId(), authcToken);
                if (!HashPasswordUtil.validatePassword(password, hashPassword)) {
                    throw new AuthFailedException("password is wrong");
                }
                if (admin.getLastLoginTime() != null) {
                    adminDAO.updateLastLoginTime(admin.getId());
                }
            } else {
                LOGGER.error("admin[{}] is not local domain type: {}" , userName, admin.getType());
                throw new AuthFailedException("admin is not local domain type");
            }
		} catch (AuthFailedException e) {
			if(enterpriseId > 0) {
				saveUserLocked(enterpriseId, userName);
			}
			throw e;
		} catch (DisabledUserException e) {
			throw e;
		}

		return admin;
	}

	@Override
	public void synLdapAccountInfo(Admin admin, Admin ldapUser) {
		if (isNotEqual(admin.getLoginName(), ldapUser.getLoginName()) || isNotEqual(admin.getName(), ldapUser.getName())
				|| isNotEqual(admin.getEmail(), ldapUser.getEmail())) {
			admin.setLoginName(ldapUser.getLoginName());
			admin.setName(ldapUser.getName());
			admin.setEmail(ldapUser.getEmail());
			adminDAO.update(admin);
		}
	}

	private boolean isNotEqual(String src, String target) {
		if (src == null && target == null) {
			return false;
		}
		if (src != null && target != null && src.equals(target)) {
			return false;
		}
		return true;
	}

	@Override
	public void updateValidKeyAndDynamicPwd(long id, String validateKey, String dynamicPwd) {
		adminDAO.updateValidKeyAndDynamicPwd(id, validateKey, dynamicPwd);
	}

	@Override
	public void updateEmail(long id, String email) {
		adminDAO.updateEmail(id, email);
	}

	private void checkUserLocked(long enterpriseId, String loginName) {
		//为兼容，使用enterpriseId + loginName保存数据
		String key = loginName + "@" + enterpriseId;
		ManagerLocked managerLocked = managerLockedDao.get(key);
		if (managerLocked == null) {
			return;
		}

		//判断锁定状态：锁定时间不为空
		if (managerLocked.getLockedAt() != null) {
			if (new Date().getTime() - managerLocked.getLockedAt().getTime() > userLockService.getConfigByLockWait()) {
				LOGGER.info("unlock user: enterprise = {}, name={}" , enterpriseId, loginName);

				//已经过期, 删除锁定记录
				managerLockedDao.delete(key);
				LogOwner owner = new LogOwner();
				owner.setLoginName(loginName);
				adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_UNLOCK, new String[] { loginName });
			} else {
				LOGGER.error("user is locked: enterprise = {}, name={}" , enterpriseId, loginName);
				throw new UserLockedWebException("user is locked.");
			}
		}
	}

	private void saveUserLocked(long enterpriseId, String loginName) {
		//为兼容，使用enterpriseId + loginName保存数据
		String key = loginName + "@" + enterpriseId;
		ManagerLocked managerLocked = managerLockedDao.get(key);
		if (managerLocked == null) {
			managerLocked = new ManagerLocked();
			managerLocked.setLoginName(key);
			managerLocked.setCreatedAt(new Date());
			managerLocked.setLoginFailTimes(INITIAL_FAIL_TIME);
			managerLockedDao.insert(managerLocked);
		} else {
			//SQL中执行+1操作
			managerLockedDao.addFailTime(managerLocked);

			//手工+1，然后判断是否达到锁定条件
			managerLocked.setLoginFailTimes(managerLocked.getLoginFailTimes() + INITIAL_FAIL_TIME);
			if (managerLocked.getLoginFailTimes() >= userLockService.getConfigByLockTimes()) {
				Date time = new Date();
				LOGGER.error("user locked: enterprise={}, loginName={}, time={}", enterpriseId, loginName, time);
				managerLocked.setLockedAt(time);
				managerLockedDao.lock(managerLocked);

				LogOwner owner = new LogOwner();
				owner.setLoginName(key);
				adminLogManager.saveAdminLog(owner, AdminLogType.KEY_ADMIN_LOGIN_LOCK, new String[] { owner.getLoginName() });
			}
		}
	}

	@Override
	public Admin getByEmail(String email) {
		return adminDAO.getByEmailWithoutCache(email);
	}

}
