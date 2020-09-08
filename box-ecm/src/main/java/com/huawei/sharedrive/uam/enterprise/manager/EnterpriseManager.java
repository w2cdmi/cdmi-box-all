package com.huawei.sharedrive.uam.enterprise.manager;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.enterprise.Enterprise;

public interface EnterpriseManager {

	Page<Enterprise> getFilterd(String filter, String appId, PageRequest pageRequest);

	Enterprise getById(long id);

	long getByDomainExclusiveId(Enterprise enterprise);

	String updateEnterpriseInfo(Enterprise enterprise);

	Enterprise getByDomainName(String domainName);

	Enterprise getByName(String domainName);

	void updateNetworkAuthStatus(Long id, byte networkAuthStatus);

	String paramValidate(Enterprise oldInfo, Enterprise enterprise);

	boolean checkOrganizeEnabled(long enterpriseId);
	
	boolean firstEnterCheckOrganizeOperPrivilege(long enterpriseId);
	void checkOrganizeOperPrivilege(long enterpriseId);

	void updateEnterprise(Enterprise enterprise);
}
