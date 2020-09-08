package com.huawei.sharedrive.uam.organization.web;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuser.web.EnterpriseUserController;
import com.huawei.sharedrive.uam.exception.ConflictException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.organization.domain.*;
import com.huawei.sharedrive.uam.organization.manager.DepartmentManager;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import com.huawei.sharedrive.uam.organization.service.EnterpriseUserDeptService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/enterprise/admin/organize")
public class DepartmentController extends AbstractCommonController {
    @Autowired
    private AuthAppService authAppService;
    @Autowired
    private AuthServerManager authServerManager;
    @Autowired
	private DepartmentManager departmentManager;
	@Autowired
    private EnterpriseManager enterpriseManager;
    @Autowired
    private AdminLogManager adminLogManager;
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    @Autowired
    private DepartmentAccountService departmentAccountService;
    @Autowired
    private EnterpriseUserDeptService userDeptService;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    

	private static Logger logger = LoggerFactory.getLogger(EnterpriseUserController.class);
	private JsonGenerator jsonGenerator = null;
	private ObjectMapper objectMapper = null;

	@RequestMapping(value = "/enterList", method = RequestMethod.GET)
	public String enterList(Model model) {
		AuthApp authApp = authAppService.getDefaultWebApp();
		if(authApp != null) {
			model.addAttribute("appId", authApp.getAuthAppId());
		}

		return "enterprise/admin/user/enterList";
	}

	@RequestMapping(value = "/listDeptUser/{enterpriseId}/{deptId}", method = RequestMethod.POST)
	public ResponseEntity<?> listDeptUser(@PathVariable(value = "enterpriseId") Long enterpriseId, @PathVariable(value = "deptId") String deptId, PageRequest request, String token) throws IOException {
		super.checkToken(token);
		Page<EnterpriseUser> userList = enterpriseUserManager.getPagedEnterpriseUser(null, 0L, deptId, enterpriseId, request);

		String content = objToString(userList.getContent());

		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(value = "/getDeptInfo/{deptId}", method = RequestMethod.POST)
	public ResponseEntity<?> getDeptManager(@PathVariable(value = "deptId") Long deptId, String token) throws IOException {
		super.checkToken(token);

        Long enterpriseId = checkAdminAndGetId();

        DeptInfo info = new DeptInfo();
        info.setEnterpriseId(enterpriseId);
        info.setDepartmentId(deptId);

		EnterpriseSecurityPrivilege deptManager = enterpriseUserManager.getDeptManager(enterpriseId, deptId);
		if(deptManager!=null){
			EnterpriseUser user = enterpriseUserService.get(deptManager.getEnterpriseUserId(), deptManager.getEnterpriseId());
	        info.setManager(user);

		}
        EnterpriseUser owner = enterpriseUserManager.getArchiveOwner(enterpriseId, deptId);
        info.setArchiveOwner(owner);

        String appId = authAppService.getDefaultWebApp().getAuthAppId();
        EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByEnterpriseApp(enterpriseId, appId);
        DepartmentAccount account = departmentAccountService.getByDeptIdAndAccountId(deptId, enterpriseAccount.getAccountId());
        info.setDepartmentAccount(account);

        return new ResponseEntity<>(objToString(info), HttpStatus.OK);
	}

    @RequestMapping(value = "addDeptManager/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addDeptManager(@PathVariable(value = "authServerId") Long authServerId, @RequestParam(value = "deptId") long deptId, @RequestParam(value = "enterpriseUserId") long enterpriseUserId,String appId) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        enterpriseUserManager.addDeptManager(enterpriseId, deptId, enterpriseUserId,appId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

	@RequestMapping(value = "createDept/{id}", method = RequestMethod.GET)
	public String enterCreateDept(@PathVariable(value = "id") Long id, Model model,String token) {
		//super.checkToken(token);
		Long enterpriseId = checkAdminAndGetId();
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		Department dept = null;
		if (!StringUtils.isBlank(String.valueOf(id))) {
			dept = departmentManager.getDeptById(enterpriseId, id);
		}
		model.addAttribute("dept", dept);
		return "enterprise/admin/user/createDept";
	}
	
	@RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
	public ResponseEntity<?> addDept(HttpServletRequest request, String token)
			throws IOException {
		super.checkToken(token);
		Long enterpriseId = checkAdminAndGetId();
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		String name = request.getParameter("name");
		Department DepartmentInfo = new Department();
		DepartmentInfo.setParentId(Long.parseLong(request.getParameter("parentid")));
		DepartmentInfo.setName(name);
		DepartmentInfo.setEnterpriseId(enterpriseId);
		Long id = departmentManager.create(DepartmentInfo);
		return new ResponseEntity<Long>(id,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deptAddEnterpriseUser", method = RequestMethod.POST)
	public ResponseEntity<?> deptAddEnterpriseUser(HttpServletRequest request, String token,String appId) throws IOException {
		super.checkToken(token);
		Long enterpriseId = checkAdminAndGetId();
		ResponseEntity<?> result= new ResponseEntity<>(HttpStatus.OK);
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		String enterpriseUserIds = request.getParameter("enterpriseUserId");
		String deptId = request.getParameter("deptId");
		Department dept = null;
		
		if (!StringUtils.isBlank(deptId)) {
			dept = departmentManager.getDeptById(enterpriseId, Long.parseLong(deptId));
			if (dept == null) {
				return new ResponseEntity<String>("noDeptException", HttpStatus.BAD_REQUEST);
			}
		}
		try {
			if (enterpriseUserIds != null && enterpriseUserIds.trim().length() > 0) {
				String[] idAry = enterpriseUserIds.split(",");

				for (int i = 0; i < idAry.length; i++) {
					EnterpriseUserDept userDept=new EnterpriseUserDept(Long.parseLong(idAry[i]), enterpriseId, Long.parseLong(deptId));
					userDeptService.create(userDept);
				}
			}

		}catch(ConflictException e){
			 return new ResponseEntity<String>("ConflictException",HttpStatus.BAD_REQUEST);
		 } 
		 return result;
	}
	
	@RequestMapping(value = "/deptDeleteEnterpriseUser", method = RequestMethod.POST)
	public ResponseEntity<?> deptDeleteEnterpriseUser(HttpServletRequest request, String token,String appId) throws IOException {
		super.checkToken(token);
		Long enterpriseId = checkAdminAndGetId();
		ResponseEntity<?> result= new ResponseEntity<>(HttpStatus.OK);
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		String enterpriseUserIds = request.getParameter("enterpriseUserId");
		String deptId = request.getParameter("deptId");
		Department dept = null;
		
		if (!StringUtils.isBlank(deptId)) {
			dept = departmentManager.getDeptById(enterpriseId, Long.parseLong(deptId));
			if (dept == null) {
				return new ResponseEntity<String>("noDeptException", HttpStatus.BAD_REQUEST);
			}
		}
		try {
			if (enterpriseUserIds != null && enterpriseUserIds.trim().length() > 0) {
				String[] idAry = enterpriseUserIds.split(",");

				for (int i = 0; i < idAry.length; i++) {
					userDeptService.deleteByDepartmentIdAndEnterpriseUserId(enterpriseId, Long.parseLong(deptId), Long.parseLong(idAry[i]));
				}
			}

		}catch(ConflictException e){
			 return new ResponseEntity<String>("ConflictException",HttpStatus.BAD_REQUEST);
		 } 
		 return result;
	}
	
	
	@RequestMapping(value = "/deleteDepartment", method = RequestMethod.POST)
	public ResponseEntity<?> deleteDepartment(String departmentId, String token,String filter,Long authServerId,
			HttpServletRequest req, Model model)
			throws IOException {
		super.checkToken(token);
		String[] description = new String[] { getEnterpriseName(), getAuthServerName(authServerId), filter };
		long enterpriseId = checkAdminAndGetId();
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(enterpriseId);
		owner.setIp(IpUtils.getClientAddress(req));
		if (StringUtils.isBlank(departmentId)) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE_ERROR, description);
			logger.error("departmentId is null");
			throw new InvalidParamterException();
		}
		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE, description);
		Page<EnterpriseUserExtend> userPage = departmentManager.delete(Long.parseLong(departmentId),enterpriseId,authServerId);
		if(null != userPage){
			List<AccountAuthserver> list = accountAuthserverManager.listBindApp(enterpriseId, authServerId);
	        StringBuilder sb = new StringBuilder();
	        if (!CollectionUtils.isEmpty(list))
	        {
	            for (AccountAuthserver accountAuthserver : list)
	            {
	                sb.append(accountAuthserver.getAuthAppId() + ",");
	            }
	        }
	        String appIds = sb.toString();
	        if (StringUtils.isNotBlank(appIds))
	        {
	            appIds = appIds.substring(0, appIds.length() - 1);
	        }
	        String authAppIds = (HtmlUtils.htmlEscape(appIds));
	        EnterpriseUserExtend.htmlEscape(userPage.getContent());
	        Object[] arr = new Object[]{authAppIds, userPage};
	        return new ResponseEntity<Object[]>(arr, HttpStatus.BAD_REQUEST);
		}
			return new ResponseEntity<>(HttpStatus.OK);
	}

	@SuppressWarnings("unused")
	private String getAuthServerName(long authServerId) {
		try {
			AuthServer authServer = authServerManager.getAuthServer(authServerId);
			return authServer.getName();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public ResponseEntity<String> getEnterpriseDeptTree() {
		Long enterpriseId = checkAdminAndGetId();
		List<DeptNode> list = departmentManager.listDeptTreeByEnterpriseId(enterpriseId);
		String result = "";
		try {
			if(list!=null){
				result = objToString(list).replace("\"children\":null", "\"children\":\"\"");
			}
		} catch (Exception e) {
			logger.error("List enterpriseUser failed, EnterpriseId is" + enterpriseId);
			e.printStackTrace();
		}
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/enterDeptTreeManage/{authServerId}", method = RequestMethod.GET)
	public String enterDeptTreeManage(@PathVariable(value = "authServerId") Long authServerId, Model model) {
		model.addAttribute("currentTypeId", authServerId);

		Long enterpriseId = checkAdminAndGetId();
		// 是否开启组织架构
		boolean isDeptPrivilege = enterpriseManager.firstEnterCheckOrganizeOperPrivilege(enterpriseId);
		model.addAttribute("isDeptPrivilege", isDeptPrivilege);

		List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
		model.addAttribute("authServerList", authServerList);

		AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
		model.addAttribute("localTypeId", authServer.getId());

		Enterprise enterprise = enterpriseManager.getById(enterpriseId);
		// Entering for the first time, the default company name as the highest organization
		model.addAttribute("enterpriseName", enterprise.getName());

		String result = getEnterpriseDeptTree().getBody();
		model.addAttribute("deptTree", result);

		return "enterprise/admin/user/organizationManage";
	}
	
	@RequestMapping(value = "/chooseDepartment", method = RequestMethod.GET)
	public String chooseDepartment(String mode, Model model) {
		String result = getEnterpriseDeptTree().getBody();
		model.addAttribute("deptTree", result);
        model.addAttribute("mode", mode);

		return "enterprise/admin/user/chooseDepartment";
	}

	@RequestMapping(value = "/modifyDeptName/{id}", method = RequestMethod.POST)
	public ResponseEntity<String> modifyDeptName(@PathVariable(value = "id") Long id, Long parentid, String name, String domain, String token) {
		super.checkToken(token);
		Long enterpriseId = checkAdminAndGetId();
		enterpriseManager.checkOrganizeOperPrivilege(enterpriseId); 
		 Department dept = null;
		 if(!StringUtils.isBlank(String.valueOf(id))){
			 dept = departmentManager.getDeptById(enterpriseId, id);
			 if(!StringUtils.isBlank(name)){
				 dept.setName(name);
			 }
			 if(!StringUtils.isBlank(String.valueOf(parentid))){
				 dept.setParentId(parentid);
			 }
			 if(!StringUtils.isBlank(domain)){
				 dept.setDomain(domain);
			 }

			 departmentManager.update(dept);
		 }
		 return new ResponseEntity<String>("success", HttpStatus.OK);
	}

	/**
	 * Get department info by specified enterprise
	 * 
	 */
	@RequestMapping(value = "/listDeptTree", method = RequestMethod.GET)
	public ResponseEntity<String> listDepartmentTree() {
		String result = getEnterpriseDeptTree().getBody();
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteEnterpriseUserDept(long authServerId, String dn, String filter, String ids,
	        HttpServletRequest req, String token,Long departmentId) {
		super.checkToken(token);
		String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), ids, dn,
	            filter};
        long enterpriseId = checkAdminAndGetId();
        enterpriseManager.checkOrganizeOperPrivilege(enterpriseId);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        if (StringUtils.isBlank(ids))
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE_ERROR, description);
            logger.error("deleteEnterpriseUserDept ids is null");
            throw new InvalidParamterException();
        }
//        userDepartmentManager.deleteUserDepartment(enterpriseId, ids,departmentId);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE, description);
        return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	private <T> String objToString(T obj){
		objectMapper = new ObjectMapper();
		String result="";
		try {
			jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
			jsonGenerator.writeObject(obj);
			result = objectMapper.writeValueAsString(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
