package com.huawei.sharedrive.uam.openapi.rest.enterprise;

import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ListEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RestWxMpRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestWxMpUserRegister;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeApiManager;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.domain.DeptNode;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.manager.DepartmentManager;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.util.TreeNodeUtil;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.WxMpUserInfo;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/api/v2/enterprise")
public class EnterpriseDeptAPIController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseDeptAPIController.class);

	@Autowired
	private AuthServerManager authServerManager;

	@Autowired
	private DepartmentManager departmentManager;

	@Autowired
	private DepartmentAccountService departmentAccountService;

	@Autowired
	private EnterpriseUserManager enterpriseUserManager;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	WxOauth2Service wxOauth2Service;

	@Autowired
	private WxUserEnterpriseService wxUserEnterpriseService;

	@Autowired
	private EnterpriseUserDeptService userDeptService;

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private EnterpriseService enterpriseService;

	@RequestMapping(value = "/depts", method = RequestMethod.GET)
	public ResponseEntity<?> listDepts(@RequestParam(value = "deptId", defaultValue = "0") long deptid, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		List<Department> depts = departmentManager.listDepByParentDepId(deptid, enterpriseId);
		List<DeptNode> list = new ArrayList<>();
		for (Department department : depts) {
			list.add(TreeNodeUtil.convertDept2Node(departmentAccountService, enterpriseId, department));
		}
		String result = TreeNodeUtil.list2Json(list);
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/all", method = RequestMethod.GET)
	public ResponseEntity<?> listDepts(@RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		List<DeptNode> list = departmentManager.getAllByEnterpriseId(enterpriseId, userToken.getAccountId());
	    AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, "LocalAuth");
		for(DeptNode deptNode : list){
			deptNode.setSubEmployees(getAllSubUserTotal(Long.parseLong(deptNode.getId()),enterpriseId,authServer.getId()));
		}
		String result = TreeNodeUtil.list2Json(list);
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/search", method = RequestMethod.GET)
	public ResponseEntity<?> searchDepts(@RequestParam(value = "search") String search, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		List<Department> depts = departmentManager.search(enterpriseId, search);
		List<DeptNode> list = new ArrayList<>();
		for (Department department : depts) {
			list.add(TreeNodeUtil.convertDept2Node(departmentAccountService, enterpriseId, department));
		}
		String result = TreeNodeUtil.list2Json(list);
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/{employeeId}/depts", method = RequestMethod.GET)
	public ResponseEntity<?> listUserDepts(@PathVariable(value = "employeeId") long employeeId, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseUserDept filter = new EnterpriseUserDept();
		filter.setEnterpriseId(enterpriseId);
		filter.setEnterpriseUserId(employeeId);
		List<EnterpriseUserDept> userDeptList = userDeptService.getByEnterPriseUser(filter);
		List<Department> list = new ArrayList<>();
		for (EnterpriseUserDept enterpriseUserDept : userDeptList) {
			list.add(departmentManager.getDeptById(enterpriseId, enterpriseUserDept.getDepartmentId()));
		}
		return new ResponseEntity<List<Department>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/enterpriseList", method = RequestMethod.POST)
	public ResponseEntity<?> listEnterpriseByUnionId(@RequestBody RestWxMpRequest restWxMpRequest) {
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(restWxMpRequest.getMpId(), restWxMpRequest.getCode(), restWxMpRequest.getIv(), restWxMpRequest.getEncryptedData());
		List<WxUserEnterprise> enterpriseList = wxUserEnterpriseService.listByUnionId(mpUserInfo.getUnionId());
		List<Enterprise> list = new ArrayList<>();
		for (WxUserEnterprise WxUserEnterprise : enterpriseList) {

			if (WxUserEnterprise.getEnterpriseId() != null && WxUserEnterprise.getEnterpriseId() != 0) {
				Enterprise enterprise = enterpriseService.getById(WxUserEnterprise.getEnterpriseId());
				if (enterprise != null) {
					Enterprise resultEnterprise = new Enterprise();
					resultEnterprise.setId(enterprise.getId());
					resultEnterprise.setName(enterprise.getName());
					list.add(resultEnterprise);
				}

			}

		}
		return new ResponseEntity<List<Enterprise>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = { "/employee/uinonId" }, method = RequestMethod.POST)
	public ResponseEntity<?> employeesByUnionId(@RequestBody RestWxMpUserRegister restWxMpRequest) {
		WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(restWxMpRequest.getMpId(), restWxMpRequest.getCode(), restWxMpRequest.getIv(), restWxMpRequest.getEncryptedData());
		WxUserEnterprise old = wxUserEnterpriseService.getByUnionIdAndEnterpriseId(mpUserInfo.getUnionId(), restWxMpRequest.getEnterpriseId());
		if (old != null) {
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/user_dept", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addUserDept(@RequestBody EnterpriseUserDept requset, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseUserDept userDept = new EnterpriseUserDept(requset.getEnterpriseUserId(), enterpriseId, requset.getDepartmentId());
		userDeptService.create(userDept);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user_dept", method = RequestMethod.PUT)
	public ResponseEntity<?> uppdatUserDept(@RequestBody EnterpriseUserDept userDept, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		userDept.setEnterpriseId(enterpriseId);
		// EnterpriseUserDept userDept=new EnterpriseUserDept(employeeId, enterpriseId,destDeptId);
		// userDeptService.deleteByDepartmentIdAndEnterpriseUserId(enterpriseId,userDept.getSrcDeptId(), userDept.getEnterpriseUserId());
		// userDeptService.create(userDept);
		userDeptService.update(userDept);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/user_dept", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteUserDept(@RequestBody EnterpriseUserDept requset, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		userDeptService.deleteByDepartmentIdAndEnterpriseUserId(enterpriseId, requset.getDepartmentId(), requset.getEnterpriseUserId());
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/{deptid}/director", method = RequestMethod.GET)
	public ResponseEntity<?> getDeptDirector(@PathVariable(value = "deptid") long deptid, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseSecurityPrivilege privilege = enterpriseUserManager.getDeptManager(enterpriseId, deptid);
		return new ResponseEntity<EnterpriseSecurityPrivilege>(privilege, HttpStatus.OK);

	}

	/// enterprises/v1/depts/{deptid}/director
	@RequestMapping(value = "/depts/{deptid}/director", method = RequestMethod.PUT)
	public ResponseEntity<?> getDeptDirector(@PathVariable(value = "deptid") long deptid, @RequestParam(value = "employeeId") long employeeid, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		enterpriseUserManager.addDeptManager(enterpriseId, deptid, employeeid, userToken);
		return new ResponseEntity<EnterpriseUser>(HttpStatus.OK);
	}

	/// enterprises/v1/depts/{deptid}/specialists?type=knowledge/hr/security

	@RequestMapping(value = "/depts/{deptid}/specialists", method = RequestMethod.PUT)
	public ResponseEntity<?> addDeptSpecialists(@PathVariable(value = "deptid") long deptid, @RequestParam(value = "type") String type, @RequestParam(value = "employeeId") long employeeId, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
		filter.setDepartmentId(deptid);
		filter.setEnterpriseId(enterpriseId);
		filter.setEnterpriseUserId(employeeId);
		List<EnterpriseSecurityPrivilege>  listPrivilege = enterpriseUserManager.listSecurityPrivilege(filter, new Limit(0L, 100));
		for(EnterpriseSecurityPrivilege privilege : listPrivilege){
			enterpriseUserManager.deletePrivilege(privilege, userToken);
		}
		

		if (type.equals("knowledge")) {
			enterpriseUserManager.addArchiveOwner(enterpriseId, deptid, employeeId, userToken);
		}
		if (type.equals("director")) {
			enterpriseUserManager.addDeptManager(enterpriseId, deptid, employeeId, userToken);
		}

		return new ResponseEntity<ResponseUser>(HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/{deptid}/specialists", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteDeptSpecialists(@PathVariable(value = "deptid") long deptid, @RequestBody EnterpriseSecurityPrivilege privilege, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		privilege.setEnterpriseId(enterpriseId);
		enterpriseUserManager.deletePrivilege(privilege,userToken);
		return new ResponseEntity<ResponseUser>(HttpStatus.OK);
	}

	/// enterprises/v1/depts/{deptid}/specialists/specialist?type=knowledge/hr/security/it
	@RequestMapping(value = "/depts/{deptid}/specialists/specialist", method = RequestMethod.GET)
	public ResponseEntity<?> getDeptSpecialistBy(@PathVariable(value = "deptid") long deptid,
			@RequestParam(value = "type",defaultValue="") String type,
			@RequestParam(value = "limit",defaultValue="20") int limit,
			@RequestParam(value = "offset",defaultValue="0") long offset,
			@RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

		EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
		filter.setDepartmentId(deptid);
		filter.setEnterpriseId(enterpriseId);
		Limit limitObj = new Limit(offset, limit);
		if(!"".equals(type)){
			byte roleTyle = getRole(type);
			if (roleTyle != 0) {
				filter.setRole(roleTyle);
			}
		}
		List<EnterpriseSecurityPrivilege> specialist = enterpriseUserManager.listSecurityPrivilege(filter,limitObj);
		for (EnterpriseSecurityPrivilege privilege : specialist) {
			EnterpriseUser enterpriseUser = enterpriseUserService.get(privilege.getEnterpriseUserId(), enterpriseId);
			Department department = departmentManager.getDeptById(privilege.getEnterpriseId(), privilege.getDepartmentId());
			privilege.setDeptName(department.getName());
			privilege.setUserName(enterpriseUser.getAlias());
			privilege.setLoginName(enterpriseUser.getName());
			privilege.setPhone(enterpriseUser.getMobile());

		}
		return new ResponseEntity<>(specialist, HttpStatus.OK);
	}
	
	
	/// enterprises/v1/depts/{deptid}/specialists/specialist?type=knowledge/hr/security/it
	@RequestMapping(value = "/depts/{deptid}/specialists", method = RequestMethod.GET)
	public ResponseEntity<?> getPageDeptSpecialist(@PathVariable(value = "deptid") long deptid,
			@RequestParam(value = "type",defaultValue="") String type,
			@RequestParam(value = "limit",defaultValue="20") int limit,
			@RequestParam(value = "offset",defaultValue="0") long offset,
			@RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

		EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
		filter.setDepartmentId(deptid);
		filter.setEnterpriseId(enterpriseId);
		Limit limitObj = new Limit(offset, limit);
		if(!"".equals(type)){
			byte roleTyle = getRole(type);
			if (roleTyle != 0) {
				filter.setRole(roleTyle);
			}
		}
		List<EnterpriseSecurityPrivilege> specialist = enterpriseUserManager.listSecurityPrivilege(filter,limitObj);
		for (EnterpriseSecurityPrivilege privilege : specialist) {
			EnterpriseUser enterpriseUser = enterpriseUserService.get(privilege.getEnterpriseUserId(), enterpriseId);
			Department department = departmentManager.getDeptById(privilege.getEnterpriseId(), privilege.getDepartmentId());
			if(department!=null){
				privilege.setDeptName(department.getName());
				privilege.setUserName(enterpriseUser.getAlias());
				privilege.setLoginName(enterpriseUser.getName());
				privilege.setPhone(enterpriseUser.getMobile());
	
			}else{
				enterpriseUserManager.deletePrivilege(privilege,userToken);
			}
			
		}
		int total  = enterpriseUserManager.listSecurityPrivilegeTotal(filter,limitObj);
		PageRequest request = new PageRequest();
		PageImpl<EnterpriseSecurityPrivilege> page =new PageImpl<>(specialist,request,total);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/{deptid}/specialists/special", method = RequestMethod.GET)
	public ResponseEntity<?> getDeptSpecialists(@PathVariable(value = "deptid") long deptid, @RequestParam(value = "type",defaultValue="") String type, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

		EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
		filter.setDepartmentId(deptid);
		filter.setEnterpriseId(enterpriseId);
		if(!"".equals(type)){
			byte roleTyle = getRole(type);
			if (roleTyle != 0) {
				filter.setRole(roleTyle);
			}
		}
		
		EnterpriseSecurityPrivilege privilege = enterpriseUserManager.getPrivilege(filter);
		return new ResponseEntity<>(privilege, HttpStatus.OK);
	}

	@RequestMapping(value = "/depts", method = RequestMethod.POST)
	public ResponseEntity<?> addDept(@RequestParam(value = "parentId") long parentId, @RequestParam(value = "name") String name, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		Department department = new Department();
		department.setEnterpriseId(enterpriseId);
		department.setName(name);
		department.setState(Department.STATE_ENABLE);
		department.setModifiedAt(new Date());
		department.setCreatedAt(new Date());
		department.setParentId(parentId);
		departmentManager.create(department);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	
	
	@RequestMapping(value = "/depts/{deptid}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateDept(@PathVariable(value = "deptid") long deptid, @RequestBody Department department, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		Department dbDepartment = departmentManager.getDeptById(enterpriseId, deptid);
		dbDepartment.setName(department.getName());
		dbDepartment.setModifiedAt(new Date());
		departmentManager.update(dbDepartment);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/{deptid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteDept(@PathVariable(value = "deptid") long deptid, @RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		departmentManager.delete(deptid, enterpriseId, authServerManager.getByEnterpriseId(enterpriseId).get(0).getId());
		departmentAccountService.deleteBydeptId(deptid, enterpriseId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/depts/{deptid}/approve", method = RequestMethod.PUT)
	public ResponseEntity<?> updateDeptApprove(@PathVariable(value = "deptid") long deptid, @RequestParam(value = "approve") boolean approve, @RequestHeader("Authorization") String authorization) {
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		DepartmentAccount departmentAccount = departmentAccountService.getByDeptIdAndEnterpriseId(deptid, enterpriseId);
		departmentAccount.setFileNeedApprove(approve);
		departmentAccountService.update(departmentAccount);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private byte getRole(@RequestParam(value = "type") String type) {
		byte roleTyle = 0;
		if (type.equals("knowledge")) {
			roleTyle = EnterpriseSecurityPrivilege.ROLE_ARCHIVE_MANAGER;
		}
		if (type.equals("director")) {
			roleTyle = EnterpriseSecurityPrivilege.ROLE_DEPT_DIRECTOR;
		}
		return roleTyle;
	}
	private  int getAllSubUserTotal(long deptId,long enterpriseId,long authServerId){
        List<Department> depts = departmentManager.listDepByParentDepId(deptId, enterpriseId);
        List<EnterpriseUser> userList = enterpriseUserService.getFilterd(null, authServerId, deptId, enterpriseId, null, null);
        int total=0;
        for(Department   department: depts){
            total=total+getAllSubUserTotal(department.getDepartmentId(),enterpriseId,authServerId);
        }
        return  total+userList.size();

    }
}
