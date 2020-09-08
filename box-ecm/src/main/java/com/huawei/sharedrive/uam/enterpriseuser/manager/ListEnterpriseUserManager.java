package com.huawei.sharedrive.uam.enterpriseuser.manager;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

@SuppressWarnings("PMD.ExcessiveParameterList")
public interface ListEnterpriseUserManager
{
    Page<EnterpriseUserExtend> getPagedEnterpriseUser(String sessionId, String dn, Long authServerId,String deptId,
        Long enterpriseId, String filter, PageRequest pageRequest);

	Page<EnterpriseUserExtend> getPagedEnterpriseUser(String sessionId, String dn, Long authServerId, String deptId,
			long enterpriseId, String filter, PageRequest request, long type, String status);
}
