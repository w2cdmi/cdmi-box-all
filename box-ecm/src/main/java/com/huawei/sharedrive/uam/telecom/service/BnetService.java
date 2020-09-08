package com.huawei.sharedrive.uam.telecom.service;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;

public interface BnetService {

	public String bindUserToProduct(String name, String aliasName, String contactEmail, String phoneNo,
			String productId, String domainName);

	public String bindUserToApp(String userName, String aliasName, String contactEmail, String phoneNo, String appId,
			String enterpriseDomain);

	public void updateBindInfo(BasicUserUpdateRequest ruser, EnterpriseUser selEnterpriseUser, String domain);

	public void unbindToApp(long enterpriseId, long authServerId, String dn, String filter, String ids,
			String sessionId);

	public void obtainUserInfo();

}
