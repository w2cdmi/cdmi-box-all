package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.LdapUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ListEnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.SqlUtils;

@Component
public class ListEnterpriseUserManagerImpl implements ListEnterpriseUserManager
{
	@Autowired
	private LdapUserManager ldapUserManager;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private EnterpriseService enterpriseService;


	@SuppressWarnings("PMD.ExcessiveParameterList")
	@Override
	public Page<EnterpriseUserExtend> getPagedEnterpriseUser(String sessionId, String dn, Long authServerId, String deptId, Long enterpriseId, String filter, PageRequest pageRequest) {
		Page<EnterpriseUser> page = null;

		if (StringUtils.isNotBlank(dn)) {
			ldapUserManager.insertLdapUser(sessionId, dn, authServerId);
			page = ldapUserManager.getPagedUserExtend(sessionId, dn, authServerId, enterpriseId, SqlUtils.stringToSqlLikeFields(filter), pageRequest);
		} else {
			page = enterpriseUserManager.getPagedEnterpriseUser(SqlUtils.stringToSqlLikeFields(filter), authServerId, deptId, enterpriseId, pageRequest);
		}
		List<EnterpriseUser> list = page.getContent();
		List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
		List<EnterpriseUserExtend> enterpriseUserExtendList = new ArrayList<EnterpriseUserExtend>(10);
		EnterpriseUserExtend enterpriseUserExtend;
		for (EnterpriseUser enterpriseUser : list) {
			enterpriseUserExtend = bulidEnterpriseUserExtend(enterpriseUser, enterpriseAccountList);
			enterpriseUserExtendList.add(enterpriseUserExtend);
		}
		Page<EnterpriseUserExtend> enterpriseUserExtendPage = new PageImpl<EnterpriseUserExtend>(enterpriseUserExtendList, pageRequest, page.getTotalElements());
		return enterpriseUserExtendPage;
	}

	private EnterpriseUserExtend bulidEnterpriseUserExtend(EnterpriseUser enterpriseUser, List<EnterpriseAccount> enterpriseAccounts) {
		EnterpriseUserExtend enterpriseUserExtend = EnterpriseUserExtend.copyEnterpriseUserPro(enterpriseUser);
		List<String> authAppIdList = enterpriseUserExtend.getAuthAppIdList();
		String authAppId;

		for (EnterpriseAccount enterpriseAccount : enterpriseAccounts) {
			authAppId = enterpriseAccount.getAuthAppId();
			try {
				UserAccount userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(), enterpriseUser.getEnterpriseId(), authAppId);

				if (null != userAccount) {
					enterpriseUserExtend.setCloudUserId(userAccount.getCloudUserId());
					enterpriseUserExtend.setSpaceQuota(userAccount.getSpaceQuota());
					enterpriseUserExtend.setSpaceUsed(userAccount.getSpaceUsed());
					authAppIdList.add(authAppId);
				}
			} catch (BusinessException e) {
				continue;
			}
		}
		return enterpriseUserExtend;
	}

	@Override
	public Page<EnterpriseUserExtend> getPagedEnterpriseUser(String sessionId, String dn, Long authServerId, String deptId, long enterpriseId, String filter, PageRequest pageRequest, long type, String status) {
		Page<EnterpriseUser> page = null;
		if (StringUtils.isNotBlank(dn)) {
			ldapUserManager.insertLdapUser(sessionId, dn, authServerId);
			page = ldapUserManager.getPagedUserExtend(sessionId, dn, authServerId, enterpriseId, SqlUtils.stringToSqlLikeFields(filter), pageRequest);
		} else {
			page = enterpriseUserManager.getPagedEnterpriseUser(SqlUtils.stringToSqlLikeFields(filter), authServerId, deptId, enterpriseId, pageRequest, type, status);
		}
		List<EnterpriseUser> list = page.getContent();
		List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
		List<EnterpriseUserExtend> enterpriseUserExtendList = new ArrayList<EnterpriseUserExtend>(10);
		EnterpriseUserExtend enterpriseUserExtend;
		for (EnterpriseUser enterpriseUser : list) {
			enterpriseUserExtend = bulidEnterpriseUserExtend(enterpriseUser, enterpriseAccountList);
			enterpriseUserExtendList.add(enterpriseUserExtend);
		
			EnterpriseSecurityPrivilege privilegeFilter = new EnterpriseSecurityPrivilege();
			privilegeFilter.setDepartmentId(Long.parseLong(deptId));
			privilegeFilter.setEnterpriseId(enterpriseId);
			privilegeFilter.setEnterpriseUserId(enterpriseUser.getId());
			List<EnterpriseSecurityPrivilege> privilegeList = enterpriseUserManager.listSecurityPrivilege(privilegeFilter,null);
			List<String> roleNames = new ArrayList<>();
			for(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege : privilegeList){
				if(enterpriseSecurityPrivilege.getRole() == EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER){
					if(Long.parseLong(deptId) != -1){
						roleNames.add("知识管理员");
					}else{
					Department department =	departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, enterpriseSecurityPrivilege.getDepartmentId());
						if(department!=null){
							roleNames.add(department.getName()+"-知识管理员");
						}	
					
					}
					
				}
				if(enterpriseSecurityPrivilege.getRole() == EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR){
					if(Long.parseLong(deptId) != -1){
						roleNames.add("主管");
					}else{
					Department department =	departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, enterpriseSecurityPrivilege.getDepartmentId());
						roleNames.add(department.getName()+"-主管");
					}
					
				}
			}
			enterpriseUserExtend.setRoleNames(roleNames);
			enterpriseUserExtend.setPrivileges(privilegeList);
			
			List<Department> departmentList = departmentService.getByEnterpriseUserId(enterpriseId, enterpriseUser.getId());
			List<String> deptNames = new ArrayList<>();
			String deptsName = "";
			if (departmentList.size() != 0) {
				for (int i = 0; i < departmentList.size(); i++) {
					if (deptsName.equals("")) {
						deptsName = departmentList.get(i).getName();
					} else {
						deptsName = deptsName + "," + departmentList.get(i).getName();
					}
					deptNames.add(departmentList.get(i).getName());
				}
				enterpriseUserExtend.setDepartmentName(deptsName);
			
			} else {
				Enterprise enterprise = enterpriseService.getById(enterpriseId);
				enterpriseUserExtend.setDepartmentName(enterprise.getName());
				deptNames.add(enterprise.getName());
			}
			enterpriseUserExtend.setDeptNames(deptNames);
			enterpriseUserExtend.setDepts(departmentList);
		}
		Page<EnterpriseUserExtend> enterpriseUserExtendPage = new PageImpl<EnterpriseUserExtend>(enterpriseUserExtendList, pageRequest, page.getTotalElements());
		return enterpriseUserExtendPage;
	}
}
