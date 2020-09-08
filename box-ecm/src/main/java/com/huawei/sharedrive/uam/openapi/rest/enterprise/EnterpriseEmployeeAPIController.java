package com.huawei.sharedrive.uam.openapi.rest.enterprise;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.enterprise.domain.AccountBasicConfig;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountBasicConfigService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ListEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.exception.ExceedQuotaException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;
import com.huawei.sharedrive.uam.openapi.domain.user.SpaceQuotaRequest;
import com.huawei.sharedrive.uam.openapi.manager.TokenMeApiManager;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.domain.DeptNode;
import com.huawei.sharedrive.uam.organization.domain.EnterpriseUserDept;
import com.huawei.sharedrive.uam.organization.manager.DepartmentManager;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import com.huawei.sharedrive.uam.util.TreeNodeUtil;
import com.huawei.sharedrive.uam.weixin.domain.WxUserEnterprise;
import com.huawei.sharedrive.uam.weixin.service.WxUserEnterpriseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping(value = "/api/v2/enterprise")
@Api(description = "员工管理api")
public class EnterpriseEmployeeAPIController extends AbstractCommonController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseEmployeeAPIController.class);

	@Autowired
	private ListEnterpriseUserManager listEnterpriseUserManager;

	@Autowired
	private AuthServerManager authServerManager;
	
    @Autowired
    private DepartmentManager departmentManager;
    
    @Autowired
    private DepartmentAccountService departmentAccountService;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private EnterpriseManager enterpriseManager;
    
    @Autowired
    private TokenMeApiManager tokenMeApiManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;

    @Autowired
    private WxUserEnterpriseService wxUserEnterpriseService;

    @Autowired
    private EnterpriseUserDeptService userDeptService;

    @Autowired
	private UserAccountManager userAccountManager;

    @Autowired
	private SystemConfigService systemConfigService;

    @Autowired
	private AccountBasicConfigService  accountBasicConfigService;

    @Autowired

	private EnterpriseAccountService enterpriseAccountService;

	@RequestMapping(value = "/{enterpriseid}/employee", method = RequestMethod.GET)
	@ApiOperation(value = "获得员工列表")
	public ResponseEntity<?> listEmployeeByeEnterpriseId(
			@PathVariable(value = "enterpriseid") long enterpriseid,
			@RequestParam(value = "search") String search, 
			@RequestParam(value = "type", defaultValue = "-1") long type,
			@RequestParam(value = "pageSize", defaultValue = "1000") Integer pageSize,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "deptId", defaultValue = "") String deptId,
			@RequestHeader("Authorization") String authorization) {
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseid);
		PageRequest request = new PageRequest();
		request.setSize(pageSize);
		request.setPage(page);
		if (StringUtils.isNotBlank(deptId)) {
			if ("all".equals(deptId)) {
				deptId = null;
			}
		} else {
			deptId = null;
		}
		if (authServerList.size() != 0) {
			Page<EnterpriseUserExtend> userPage = listEnterpriseUserManager.getPagedEnterpriseUser(null, null, authServerList.get(0).getId(), deptId, enterpriseid, search, request, type,null);
			return new ResponseEntity<Page<EnterpriseUserExtend>>(userPage, HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

	}
	
	
	///enterprises/v1/employees/employee?search={keyword}
	@RequestMapping(value = "/employees", method = RequestMethod.GET)
	public ResponseEntity<?> listEmployees(
			@RequestParam(value = "search", defaultValue = "") String search, 
			@RequestParam(value = "type", defaultValue = "-1") long type,
			@RequestParam(value = "pageSize", defaultValue = "1000") Integer pageSize,
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "status",defaultValue = "0") String status,
			@RequestParam(value = "deptId", defaultValue = "0") String deptId,
			@RequestHeader("Authorization") String authorization) {

		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		List<AuthServer> authServerList = authServerManager.getByEnterpriseId(userToken.getEnterpriseId());
		PageRequest request = new PageRequest();
		request.setSize(pageSize);
		request.setPage(page);
		
		if (authServerList.size() != 0) {
			Page<EnterpriseUserExtend> userPage = listEnterpriseUserManager.getPagedEnterpriseUser(null, null,
					authServerList.get(0).getId(), deptId, userToken.getEnterpriseId(), search, request, type,status);
			return new ResponseEntity<Page<EnterpriseUserExtend>>(userPage, HttpStatus.OK);
		}
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

	}
	
	
	// /enterprises/v1/depts_employees?deptid={deptid}


	@RequestMapping(value = "/depts_employees", method = RequestMethod.GET)
	public ResponseEntity<?> listDeptsAndEmployees(
			@RequestParam(value = "deptId", defaultValue = "0") long deptId,
			@RequestHeader("Authorization") String authorization) {
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
        List<Department> depts = departmentManager.listDepByParentDepId(deptId, enterpriseId);
        String result = "";
        List<DeptNode> list = new ArrayList<>();
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, "LocalAuth");
        if (depts != null) {
            for (Department dept : depts) {
                DeptNode mj = TreeNodeUtil.convertDept2Node(departmentAccountService,enterpriseId, dept);
                mj.setSubEmployees(getAllSubUserTotal(dept.getDepartmentId(),enterpriseId,authServer.getId()));
                list.add(mj);
            }
        }
        listUsers(deptId, userToken, enterpriseId, list, authServer);
        result=TreeNodeUtil.list2Json(list);
        return new ResponseEntity<String>(result, HttpStatus.OK);
	}

    private void listUsers(@RequestParam(value = "deptId", defaultValue = "0") long deptId, UserToken userToken, long enterpriseId, List<DeptNode> list, AuthServer authServer) {
        if (authServer != null) {
            List<EnterpriseUser> userList = enterpriseUserService.getFilterd(null, authServer.getId(), deptId, enterpriseId, null, null);
            EnterpriseUser2DeptNode(deptId, userToken.getAccountId(), list, userList);
        }
    }



    private void EnterpriseUser2DeptNode(@RequestParam(value = "deptId", defaultValue = "-1") long deptId, long enterpriseId, List<DeptNode> list, List<EnterpriseUser> userList) {
        if (userList != null) {
            for (EnterpriseUser enterpriseUser : userList) {
                UserAccount userAccount = userAccountService.get(enterpriseUser.getId(), enterpriseId);
                if (userAccount != null) {
                    DeptNode mj = TreeNodeUtil.convertUser2Node(deptId, enterpriseUser, userAccount);
                    list.add(mj);
                }
            }
        }
    }


    @RequestMapping(value = "/employees/{employeeid}", method = RequestMethod.GET)
	public ResponseEntity<?> getEmployeeInfo(
			@PathVariable(value = "employeeid") long employeeid,
			@RequestHeader("Authorization") String authorization) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

  	    Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (enterprise == null) {
            throw new NoSuchUserException("get user info failed, enterprise is null");
        }

        EnterpriseUser user = enterpriseUserService.get(employeeid, enterpriseId);
        if(user == null) {
            throw new NoSuchUserException("get user info failed, user is null");
        }

        EnterpriseUserAccount enterpriseUserAccount = tokenMeApiManager.getUserInfo(employeeid, userToken.getAccountId(), enterpriseId);
        if (enterpriseUserAccount == null) {
            throw new NoSuchUserException("get user info failed, userAccount is null");
        }
        ResponseUser responseUser = ResponseUser.convetToResponseUser(enterpriseUserAccount, enterpriseUserAccount.getAppId());
        return new ResponseEntity<ResponseUser>(responseUser, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/employees/{employeeid}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteEmployee(
			@PathVariable(value = "employeeid") long employeeid,
			@RequestHeader("Authorization") String authorization) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

  	    Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (enterprise == null) {
            throw new NoSuchUserException("get user info failed, enterprise is null");
        }
        EnterpriseUser user = enterpriseUserService.get(employeeid, enterpriseId);
        if(user == null) {
            throw new NoSuchUserException("get user info failed, user is null");
        }
        userDeptService.deleteByEnterpriseUserId(employeeid,enterpriseId);
        wxUserEnterpriseService.deleteByEnterpriseUserIdAndEnterpriseId(employeeid,enterpriseId);
        enterpriseUserService.deleteById(enterpriseId, employeeid);
        return new ResponseEntity<String>( HttpStatus.OK);
	}
	
	
	
	///enterprises/v1/employees/{employeeid}/depts_roles
	
	
	@RequestMapping(value = "/employees/{employeeid}/depts_roles", method = RequestMethod.GET)
	public ResponseEntity<?> listEmployeeInfo(
			@PathVariable(value = "employeeid") long employeeid,
			@RequestParam(value = "deptid") String deptid,
			@RequestHeader("Authorization") String authorization) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();

  	    Enterprise enterprise = enterpriseManager.getById(enterpriseId);
        if (enterprise == null) {
            throw new NoSuchUserException("get user info failed, enterprise is null");
        }

        EnterpriseUser user = enterpriseUserService.get(employeeid, enterpriseId);
        if(user == null) {
            throw new NoSuchUserException("get user info failed, user is null");
        }
        
        EnterpriseSecurityPrivilege filter = new EnterpriseSecurityPrivilege();
        filter.setEnterpriseUserId(employeeid);
        filter.setEnterpriseId(enterpriseId);
        if(deptid!=null){
        	filter.setDepartmentId(Long.parseLong(deptid));
        }
        List<EnterpriseSecurityPrivilege> securityPrivilegelist = enterpriseUserManager.listSecurityPrivilege(filter,null);
        
        for(EnterpriseSecurityPrivilege privilege :securityPrivilegelist){
        	privilege.setDeptName(departmentManager.getDeptById(enterpriseId, privilege.getDepartmentId()).getName());
        }
        return new ResponseEntity<List<EnterpriseSecurityPrivilege>>(securityPrivilegelist, HttpStatus.OK);
	}

	
	
	@RequestMapping(value = "/employees/{employeeid}/dismiss", method = RequestMethod.PUT)
	public ResponseEntity<?> updateEmployeeStatusDismiss(
			@PathVariable(value = "employeeid") long employeeid,
			@RequestHeader("Authorization") String authorization) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseUser user = enterpriseUserService.get(employeeid, enterpriseId);
		List<Long> userIds = new ArrayList<>();
		userIds.add(user.getId());
		enterpriseUserManager.updateEnterpriseUserStatus(userIds, EnterpriseUserStatus.DISMISS, enterpriseId);
        return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/employees/{employeeid}/handover", method = RequestMethod.PUT)
	public ResponseEntity<?> updateEmployeeStatusHandover(
			@PathVariable(value = "employeeid") long employeeid,
			@RequestHeader("Authorization") String authorization) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);
		long enterpriseId = userToken.getEnterpriseId();
		EnterpriseUser user = enterpriseUserService.get(employeeid, enterpriseId);
		List<Long> userIds = new ArrayList<>();
		userIds.add(user.getId());
		enterpriseUserManager.updateEnterpriseUserStatus(userIds, EnterpriseUserStatus.HANDOVER, enterpriseId);
        return new ResponseEntity<String>(HttpStatus.OK);
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
