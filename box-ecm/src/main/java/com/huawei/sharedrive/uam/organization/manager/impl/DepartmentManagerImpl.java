package com.huawei.sharedrive.uam.organization.manager.impl;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.manager.AccountBasicConfigManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.BusinessErrorCode;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.NoSuchItemException;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.domain.DeptNode;
import com.huawei.sharedrive.uam.organization.manager.DepartmentManager;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.uam.teamspace.domain.RestTeamSpaceInfo;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.uam.domain.AuthApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DepartmentManagerImpl implements DepartmentManager {
	private static Logger logger = LoggerFactory.getLogger(DepartmentManagerImpl.class);

	@Autowired
	AppBasicConfigService appBasicConfigService;

	@Autowired
	AccountBasicConfigManager accountBasicConfigManager;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	DepartmentAccountService departmentAccountService;

	@Autowired
	EnterpriseUserService enterpriseUserService;

	@Autowired
	AuthAppService authAppService;

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	EnterpriseAccountManager enterpriseAccountManager;

	@Autowired
	EnterpriseAccountService enterpriseAccountService;

	@Autowired
	UserAccountManager userAccountManager;

	@Autowired
	TeamSpaceService teamSpaceService;

	@Override
	public Long create(Department dept) {
		try {
			//自动生成部门空间
			RestTeamSpaceInfo info = createDeptTeamSpace(dept);
			if (info != null) {
				//生成部门账号，其他账号使用部门空间的cloudUserId.
				dept.setState(Department.STATE_ENABLE);
				long deptId = departmentService.create(dept);
				String authAppId = authAppService.getDefaultWebApp().getAuthAppId();
				EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(dept.getEnterpriseId(), authAppId);
				DepartmentAccount deptAccount = new DepartmentAccount(enterpriseAccount.getAccountId(), dept.getEnterpriseId(), deptId);
				fillDeptAccountWithAppConfig(deptAccount, authAppId);
				//ufm返回的结果中id对应的就是cloudUserId(Teamspace主键也是cloudUserId).
				deptAccount.setCloudUserId(info.getId());
				deptAccount.setCreatedAt(new Date());
				departmentAccountService.create(deptAccount);
				return deptId;
			}
		} catch (Exception e) {
			logger.error("Failed to create department, enterpriseId={}, deptId={}, error={}", dept.getEnterpriseId(), dept.getDepartmentId(), e.getMessage());
			e.printStackTrace();
		}

		return -1L;
	}

	public void fillDeptAccountWithAppConfig(DepartmentAccount account, String authAppId) {
		AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(authAppId);
		if (null == appBasicConfig) {
			logger.error("[deptAccount] find appBasicConfig failed" + " authAppId:" + authAppId);
			throw new BusinessException(BusinessErrorCode.NotFoundException, "find userAccount failed");
		}
		AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
		accountBasicConfig.setAccountId(account.getAccountId());
		accountBasicConfig = accountBasicConfigManager.queryAccountBasicConfig(accountBasicConfig, authAppId);
		if (!accountBasicConfig.getUserSpaceQuota().equals("-1")) {
			appBasicConfig.setUserSpaceQuota(Long.parseLong(accountBasicConfig.getUserSpaceQuota()));
		}
		if (!accountBasicConfig.getUserVersions().equals("-1")) {
			appBasicConfig.setMaxFileVersions(Integer.parseInt(accountBasicConfig.getUserVersions()));
		}

		appBasicConfig.setEnableTeamSpace(accountBasicConfig.isEnableTeamSpace());

		if (!accountBasicConfig.getMaxTeamSpaces().equals("-1")) {
			appBasicConfig.setMaxTeamSpaces(Integer.parseInt(accountBasicConfig.getMaxTeamSpaces()));
		}
		if (!accountBasicConfig.getTeamSpaceQuota().equals("-1")) {
			appBasicConfig.setTeamSpaceQuota(Long.parseLong(accountBasicConfig.getTeamSpaceQuota()));
		}

		departmentAccountService.buildDepartmentAccount(account, appBasicConfig);
	}

	public RestTeamSpaceInfo createDeptTeamSpace(Department dept) {
		RestTeamSpaceCreateRequest request = new RestTeamSpaceCreateRequest();
		//设置空间名称
		request.setName(dept.getName());
		//设置“上传文件时通知”
//            request.setUploadNotice("disable");

		//设为不限制
		request.setMaxMembers(-1);
		request.setMaxVersions(-1);
		request.setSpaceQuota((long) -1);
		request.setType(1);//部门空间
		request.setOwnerBy(-2L);//系统所有。此处不指定Owner，使用[account,...]Token，UFM会自动设置owner为-2（APP_USER_ID)
		AuthApp app = authAppService.getDefaultWebApp();
		AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(app.getAuthAppId());
		request.setRegionId(appBasicConfig.getUserDefaultRegion().byteValue());

		return teamSpaceService.createTeamSpace(dept.getEnterpriseId(), app.getAuthAppId(), request);
	}

	/**
	 * List all enterprise User by enterpriseId
	 * 
	 */
	@Override
	public List<DeptNode> getAllByEnterpriseId(long id, long accountId) {
		List<Department> deps = departmentService.listDeptTreeByEnterpriseId(id);
		
		List<EnterpriseUser> list = new ArrayList<>();
//		List<EnterpriseUser> allEnterpriseUser = enterpriseUserService.getAllEnterpriseUser(id);
//		for (EnterpriseUser enterpriseUser : allEnterpriseUser) {
//			UserAccount userAccount = userAccountService.get(enterpriseUser.getId(), accountId);
//			if (userAccount != null) {
//				enterpriseUser.setId(userAccount.getCloudUserId());
//				list.add(enterpriseUser);
//			}
//		}
		List<DeptNode> deptTree = convert2NodeList(deps, list);
		return deptTree;
	}

	/**
	 * Convert list into node list that they have dependency relationship for
	 * each other
	 * 
	 * @param deptList
	 */
	private List<DeptNode> convert2NodeList(List<Department> deptList, List<EnterpriseUser> users) {
		List<DeptNode> list = new ArrayList<>();
		for (Department dept : deptList) {
			DeptNode mj = new DeptNode();
			mj.setId(String.valueOf(dept.getDepartmentId()));
			mj.setpId(String.valueOf(dept.getParentId()));
			mj.setName(dept.getName());
			mj.setType("department");
			list.add(mj);
		}
		if (users != null) {
			for (EnterpriseUser user : users) {
				DeptNode mj = new DeptNode();
				mj.setId(String.valueOf(user.getId()));
				mj.setName(user.getName());
				mj.setEmail(user.getEmail());
				mj.setAlias(user.getAlias());
				mj.setType("user");
				list.add(mj);
			}
		}
		return list;
	}

	/**
	 * Create node list into node tree that their node have dependency
	 * relationship for each other
	 * 
	 * @param deptList
	 *            the bean list
	 */
	public List<DeptNode> createDeptTree(List<Department> deptList, List<EnterpriseUser> users) {
		List<DeptNode> result = new ArrayList<>();
		List<DeptNode> targetList = convert2NodeList(deptList, users);
		for (DeptNode mj1 : targetList) {
			boolean isRoot = true;
			for (DeptNode mj2 : targetList) {
				if (mj2.getId().equals(mj1.getpId())) {
					isRoot = false;
					if (mj2.getChildren() == null) {
						mj2.setChildren(new ArrayList<DeptNode>());
					}
					mj2.getChildren().add(mj1);
					break;
				}
			}
			if (isRoot) {
				result.add(mj1);
			}
		}
		return result;
	}
	public List<DeptNode> createDeptTree(List<Department> deptList) {
		List<DeptNode> targetList = convert2NodeList(deptList,null);
		return targetList;
	}

	@Override
	public Department getDepartmentByPath(long enterpriseId, String deptPath) {
		Department targetDept = null;
		if (null != deptPath && deptPath.trim().length() > 0) {
			targetDept = obtainTartgetDept(enterpriseId, deptPath, -1L);
		}
		return targetDept;
	}

	public Department obtainTartgetDept(long enterpriseId, String depts, long parentid) {
		Department retDept = null;
		int index = depts.indexOf("\\");

		if (index > -1) {
			String topClassDeptNm = depts.substring(0, index);
			Department dept = departmentService.getEnpDeptByNameAndParent(enterpriseId, topClassDeptNm, parentid);

			if (dept != null) {
				String pStr = depts.substring(index + 1, depts.length());
				retDept = obtainTartgetDept(enterpriseId, pStr, dept.getDepartmentId());
			} else {
				throw new NoSuchItemException("DepartmentNotFoudException");
			}
		} else {
			retDept = departmentService.getEnpDeptByNameAndParent(enterpriseId, depts, parentid);
		}
		return retDept;
	}

	@Override
	public Department getDeptById(long enterpriseId, long id) {
		return departmentService.getByEnterpriseIdAndDepartmentId(enterpriseId, id);
	}

	public List<DeptNode> listDeptTreeByEnterpriseId(Long enterpriseId) {
		List<Department> list = null;
		List<DeptNode> deptList = null;
		if (null != enterpriseId) {
			list = departmentService.listDeptTreeByEnterpriseId(enterpriseId);
		}
		if (list != null && list.size() > 0) {
			deptList = convert2NodeList(list, null);
		}
		return deptList;
	}

	@Override
	public List<DeptNode> getRootDepartment(long enterpriseId) {
		List<Department> list = departmentService.listRootDepartmentByEnterpriseId(enterpriseId);
		List<DeptNode> targetList = convert2NodeList(list,null);
		return targetList;
	}

	@Override
	public List<Department> listDepByParentDepId(long parentDepId, long enterpriseId) {
		List<Department> list = departmentService.listDepByParentDepId(parentDepId,enterpriseId);
		return list;
	}

	private List<Long> getAllDeptIds(List<Long> deptIds,long deptId,long enterpriseId){
   	 List<Department> listDepByParentDepId = listDepByParentDepId(deptId,enterpriseId);
   	 if(listDepByParentDepId.size()!=0){
   		 for (Department department : listDepByParentDepId) {
   			 long id = department.getDepartmentId();
   			 deptIds.add(id);
   			 getAllDeptIds(deptIds,id,enterpriseId);
			}
   	 }
   	return deptIds;
   }
	
	@Override
	public Page<EnterpriseUserExtend> delete(Long departmentId,long enterpriseId,long authServerId) {
		List<Long> list = new ArrayList<>();
		list.add(departmentId);
		List<EnterpriseUser> userList = new ArrayList<>();
		int total = 0;
		try {
			
			list = getAllDeptIds(list, departmentId, enterpriseId);
			if(list!=null && list.size()>0 ){
				List<EnterpriseUser> tempList = null;
				for(Long l : list){
					tempList = enterpriseUserService.getFilterd(null, authServerId, l, enterpriseId, null, null);
					userList.addAll(tempList);
					total =total + tempList.size();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(CollectionUtils.isEmpty(userList)){
			for(Long l : list){
				departmentService.delete(enterpriseId, l);
			}
			return null;
		}
		Page<EnterpriseUser> page = new PageImpl<EnterpriseUser>(userList, null, total);
		List<EnterpriseUser> pageList = page.getContent();
        List<EnterpriseAccount> enterpriseAccountList = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
        List<EnterpriseUserExtend> enterpriseUserExtendList = new ArrayList<EnterpriseUserExtend>(10);
        EnterpriseUserExtend enterpriseUserExtend;
        for (EnterpriseUser enterpriseUser : pageList)
        {
            enterpriseUserExtend = bulidEnterpriseUserExtend(enterpriseUser, enterpriseAccountList);
            enterpriseUserExtendList.add(enterpriseUserExtend);
        }
        Page<EnterpriseUserExtend> enterpriseUserExtendPage = new PageImpl<EnterpriseUserExtend>(
            enterpriseUserExtendList, null, page.getTotalElements());
        
		return enterpriseUserExtendPage;
	}
	
	private EnterpriseUserExtend bulidEnterpriseUserExtend(EnterpriseUser enterpriseUser,
	        List<EnterpriseAccount> enterpriseAccounts)
	    {
	        EnterpriseUserExtend enterpriseUserExtend = EnterpriseUserExtend.copyEnterpriseUserPro(enterpriseUser);
	        List<String> authAppIdList = enterpriseUserExtend.getAuthAppIdList();
	        String authAppId;
	        for (EnterpriseAccount enterpriseAccount : enterpriseAccounts)
	        {
	            authAppId = enterpriseAccount.getAuthAppId();
	            try
	            {
	                UserAccount userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(),
	                    enterpriseUser.getEnterpriseId(),
	                    authAppId);
	                if (null != userAccount)
	                {
	                    authAppIdList.add(authAppId);
	                }
	            }
	            catch (BusinessException e)
	            {
	                continue;
	            }
	        }
	        return enterpriseUserExtend;
	    }
	
	@Override
	public void update(Department dept) {
		
		departmentService.update(dept);
		
	}

	@Override
	public List<Department> search(long enterpriseId, String name) {
		// TODO Auto-generated method stub
		return departmentService.search(enterpriseId,name);
	} 

}
