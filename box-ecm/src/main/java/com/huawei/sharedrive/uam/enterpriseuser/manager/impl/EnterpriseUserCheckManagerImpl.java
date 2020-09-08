package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserCheckManager;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.ShaEncryptException;
import com.huawei.sharedrive.uam.util.PasswordValidateUtil;

import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

@Component
public class EnterpriseUserCheckManagerImpl implements EnterpriseUserCheckManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserCheckManager.class);

	@Autowired
	private EnterpriseAccountService enterpriseAccountService;

	@Override
	public boolean isUpdateByLdap(EnterpriseUser user, EnterpriseUser ldapUser) {
		boolean isUpdate = false;
		if (isAliasChange(user, ldapUser)) {
			isUpdate = true;
		}

		if (isEmailChange(user, ldapUser)) {
			isUpdate = true;
		}

		if (isNameChange(user, ldapUser)) {
			isUpdate = true;
		}

		if (isDescriptionChange(user, ldapUser)) {
			isUpdate = true;
		}

		return isUpdate;
	}

	private boolean isDescriptionChange(EnterpriseUser user, EnterpriseUser ldapUser) {
		if (StringUtils.isNotBlank(user.getDescription())) {
			if (!StringUtils.equals(user.getDescription(), ldapUser.getDescription())) {
				return true;
			}
		} else {
			if (StringUtils.isNotBlank(ldapUser.getDescription())) {
				return true;
			}
		}
		return false;
	}

	private boolean isNameChange(EnterpriseUser user, EnterpriseUser ldapUser) {
		if (StringUtils.isNotBlank(user.getName())) {
			if (StringUtils.isNotBlank(ldapUser.getName()) && !user.getName().equals(ldapUser.getName())) {
				return true;
			}
		} else {
			if (StringUtils.isNotBlank(ldapUser.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmailChange(EnterpriseUser user, EnterpriseUser ldapUser) {
		if (StringUtils.isNotBlank(user.getEmail())) {
			if (StringUtils.isNotBlank(ldapUser.getEmail()) && !user.getEmail().equals(ldapUser.getEmail())) {
				return true;
			}
		} else {
			if (StringUtils.isNotBlank(ldapUser.getEmail())) {
				return true;
			}
		}
		return false;
	}

	private boolean isAliasChange(EnterpriseUser user, EnterpriseUser ldapUser) {
		if (StringUtils.isNotBlank(user.getAlias())) {
			if (StringUtils.isNotBlank(ldapUser.getAlias()) && !user.getAlias().equals(ldapUser.getAlias())) {
				return true;
			}
		} else {
			if (StringUtils.isNotBlank(ldapUser.getAlias())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("PMD.PreserveStackTrace")
	@Override
	public void checkAndSetPassword(EnterpriseUser enterpriseUser, String newPassword, String oldPassword) {
		// 根据企业ID查找密码复杂度
		long enterpriseId = enterpriseUser.getEnterpriseId();
		int pwd_Level = 1;
		Integer pwdLevel = 1;
		if (StringUtils.isNotBlank(enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId))) {
			pwdLevel = Integer.parseInt(enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId).trim());
		}
		if (!StringUtils.isBlank(pwdLevel.toString())) {
			pwd_Level = pwdLevel;
		}
		if (!PasswordValidateUtil.isValidPassword(newPassword, pwd_Level)) {
			throw new InvalidParamterException("newPassword is not correct");
		}
		if (!PasswordValidateUtil.isValidPassword(oldPassword, 3)) {
			throw new InvalidParamterException("oldPassword is not correct");
		}

		HashPassword oldHashPassword = new HashPassword();
		oldHashPassword.setHashPassword(enterpriseUser.getPassword());
		oldHashPassword.setSalt(enterpriseUser.getSalt());
		oldHashPassword.setIterations(enterpriseUser.getIterations());
		if (!HashPasswordUtil.validatePassword(oldPassword, oldHashPassword)) {
			throw new InvalidParamterException("oldpassword is not correct");
		}
		try {
			HashPassword hashPassword = HashPasswordUtil.generateHashPassword(newPassword);
			enterpriseUser.setPassword(hashPassword.getHashPassword());
			enterpriseUser.setIterations(hashPassword.getIterations());
			enterpriseUser.setSalt(hashPassword.getSalt());
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("digest exception");
			throw new ShaEncryptException();
		} catch (InvalidKeySpecException e) {
			LOGGER.error("digest exception");
			throw new ShaEncryptException();
		}
	}

	@Override
	public boolean isUpdateAndSetEnterpriseUser(EnterpriseUser selUser, EnterpriseUser updateUser) {
		boolean isUpdate = false;
		if (isUpdateAndSetAlias(selUser, updateUser)) {
			isUpdate = true;
		}
		if (isUpdateAndSetEmail(selUser, updateUser)) {
			isUpdate = true;
		}
		if (isUpdateAndSetDescrip(selUser, updateUser)) {
			isUpdate = true;
		}
		if (isUpdateAndSetMobile(selUser, updateUser)) {
			isUpdate = true;
		}
		return isUpdate;
	}

	private boolean isUpdateAndSetDescrip(EnterpriseUser selUser, EnterpriseUser updateUser) {
		boolean isUpdate = false;
		if (StringUtils.isNotBlank(selUser.getDescription())) {
			if (StringUtils.isNotBlank(updateUser.getDescription())
					&& !StringUtils.equals(selUser.getDescription(), updateUser.getDescription())) {
				isUpdate = true;
				selUser.setDescription(updateUser.getDescription());
			}
		} else {
			if (StringUtils.isNotBlank(updateUser.getDescription())) {
				isUpdate = true;
				selUser.setDescription(updateUser.getDescription());
			}
		}
		return isUpdate;
	}

	private boolean isUpdateAndSetEmail(EnterpriseUser selUser, EnterpriseUser updateUser) {
		boolean isUpdate = false;
		if (StringUtils.isNotBlank(selUser.getEmail())) {
			if (StringUtils.isNotBlank(updateUser.getEmail()) && !selUser.getEmail().equals(updateUser.getEmail())) {
				isUpdate = true;
				selUser.setEmail(updateUser.getEmail());
			}
		} else {
			if (StringUtils.isNotBlank(updateUser.getEmail())) {
				isUpdate = true;
				selUser.setEmail(updateUser.getEmail());
			}
		}
		return isUpdate;
	}

	private boolean isUpdateAndSetMobile(EnterpriseUser selUser, EnterpriseUser updateUser) {
		boolean isUpdate = false;
		if (StringUtils.isNotBlank(selUser.getMobile())) {
			if (StringUtils.isNotBlank(updateUser.getMobile()) && !selUser.getMobile().equals(updateUser.getMobile())) {
				isUpdate = true;
				selUser.setMobile(updateUser.getMobile());
			}
		} else {
			if (StringUtils.isNotBlank(updateUser.getMobile())) {
				isUpdate = true;
				selUser.setMobile(updateUser.getMobile());
			}
		}
		return isUpdate;
	}

	private boolean isUpdateAndSetAlias(EnterpriseUser selUser, EnterpriseUser updateUser) {
		boolean isUpdate = false;
		if (StringUtils.isNotBlank(selUser.getAlias())) {
			if (StringUtils.isNotBlank(updateUser.getAlias()) && !selUser.getAlias().equals(updateUser.getAlias())) {
				isUpdate = true;
				selUser.setAlias(updateUser.getAlias());
			}
		} else {
			if (StringUtils.isNotBlank(updateUser.getAlias())) {
				isUpdate = true;
				selUser.setAlias(updateUser.getAlias());
			}
		}
		return isUpdate;
	}

	@Override
	public boolean isLoginNameChanged(String name, String ldapName) {

		boolean isUpdate = false;
		if (StringUtils.isNotBlank(name)) {
			if (!name.equals(ldapName) && StringUtils.isNotBlank(ldapName)) {
				isUpdate = true;
			}
		} else {
			if (StringUtils.isNotBlank(ldapName)) {
				isUpdate = true;
			}
		}
		return isUpdate;
	}
}
