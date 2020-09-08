package com.huawei.sharedrive.uam.accountuser.manager.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.ListUserAccountManager;
import com.huawei.sharedrive.uam.accountuser.manager.UpdateUserAccountListManager;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountCheckManager;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.authserver.service.AccountAuthserverService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.exception.BusinessErrorCode;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Component
public class UpdateUserAccountListManagerImpl implements UpdateUserAccountListManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserAccountListManager.class);

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private EnterpriseAccountService enterpriseAccountService;

	@Autowired
	private UserAccountCheckManager userAccountCheckManager;

	@Autowired
	private ListUserAccountManager listUserAccountManager;

	@Autowired
	private AccountAuthserverService accountAuthserverService;

	@Autowired
	private UserAccountService userAccountService;


	@Override
	public void updateUserList(UpdateUserAccountList updateUserAccountList, String sessionId, String appId,
			Long authServerId, Long enterpriseId) {
		AuthServer authServer = authServerManager.getAuthServer(authServerId);
		if (null == authServer) {
			LOGGER.error("[userAccount] find AuthServer failed" + " enterpriseId:" + enterpriseId + " authServerId:"
					+ authServerId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find AuthServer failed");
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:" + enterpriseId + " appId:"
					+ appId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
			LOGGER.error("enterpriseAccount status is disable, enterpriseId: " + enterpriseId + "authAppId: " + appId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseAccount status disable");
		}

//		AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
//		accountBasicConfig.setAccountId(enterpriseAccount.getAccountId());
//		accountBasicConfig = accountBasicConfigManager.queryAccountBasicConfig(accountBasicConfig,appId);

		checkParam(updateUserAccountList, enterpriseAccount);
		userAccountCheckManager.checkUpdateUserListPara(updateUserAccountList, appId);
		String userIds = updateUserAccountList.getUserIds();
		if ("all".equalsIgnoreCase(userIds)) {
			String dn = updateUserAccountList.getSelLdapDn();
			// TODO To be optimized
			Integer status = null;
			try {
				if (StringUtils.isNotBlank(updateUserAccountList.getSelStatus())) {

					status = Integer.parseInt(updateUserAccountList.getSelStatus());
				}
			} catch (NumberFormatException e) {
				LOGGER.error("update useraccount failed ", e);
			}
			String filter = updateUserAccountList.getSelFilter();
			UserLdap tmpUserLdap = new UserLdap();
			UserAccount tmpUserAccount = new UserAccount();
			tmpUserLdap.setAuthServerId(authServerId);
			tmpUserLdap.setSessionId(sessionId);
			tmpUserLdap.setDn(dn);
			tmpUserAccount.setStatus(status);
			tmpUserAccount.setEnterpriseId(enterpriseId);
			tmpUserAccount.setAccountId(enterpriseAccount.getAccountId());
			List<UserAccount> userAccoutList = listUserAccountManager.getUserAccountList(tmpUserLdap, tmpUserAccount,
					appId, filter);
			for (UserAccount userAccount : userAccoutList) {
				try {
					fillUserByUpdateUserList(userAccount, updateUserAccountList);
					userAccountManager.update(userAccount, appId);
				} catch (Exception e) {
					LOGGER.error("update useraccount failed ", e);
				}
			}
		} else {
			String ids = updateUserAccountList.getUserIds();
			if (StringUtils.isBlank(ids)) {
				LOGGER.error("ids is null");
				throw new InvalidParamterException();
			}
			String[] idArray = ids.split(",");
			for (String id : idArray) {
				try {
					int idNum = Integer.parseInt(id);
					UserAccount userAccount = userAccountManager.getById(idNum, enterpriseId, appId);
					fillUserByUpdateUserList(userAccount, updateUserAccountList);
					userAccountManager.update(userAccount, appId);
				} catch (Exception e) {
					LOGGER.error("update useraccount failed ", e);
				}
			}
		}
	}

	@Override
	public void updateUserStatus(UserLdap userLdap, UserAccount userAccount, String appId, String filter,
			Integer selStatus) {
		AuthServer authServer = authServerManager.getAuthServer(userLdap.getAuthServerId());
		if (null == authServer) {
			LOGGER.error("[userAccount] find AuthServer failed" + " enterpriseId:" + userAccount.getEnterpriseId()
					+ " authServerId:" + userLdap.getAuthServerId());
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find AuthServer failed");
		}
		EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(userAccount.getEnterpriseId(),
				appId);
		if (null == enterpriseAccount) {
			LOGGER.error("[userAccount] find enterpriseAccount failed" + " enterpriseId:"
					+ userAccount.getEnterpriseId() + " appId:" + appId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "find enterpriseAccount failed");
		}
		if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
			LOGGER.error("enterpriseAccount status is disable, enterpriseId: " + userAccount.getEnterpriseId()
					+ "authAppId: " + appId);
			throw new BusinessException(BusinessErrorCode.INTERNAL_SERVER_ERROR, "enterpriseAccount status disable");
		}
		userAccountCheckManager.checkUserAccountStatus(userAccount.getStatus());
		if ("all".equalsIgnoreCase(userAccount.getAlias())) {
			// TODO To be optimized
			UserLdap tmpUserLdap = new UserLdap();
			UserAccount tmpUserAccount = new UserAccount();
			tmpUserLdap.setAuthServerId(userLdap.getAuthServerId());
			tmpUserLdap.setDn(userLdap.getDn());
			tmpUserLdap.setSessionId(userLdap.getSessionId());
			tmpUserAccount.setStatus(selStatus);
			tmpUserAccount.setEnterpriseId(userAccount.getEnterpriseId());
			tmpUserAccount.setAccountId(enterpriseAccount.getAccountId());
			List<UserAccount> userAccoutList = listUserAccountManager.getUserAccountList(tmpUserLdap, tmpUserAccount,
					appId, filter);
			for (UserAccount iter : userAccoutList) {
				try {
					iter.setStatus(userAccount.getStatus());
					userAccountManager.update(iter, appId);
				} catch (Exception e) {
					LOGGER.error("update useraccount failed ", e);
				}
			}
		} else {
			if (StringUtils.isBlank(userAccount.getAlias())) {
				LOGGER.error("ids is null");
				throw new InvalidParamterException();
			}
			String[] idArray = userAccount.getAlias().split(",");
			for (String id : idArray) {
				try {
					int idNum = Integer.parseInt(id);
					UserAccount tmpUserAccount = userAccountManager.getById(idNum, userAccount.getEnterpriseId(),
							appId);
					tmpUserAccount.setStatus(userAccount.getStatus());
					userAccountManager.update(tmpUserAccount, appId);
				} catch (Exception e) {
					LOGGER.error("update useraccount failed ", e);
				}
			}
		}
	}

	@Override
	public void updateRole(UserLdap userLdap, UserAccount userAccount, String appId, String ids, String filter) {

		long accountId = accountAuthserverService.getAccountId(userLdap.getAuthServerId(),
				userAccount.getEnterpriseId(), appId);
		if (AccountAuthserver.UNDEFINED_OPEN_ACCOUNT == accountId) {
			return;
		}

		if ("all".equalsIgnoreCase(ids)) {
			// TODO To be optimized
			UserLdap tmpUserLdap = new UserLdap();
			UserAccount tmpUserAccount = new UserAccount();
			tmpUserLdap.setAuthServerId(userLdap.getAuthServerId());
			tmpUserLdap.setDn(userLdap.getDn());
			tmpUserLdap.setSessionId(userLdap.getSessionId());
			tmpUserAccount.setStatus(userAccount.getStatus());
			tmpUserAccount.setEnterpriseId(userAccount.getEnterpriseId());
			tmpUserAccount.setAccountId(accountId);
			List<UserAccount> userAccoutList = listUserAccountManager.getUserAccountList(tmpUserLdap, tmpUserAccount,
					appId, filter);
			for (UserAccount iter : userAccoutList) {
				try {
					iter.setRoleId(userAccount.getRoleId());
					userAccountService.updateRole(iter, String.valueOf(iter.getId()));
				} catch (Exception e) {
					LOGGER.error("update useraccount failed ", e);
				}
			}
		} else {
			UserAccount tmpUserAccount = new UserAccount();
			tmpUserAccount.setAccountId(accountId);
			tmpUserAccount.setRoleId(userAccount.getRoleId());
			userAccountService.updateRole(tmpUserAccount, ids);
		}
	}

	private void fillUserByUpdateUserList(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		fillMaxVersions(userAccount, updateUserList);
		fillSpaceQuota(userAccount, updateUserList);
		fillRegionId(userAccount, updateUserList);
		fillTeamSpaceFlag(userAccount, updateUserList);
		fillTeamSpaceMaxNum(userAccount, updateUserList);
		fillDownloadBandWidth(userAccount, updateUserList);
		fillUploadBandWidth(userAccount, updateUserList);
	}

	private void fillUploadBandWidth(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsUploadBandWidthInput()) {
			return;
		}
		if (!updateUserList.getIsUploadBandWidthInput()) {
			return;
		}
		if (null == updateUserList.getUploadBandWidth()) {
			return;
		}
		userAccount.setUploadBandWidth(updateUserList.getUploadBandWidth());
	}

	private void fillDownloadBandWidth(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsDownloadBandWidthInput()) {
			return;
		}
		if (!updateUserList.getIsDownloadBandWidthInput()) {
			return;
		}
		if (null == updateUserList.getDownloadBandWidth()) {
			return;
		}
		userAccount.setDownloadBandWidth(updateUserList.getDownloadBandWidth());
	}

	private void fillTeamSpaceMaxNum(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsTeamSpaceMaxNum()) {
			return;
		}
		if (!updateUserList.getIsTeamSpaceMaxNum()) {
			return;
		}
		if (updateUserList.getTeamSpaceMaxNum() == null) {
			return;
		}
		userAccount.setTeamSpaceMaxNum(updateUserList.getTeamSpaceMaxNum());
	}

	private void fillTeamSpaceFlag(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsTeamSpaceFlag()) {
			return;
		}
		if (null != updateUserList.getTeamSpaceFlag()) {
			userAccount.setTeamSpaceFlag((int) updateUserList.getTeamSpaceFlag());
		} else {
			userAccount.setTeamSpaceFlag((int) User.TEAMSPACE_FLAG_SET);
		}
	}

	private void fillRegionId(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsRegionId()) {
			return;
		}
		if (!updateUserList.getIsRegionId()) {
			return;
		}
		if (null == updateUserList.getRegionId()) {
			return;
		}
		userAccount.setRegionId(updateUserList.getRegionId());
	}

	private void fillSpaceQuota(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsSpaceQuota()) {
			return;
		}
		if (!updateUserList.getIsSpaceQuota()) {
			return;
		}
		if (null == updateUserList.getSpaceQuota()) {
			return;
		}
		userAccount.setSpaceQuota(updateUserList.getSpaceQuota());
	}

	private void fillMaxVersions(UserAccount userAccount, UpdateUserAccountList updateUserList) {
		if (null == updateUserList.getIsMaxVersions()) {
			return;
		}
		if (!updateUserList.getIsMaxVersions().booleanValue()) {
			return;
		}
		if (null == updateUserList.getMaxVersions()) {
			return;
		}
		userAccount.setMaxVersions(updateUserList.getMaxVersions());
	}

	private void checkParam(UpdateUserAccountList updateUserAccountList,
			EnterpriseAccount enterpriseAccount) {
//		if (!accountBasicConfig.getUserSpaceQuota().equals("-1")) {
//			if (updateUserAccountList.getIsSpaceQuota() != null && updateUserAccountList.getIsSpaceQuota()
//					&& updateUserAccountList.getSpaceQuota() > Long.parseLong(accountBasicConfig.getUserSpaceQuota())) {
//				LOGGER.error("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
//				throw new InvalidParamterException("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
//			}
//		} else {
			if (updateUserAccountList.getIsSpaceQuota() != null && updateUserAccountList.getIsSpaceQuota()
					&& updateUserAccountList.getSpaceQuota() * 1024 > enterpriseAccount.getMaxSpace()) {
				LOGGER.error("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
				throw new InvalidParamterException("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
			}
//		}
//		if (!accountBasicConfig.getUserVersions().equals("-1")) {
//			if (updateUserAccountList.getIsMaxVersions() != null && updateUserAccountList.getIsMaxVersions()
//					&& updateUserAccountList.getMaxVersions() > Integer
//							.parseInt(accountBasicConfig.getUserVersions())) {
//				LOGGER.error("invalid version:" + updateUserAccountList.getMaxVersions());
//				throw new InvalidParamterException("invalid version:" + updateUserAccountList.getMaxVersions());
//			}
//		}
//		if (!accountBasicConfig.getMaxTeamSpaces().equals("-1")) {
//			if (updateUserAccountList.getIsTeamSpaceMaxNum() != null && updateUserAccountList.getIsTeamSpaceMaxNum()
//					&& updateUserAccountList.getTeamSpaceMaxNum() > Integer
//							.parseInt(accountBasicConfig.getMaxTeamSpaces())) {
//				LOGGER.error("invalid TeamSpaceMaxNum:" + updateUserAccountList.getTeamSpaceMaxNum());
//				throw new InvalidParamterException(
//						"invalid TeamSpaceMaxNum:" + updateUserAccountList.getTeamSpaceMaxNum());
//			}
//			// appBasicConfig.setMaxTeamSpaces(Integer.parseInt(accountBasicConfig.getMaxTeamSpaces()));
//		} else {
//			if (updateUserAccountList.getIsTeamSpaceMaxNum() != null && updateUserAccountList.getIsTeamSpaceMaxNum()
//					&& updateUserAccountList.getTeamSpaceMaxNum() > enterpriseAccount.getMaxTeamspace()) {
//				LOGGER.error("invalid TeamSpaceMaxNum:" + updateUserAccountList.getTeamSpaceMaxNum());
//				throw new InvalidParamterException(
//						"invalid TeamSpaceMaxNum:" + updateUserAccountList.getTeamSpaceMaxNum());
//			}
//		}
		if (null != updateUserAccountList.getSpaceQuota()
				&& updateUserAccountList.getSpaceQuota() > User.SPACE_QUOTA_MAX_G) {
			LOGGER.error("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
			throw new InvalidParamterException("invalid spaceQuota:" + updateUserAccountList.getSpaceQuota());
		}
		if (updateUserAccountList.getSpaceQuota() != null
				&& updateUserAccountList.getSpaceQuota() != User.SPACE_QUOTA_UNLIMITED) {
			updateUserAccountList.setSpaceQuota(
					updateUserAccountList.getSpaceQuota() * User.SPACE_UNIT * User.SPACE_UNIT * User.SPACE_UNIT);
		}
	}
}
