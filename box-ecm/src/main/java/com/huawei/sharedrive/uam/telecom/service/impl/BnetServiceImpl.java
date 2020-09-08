package com.huawei.sharedrive.uam.telecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;
import com.huawei.sharedrive.uam.telecom.service.BnetService;

import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;

@Service
public class BnetServiceImpl implements BnetService {

	@Autowired
	private EnterpriseManager enterpriseManager;

	@Autowired
	private AuthServerService authServerService;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private UserAccountManager userAccountManager;

	
	@Override
	public String bindUserToProduct(String name, String aliasName, String contactEmail,  String phoneNo,String productId,
			String domainName) {

		String str = bindUserToApp(name, aliasName, contactEmail,  phoneNo,productId, domainName);

		return str;
	}

	//创建用户
	@Override
	public String bindUserToApp(String userName, String aliasName, String contactEmail, String phoneNo,String appId,
			String enterpriseDomain) {
		// Enterprise format enterpriseUser
		EnterpriseUser enterpriseUser = new EnterpriseUser();
		enterpriseUser.setName(userName);
		enterpriseUser.setAlias(aliasName);
		enterpriseUser.setEmail(contactEmail);
		enterpriseUser.setMobile(phoneNo);

		try {
			// Get enterprise ID
			Enterprise enterprise = enterpriseManager.getByDomainName(enterpriseDomain);
			long enterpriseID = enterprise.getId();
			enterpriseUser.setEnterpriseId(enterpriseID);

			// Get auth server
			AuthServer authServer = authServerService.getByEnterpriseIdType(enterpriseID, "LocalAuth");
			enterpriseUser.setUserSource(authServer.getId());

			// Get user Id
			enterpriseUser = enterpriseUserManager.getEUserByLoginName(enterpriseUser.getName(),
					enterprise.getDomainName());
			long userId = enterpriseUser.getId();

			// User account create
			userAccountManager.create(userId, enterpriseID, appId,enterpriseUser.getName());

		} catch (Exception e) {

		}
		return "ok";
	}

	/**
	 * 修改用户绑定信息
	 * 
	 * 
	 */
	@Override
	public void updateBindInfo(BasicUserUpdateRequest ruser, EnterpriseUser selEnterpriseUser, String domain) {

		enterpriseUserManager.update(ruser, selEnterpriseUser, domain);

	}

	/**
	 * 解绑用户应用绑定信息
	 * 
	 * 
	 */
	@Override
	public void unbindToApp(long enterpriseId, long authServerId, String dn, String filter, String ids,
			String sessionId) {

		enterpriseUserManager.delete(enterpriseId, authServerId, dn, filter, ids, sessionId);
	}

	/**
	 * 获取用户信息
	 * 
	 * 
	 */
	@Override
	public void obtainUserInfo() {

	}

}
