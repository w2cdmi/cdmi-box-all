package com.huawei.sharedrive.uam.telecom.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.authserver.web.AuthServerController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.telecom.model.RestEnpUserModel;
import com.huawei.sharedrive.uam.telecom.service.BnetService;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Controller
@RequestMapping(value = "/api/telecom/management")
public class UserManagementController {

	@Autowired
	private BnetService bnetService;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private AdminLogManager adminLogManager;

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private AccountAuthserverManager accountAuthserverManager;

	private final static Logger LOGGER = LoggerFactory.getLogger(AuthServerController.class);

	@RequestMapping(value = "bindUserToApp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createEnpUserAndOpening(@RequestBody RestEnpUserModel restEnpUser,
			HttpServletRequest req) {

		String userName = restEnpUser.getName();
		String aliasName = restEnpUser.getAliasName();
		String appId = restEnpUser.getProductId();
		String enterpriseDomain = restEnpUser.getEnterpriseDomainName();
		String phoneNo = restEnpUser.getPhoneNo();

		String ok = bnetService.bindUserToApp(userName, aliasName, null, phoneNo, appId, enterpriseDomain);
		return new ResponseEntity<String>(ok, HttpStatus.OK);
	}

	/**
	 * bind enterprise authentication for Telecom
	 * 
	 * @param accountIds
	 * @param type
	 * @return ResponseEntity
	 */
	@RequestMapping(value = "bindAppToEnterprise", method = RequestMethod.POST)
	public ResponseEntity<?> bindAppToEnterprise(@RequestBody RestEnpUserModel restEnpUser, HttpServletRequest req) {
		Long enterpriseId = restEnpUser.getEnterpriseId();
		Long authServerId = restEnpUser.getAuthServerId();
		String accountIds = restEnpUser.getAccountIds();
		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		AuthServer authServer = authServerManager.getAuthServer(authServerId);
		String loginName = StringUtils.isNotBlank(enterprise.getContactEmail()) ? enterprise.getContactEmail()
				: enterprise.getContactPhone();

		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(enterpriseId);
		owner.setIp("");
		owner.setLoginName(loginName);

		String[] accountAuthServerStr = accountIds.split(";");
		LOGGER.info("[authServer] accountAuthServers :" + accountIds);
		String accountAuth;
		String[] accountAuthArg;

		String typeDetail;
		for (int i = 0; i < accountAuthServerStr.length; i++) {
			accountAuth = accountAuthServerStr[i];
			accountAuthArg = accountAuth.split(",");
			if (accountAuthArg.length != 2) {
				adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP_ERROR,
						new String[] { loginName, "AuthserverName: " + authServer.getName() });
				return new ResponseEntity<String>(ErrorCode.INVALID_PARAMTER.name(), HttpStatus.BAD_REQUEST);
			}
			typeDetail = AdminLogType.KEY_BINDAPP_OPENACCOUNT_OPEN.getDetails();

			tryBindApp(authServerId, owner, accountAuthArg, typeDetail);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private void tryBindApp(Long authServerId, LogOwner owner, String[] accountAuthArg, String typeDetail) {
		AuthServer authServer = authServerManager.getAuthServer(authServerId);
		EnterpriseAccount enterpriseAccount;
		String[] description;
		Long accountId = null;
		byte type;
		try {
			accountId = Long.parseLong(accountAuthArg[0]);
			type = Byte.parseByte(accountAuthArg[1]);

			if (type == 1) {
				typeDetail = AdminLogType.KEY_AUTHORIZE_ROUNCECREATEACCOUNT.getDetails();
			}
			enterpriseAccount = enterpriseAccountManager.getByAccountId(accountId);
			String appId = null;
			if (null != enterpriseAccount) {
				appId = enterpriseAccount.getAuthAppId();
				owner.setAppId(appId);
			}
			description = new String[] { owner.getLoginName(), "AuthserverName: " + authServer.getName(), typeDetail, appId };
		} catch (NumberFormatException e) {
			description = new String[] { owner.getLoginName(), "AuthserverName: " + authServer.getName(), typeDetail };
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP_ERROR, description);
			throw new InvalidParamterException(e.getMessage());
		}
		accountAuthserverManager.bindApp(authServerId, accountId, type);
		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP, description);
	}

	@RequestMapping(value = "updateBindInfo", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> updateEnpUserInfo(HttpServletRequest req) {

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "unbindToApp", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteEnpUser(HttpServletRequest req) {

		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
