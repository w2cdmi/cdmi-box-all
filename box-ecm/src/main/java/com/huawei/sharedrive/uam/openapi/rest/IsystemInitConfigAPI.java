package com.huawei.sharedrive.uam.openapi.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterprise;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

@Controller
@RequestMapping(value = "/api/v2/isystem")
public class IsystemInitConfigAPI {

	@Autowired
	private EnterpriseManager enterpriseManager;
	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;
	@Autowired
	private AuthServerService authServerService;

	@Autowired
	private AccountAuthserverManager accountAuthserverManager;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private UserAccountManager userAccountManager;

	/*
	 * Isystem init config enterprise bind app
	 */
	@RequestMapping(value = "/enterprise/app/bind", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> enterpriseAppBind(@RequestBody RestEnterprise restEnterprise,
			@RequestHeader("appId") String appId) {

		try {

			// Get enterprise ID
			Enterprise enterprise = enterpriseManager.getByDomainName(restEnterprise.getDomainName());
			long enterpriseID = Long.valueOf(enterprise.getId());

			// Get authserver
			AuthServer authServer = authServerService.getByEnterpriseIdType(enterpriseID, "LocalAuth");

			// Get enterpriseAccount
			EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterpriseID, appId);

			// Enterprise bind app
			accountAuthserverManager.bindApp(authServer.getId(), enterpriseAccount.getAccountId(), (byte) -1);

		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	/*
	 * Create enterprise user and user account
	 */
	@RequestMapping(value = "/enterprise/user/create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> userCreateInIsystem(@RequestBody RestEnterprise restEnterprise,
			@RequestHeader("appId") String appId) {
		// Enterprise format enterpriseUser
		EnterpriseUser enterpriseUser = new EnterpriseUser();
		enterpriseUser.setName(restEnterprise.getName());
		enterpriseUser.setAlias(restEnterprise.getDomainName());
		enterpriseUser.setEmail(restEnterprise.getContactEmail());

		try {
			// Get enterprise ID
			// long enterpriseID = Long.parseLong(enterpriseId);
			Enterprise enterprise = enterpriseManager.getById(restEnterprise.getId());
			enterpriseUser.setEnterpriseId(enterprise.getId());

			// Get auth server
			AuthServer authServer = authServerService.getByEnterpriseIdType(restEnterprise.getId(), "LocalAuth");
			enterpriseUser.setUserSource(authServer.getId());

			// Add enterprise user
			EnterpriseUser eUser = enterpriseUserManager.getEUserByLoginName(enterpriseUser.getName(),
					enterpriseUser.getAlias());
			long userId = 0;
			if (eUser == null) {
				enterpriseUserManager.createLocal(enterpriseUser, true);

				// Get user Id
				enterpriseUser = enterpriseUserManager.getEUserByLoginName(enterpriseUser.getName(),
						enterprise.getDomainName());
				userId = enterpriseUser.getId();

			} else {
				userId = eUser.getId();
			}

			// User account create
			if (userAccountManager.getById(userId, enterprise.getId(), appId) == null) {
				userAccountManager.create(userId, restEnterprise.getId(), appId,restEnterprise.getName());
			}

		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/enterprise/user/get", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<RestEnterprise> getEnterpriseUser(@RequestHeader("loginName") String loginName,
			@RequestHeader("domain") String domain) {
		EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(loginName, domain);
		RestEnterprise restEnterprise = new RestEnterprise();
		restEnterprise.setName(enterpriseUser.getName());
		restEnterprise.setDomainName(enterpriseUser.getAlias());
		restEnterprise.setContactEmail(enterpriseUser.getEmail());
		return new ResponseEntity<RestEnterprise>(restEnterprise, HttpStatus.OK);
	}
}
