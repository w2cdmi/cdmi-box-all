package com.huawei.sharedrive.uam.enterpriseuser.web;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserExtend;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ListEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.organization.service.DepartmentService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.MessageTemplate;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.service.MessageTemplateService;
import com.huawei.sharedrive.uam.user.service.UserNotifyService;
import org.apache.commons.io.IOUtils;
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
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@SuppressWarnings("PMD.ExcessiveParameterList")
@Controller
@RequestMapping(value = "/enterprise/admin/user")
public class EnterpriseUserController extends AbstractCommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(EnterpriseUserController.class);
    
    private static final String ALL_USER = "all";
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private ListEnterpriseUserManager listEnterpriseUserManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private EnterpriseManager enterpriseManager;

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private UserNotifyService userNotifyService;
    
    @Autowired
    private DepartmentService departmentService;
    

    @RequestMapping(value = "createUser/{authServerId}", method = RequestMethod.GET)
    public String enterCreateUser(@PathVariable(value = "authServerId") Long authServerId, Model model)
    {
    	long enterpriseId = checkAdminAndGetId();
    	boolean isOrganizeEnabled= enterpriseManager.checkOrganizeEnabled(enterpriseId);
    	model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
        model.addAttribute("authServerId", authServerId);
        model.addAttribute("nameMinLength", EnterpriseUser.NAME_MIN_LENGTH);
        return "enterprise/admin/user/createUser";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "createUser/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addEnterpriseUser(@PathVariable(value = "authServerId") Long authServerId,
        EnterpriseUser enterpriseUser, HttpServletRequest request, String token) throws IOException
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
            enterpriseUser.getName(), enterpriseUser.getAlias(), enterpriseUser.getEmail(),
            enterpriseUser.getMobile(), enterpriseUser.getDescription()};
        Long enterpriseId = checkAdminAndGetId();
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        Set violations = validator.validate(enterpriseUser);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        if (!violations.isEmpty())
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new ConstraintViolationException(violations);
        }
        if (enterpriseUser.getName().length() < EnterpriseUser.NAME_MIN_LENGTH)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new InvalidParamterException("invalid name length:" + enterpriseUser.getName());
        }
        if (null == authServer)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new InvalidParamterException("no such authServerId:" + authServerId);
        }
        if (!StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType()))
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new InvalidParamterException("wrong authServerType:" + authServer.getType());
        }
        enterpriseUser.setEnterpriseId(enterpriseId);
        enterpriseUser.setUserSource(authServerId);
        
        enterpriseUserManager.createLocal(enterpriseUser, true);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD, description);
        
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @RequestMapping(value = "createUserAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addUserAccount(String ids, String appIds, String dn, Long authServerId,
        String filter, HttpServletRequest req, String token)
    {
        
        super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        StringBuilder name = new StringBuilder();
        if (!StringUtils.isBlank(ids) && !ALL_USER.equalsIgnoreCase(ids))
        {
            String[] idArray = ids.split(",");
            EnterpriseUser enterpriseUser = null;
            for (String userId : idArray)
            {
                enterpriseUser = enterpriseUserService.get(Long.parseLong(userId), enterpriseId);
                if (null != enterpriseUser)
                {
                    name.append(enterpriseUser.getName() + " ,");
                }
            }
        }
        if (ALL_USER.equalsIgnoreCase(ids))
        {
            name.append(ALL_USER);
        }
        
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
            name.toString(), appIds, dn, filter};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appIds);
        if (StringUtils.isBlank(ids) || StringUtils.isBlank(appIds))
        {
            logger.error("userIds or appIds is null");
            throw new InvalidParamterException();
        }
        String[] appIdArray = appIds.split(",");
        String[] idArray = ids.split(",");
        if (ALL_USER.equalsIgnoreCase(ids))
        {
            HttpSession session = req.getSession();
            String sessionId = session.getId();
            idArray = enterpriseUserManager.getAllUserId(enterpriseId, authServerId, dn, filter, sessionId);
        }
        try
        {
            String appId;
            for (int i = 0; i < appIdArray.length; i++)
            {
                appId = appIdArray[i];
                for (int j = 0; j < idArray.length; j++)
                {
                    userAccountManager.create(Long.parseLong(idArray[j]), enterpriseId, appId,null);
                }
            }
        }
        catch (NumberFormatException e)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_OPEN_ACCOUNT_ERROR, description);
            throw new InvalidParamterException(e.getMessage());
        }
        catch (RuntimeException e)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_OPEN_ACCOUNT_ERROR, description);
            throw new InternalServerErrorException(e);
        }
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_OPEN_ACCOUNT, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "listEnterpriseAccount/{authServerId}", method = RequestMethod.GET)
    public String listEnterpriseAccount(@PathVariable(value = "authServerId") Long authServerId, Model model)
    
    {
        Long enterpriseId = checkAdminAndGetId();
        List<AccountAuthserver> list = accountAuthserverManager.listBindApp(enterpriseId, authServerId);
        model.addAttribute("AccountAuthserverList", list);
        return "enterprise/admin/user/openAccount";
    }
    //密码复杂度
    @RequestMapping(value = "enterpriseAccountPwdLevel", method = RequestMethod.GET)
    public String enterpriseAccountPwdLevel(Model model)
    
    {
        Long enterpriseId = checkAdminAndGetId();
        int pwdLevel = 1;
        if(StringUtils.isNotBlank(enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId))){
    		pwdLevel = Integer.parseInt(enterpriseAccountService.getPwdLevelByEnterpriseId(enterpriseId).trim());
    	}
        model.addAttribute("pwdLevel", pwdLevel);
        return "enterprise/admin/user/enterpriseAccountPwdLevel";
    }
    
   
    @RequestMapping(value = "setEnterpriseAccountPwdLevel", method = RequestMethod.POST)
    public ResponseEntity<?> setAccountPwdLevel(String pwdLevel, String token)
    
    {
    	super.checkToken(token);
        Long enterpriseId = checkAdminAndGetId();
        Integer pwd_Level = 1;
        if(StringUtils.isNotBlank(pwdLevel)){
        	pwd_Level = Integer.parseInt(pwdLevel);
        }
        enterpriseAccountService.modifyPwdLevelByEnterpriseId(enterpriseId,pwd_Level);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "employeeManage/{authServerId}", method = RequestMethod.GET)
    public String employeeManage(@PathVariable(value = "authServerId") Long authServerId, Model model,HttpServletRequest req)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
    	boolean isOrganizeEnabled= enterpriseManager.checkOrganizeEnabled(enterpriseId);
    	model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
        model.addAttribute("localTypeId", authServer.getId());
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("authServerList", authServerList);
        return "enterprise/admin/user/employeeManage";
    }
    
    @RequestMapping(value = "employeeManage", method = RequestMethod.POST)
    public ResponseEntity<Object[]> employeeManage(String dn, Long authServerId, String deptId,String filter, Integer page, @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
        String newHeadItem, boolean newFlag, @RequestParam(value = "type", defaultValue = "-1") long type, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        if (null == page || page < 1)
        {
            page = 1;
        }
        PageRequest request = new PageRequest();
        request.setSize(pageSize);
        request.setPage(page);
        if (StringUtils.isNotBlank(newHeadItem))
        {
            Order order = new Order();
            order.setField(newHeadItem);
            order.setDesc(newFlag);
            request.setOrder(order);
        }
        
        if(StringUtils.isNotBlank(deptId)){
        	if("all".equals(deptId)){
        		deptId = null;
        	}
        }else{
    		deptId = null;
        }
       
        Page<EnterpriseUserExtend> userPage = listEnterpriseUserManager.getPagedEnterpriseUser(sessionId,
            dn,
            authServerId,
            deptId,
            enterpriseId,
            filter,
            request,
            type,
            null);
        
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
        return new ResponseEntity<Object[]>(arr, HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseEntity<?> delete(long authServerId, String dn, String filter, String ids,
        HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), ids, dn,
            filter};
        long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        if (StringUtils.isBlank(ids))
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE_ERROR, description);
            logger.error("ids is null");
            throw new InvalidParamterException();
        }
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        enterpriseUserManager.delete(enterpriseId, authServerId, dn, filter, ids, sessionId);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_DELETE_EMPLOYEE, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "exportEmployees/{authServerId}/{id}/{dn}/{filter}", method = RequestMethod.GET)
    public void exportEmployees(HttpServletRequest request, HttpServletResponse response,
        @PathVariable("authServerId") Long authServerId, @PathVariable("id") String id,
        @PathVariable("dn") String dn, @PathVariable("filter") String filter) throws IOException
    {
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        String[] description;
        if (null != id)
        {
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId),
                String.valueOf(id)};
        }
        else
        {
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId)};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXPORT_EMPLOYEE_ERROR, description);
            return;
        }
        try
        {
            long enterpriseId = checkAdminAndGetId();
            EnterpriseUser enterpriseUser = new EnterpriseUser();
            enterpriseUser.setEnterpriseId(enterpriseId);
            enterpriseUser.setUserSource(authServerId);
            enterpriseUser.setDescription(filter);
            enterpriseUser.setUserDn(dn);
            
            enterpriseUserManager.exportEmployeeList(request, response, enterpriseUser, id);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXPORT_EMPLOYEE, description);
        }
        catch (RuntimeException e)
        {
            logger.error(e.toString());
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXPORT_EMPLOYEE_ERROR, description);
            response.getOutputStream().close();
        }
        finally
        {
            IOUtils.closeQuietly(response.getOutputStream());
        }
    }
    
    @RequestMapping(value = "employeeInfoTemplateFile", method = RequestMethod.GET)
    public void employeeInfoTemplateFile(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
    	 long enterpriseId = checkAdminAndGetId();
        String[] description = new String[]{getEnterpriseName()};
        String logId = systemLogManager.save(request,
            OperateType.EnterpriseEmployees,
            OperateDescription.ENTERPRISE_EMPLOYEES_EXPORT_TEMPLATE,
            null,
            description);
        
        try
        {
            enterpriseUserManager.downloadEmployeeInfoTemplateFile(request, response,enterpriseId);
            systemLogManager.updateSuccess(logId);
        }
        catch (IOException e)
        {
            logger.error(e.toString());
            response.getOutputStream().close();
        }
        finally
        {
            IOUtils.closeQuietly(response.getOutputStream());
        }
    }
    
    private String getAuthServerName(long authServerId)
    {
        try
        {
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            return authServer.getName();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    @RequestMapping(value = "validLoginName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validLoginName(EnterpriseUser enterpriseUser, String token)
        throws BadRequestException, UnsupportedEncodingException
    {
        super.checkToken(token);
        if (StringUtils.isNotEmpty(enterpriseUser.getName()) && enterpriseUser.getName().trim().length() != 0)
        {
            byte[] checkKey = enterpriseUser.getName().trim().getBytes("utf-8");
            if (checkKey.length > User.LOGINNAME_LENGTH)
            {
                throw new BadRequestException();
            }
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "validPhone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validPhone(EnterpriseUser enterpriseUser, String token)
        throws BadRequestException, UnsupportedEncodingException
    {
        super.checkToken(token);
        if (StringUtils.isNotEmpty(enterpriseUser.getMobile()) && enterpriseUser.getMobile().trim().length() != 0)
        {
            byte[] checkKey = enterpriseUser.getMobile().trim().getBytes("utf-8");
            if (checkKey.length > User.LOGINNAME_LENGTH)
            {
                throw new BadRequestException();
            }
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
	@RequestMapping(value = "changeStatus", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> changeEnterpriseUserStatus(long authServerId, String dn, String filter, String ids,
			String token, String status, HttpServletRequest req) {
		super.checkToken(token);
		long enterpriseId = checkAdminAndGetId();
		LogOwner owner = new LogOwner();
		owner.setEnterpriseId(enterpriseId);
		owner.setIp(IpUtils.getClientAddress(req));
		String[] description = new String[] { getEnterpriseName(), getAuthServerName(authServerId), ids, dn, filter };

		if (StringUtils.isBlank(ids)) {
			adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CHANGE_EMPLOYEE_STATUS_ERROR, description);
			logger.error("ids is null");
			throw new InvalidParamterException();
		}
		
		String[] idArray = ids.split(",");
		if (ALL_USER.equalsIgnoreCase(ids)) {
			HttpSession session = req.getSession();
			String sessionId = session.getId();
			idArray = enterpriseUserManager.getAllUserId(enterpriseId, authServerId, dn, filter, sessionId);
		}

		List<Long> userIdList = new ArrayList<Long>(idArray.length);
		for (String userId : idArray) {
			userIdList.add(Long.valueOf(userId));
		}
		enterpriseUserManager.updateEnterpriseUserStatus(userIdList, EnterpriseUserStatus.getEnterpriseUserStatus(status),
				enterpriseId);	
		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_CHANGE_EMPLOYEE_STATUS, description);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
    
	@RequestMapping(value = "showMigrationDataUI/{authServerId}/{userId}", method = RequestMethod.GET)
	public String showMigrationDataUI(@PathVariable(value = "authServerId") Long authServerId,
			@PathVariable(value = "userId") Long userId, @RequestParam String departureUserName,
			@RequestParam String appIds, Model model, HttpServletRequest req) {
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		long enterpriseId = sessAdmin.getEnterpriseId();
		List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
		AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId,
		      AuthServer.AUTH_TYPE_LOCAL);
		model.addAttribute("departureUserId", userId);
		model.addAttribute("departureUserName", departureUserName);
		model.addAttribute("appIds", appIds);
		model.addAttribute("localTypeId", authServer.getId());
	    model.addAttribute("currentTypeId", authServerId);
	    model.addAttribute("authServerList", authServerList);

		HttpSession session = req.getSession();
		session.setAttribute("migrateToken", getFixLenthString(10));

		return "enterprise/admin/user/migrationDataUI";
	}
    
    @RequestMapping(value = "migrateData/{authServerId}/{migrateType}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> migrationData(@PathVariable(value = "authServerId") String authServerId,
        @PathVariable(value = "migrateType") int migrateType, String token,
        @RequestParam String appIds, @RequestParam String departureUserName,
        @RequestParam(defaultValue = "0") Long departureUserId, HttpServletRequest request ) {

        super.checkToken(token);
        if (checkSimpleMigrateToken(request)){
        	return new ResponseEntity<String>(HttpStatus.TOO_MANY_REQUESTS);
        }
        
        long enterpriseId = checkAdminAndGetId();
        if (enterpriseUserManager.isMigratedForUser(enterpriseId, departureUserId)){
        	return new ResponseEntity<String>(HttpStatus.MOVED_PERMANENTLY);
        }
        
        DataMigrationRequestDto dataMigration = new DataMigrationRequestDto();
        dataMigration.setEnterpriseId(enterpriseId);
        dataMigration.setAuthServerId(authServerId);
        dataMigration.setAppIds(appIds);
        dataMigration.setDepartureUserName(departureUserName);
        dataMigration.setMigrationType(migrateType);
        dataMigration.setDepartureUserId(departureUserId);
        if (migrateType == DataMigrationRequestDto.CONST_MIGRATION_TO_NEW_USER){
            dataMigration.setName(request.getParameter("name"));
            dataMigration.setAlias(request.getParameter("alias"));
            dataMigration.setEmail(request.getParameter("email"));
            dataMigration.setMobile(request.getParameter("mobile"));
            dataMigration.setDescription(request.getParameter("description"));
        } else {
            dataMigration.setRecipientUserName(request.getParameter("recipientUserName"));
            dataMigration.setRecipientUserId(Long.valueOf(request.getParameter("recipientUserId")));
        }

        String[] appIdArr = dataMigration.getAppIds().split(",");
        List<String> appIdList = Arrays.asList(appIdArr);
        enterpriseUserManager.migrateData(dataMigration, dataMigration.getEnterpriseId(), appIdList);

        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(request));
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(Long.valueOf(authServerId)), dataMigration.getMigrationType() + "", 
                dataMigration.getDepartureUserId() + "", dataMigration.getRecipientUserId() + ""};
        
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_MIGRATE_DATA, description);
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 校验简单防重复提交操作
     * @param request
     * @return
     */
	private boolean checkSimpleMigrateToken(HttpServletRequest request) {
		boolean isRepeatSubmit = false;
		
		String requMigrateToken = request.getParameter("migrateToken");
		HttpSession session = request.getSession();
		Object sessionToken = session.getAttribute("migrateToken");
		
		if (null == requMigrateToken || null == sessionToken ||
				!StringUtils.equalsIgnoreCase(requMigrateToken, String.valueOf(sessionToken))){
			isRepeatSubmit = true;
		}
		if (null != sessionToken ){
			session.removeAttribute("migrateToken");
		}

		return isRepeatSubmit;
	}

	private  String getFixLenthString(int strLength) {  
        Random random = new Random();  
        double pross = (1 + random.nextDouble()) * Math.pow(10, strLength);  
        String fixLenthString = String.valueOf(pross);  
        
        return fixLenthString.substring(1, strLength + 1);  
    }  
	
	@RequestMapping(value = "changeUserDeptInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateUserDeptInfo(String ids, String deptId, String appIds, String dn, long authServerId, String filter, HttpServletRequest req, String token){
        super.checkToken(token);
        long enterpriseId = checkAdminAndGetId();
        HttpSession session = req.getSession();
        String sessionId= session.getId();
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        if(enterpriseManager.checkOrganizeEnabled(enterpriseId)){
        	if (deptId ==null || deptId.isEmpty()){
        		String[] description={"Department:"+ deptId};
        		adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
        		throw new InvalidParamterException("Department is incorrect");
        	}
        	enterpriseUserManager.updateDepartmentInfo(enterpriseId, authServerId, Long.parseLong(deptId), dn, filter, ids, sessionId,authServer.getAppId());
        }
        if (authServer==null){
        	String[] description={"authServer:"};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new NoSuchAuthServerException("The authServer is empty!");
        }else if(!"LocalAuth".equals(authServer.getType())){
        	String[] description={"Department:"+ deptId};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new AuthFailedException("No Authentication for current operation!");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "infoSecurityManage/{authServerId}", method = RequestMethod.GET)
    public String infoSecurityManage(@PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        model.addAttribute("currentTypeId", authServerId);

        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
        model.addAttribute("localTypeId", authServer.getId());

        List<EnterpriseUser> securityManagerlist = enterpriseUserManager.listInfoSecurityManager(enterpriseId);
        
        model.addAttribute("securityManagerlist", securityManagerlist);

        return "enterprise/admin/user/infoSecurityManage";
    }

    @RequestMapping(value = "setInfoSecurityManager/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setInfoSecurityManager(@PathVariable(value = "authServerId") Long authServerId, @RequestParam(value = "enterpriseUserId") long enterpriseUserId,
    		@RequestParam(value = "deptId", defaultValue = "0") long deptId)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        enterpriseUserManager.setInfoSecurityManager(enterpriseId, enterpriseUserId,deptId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "archiveOwnerManage/{authServerId}", method = RequestMethod.GET)
    public String archiveOwnerManage(@PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
        boolean isOrganizeEnabled= enterpriseManager.checkOrganizeEnabled(enterpriseId);
        model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
        model.addAttribute("localTypeId", authServer.getId());
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("authServerList", authServerList);

        return "enterprise/admin/user/archiveOwnerManage";
    }

    @RequestMapping(value = "listSecurityPrivilege", method = RequestMethod.POST)
    public ResponseEntity<Page<EnterpriseUserExtend>> listSecurityPrivilege(String authServerId, String deptId, String filter, Integer page, 
    		@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,String newHeadItem, boolean newFlag,byte roleType, String token) {
        super.checkToken(token);
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();

        if (null == page || page < 1) {
            page = 1;
        }

        PageRequest request = new PageRequest();
        request.setSize(pageSize);
        request.setPage(page);
        if (StringUtils.isNotBlank(newHeadItem)) {
            Order order = new Order();
            order.setField(newHeadItem);
            order.setDesc(newFlag);
            request.setOrder(order);
        }

        Long departmentId = null;
        if (StringUtils.isNotBlank(deptId) && !deptId.equals("all")) {
            departmentId = new Long(deptId);
        }

        Page<EnterpriseUser> userPage = enterpriseUserManager.getPagedSecurityPrivilege(enterpriseId, departmentId, authServerId, filter, request,roleType);
        List<EnterpriseUserExtend> content=new ArrayList<>();
        for(int i=0;i<userPage.getContent().size();i++){
        	EnterpriseUserExtend enterpriseUserExtend = EnterpriseUserExtend.copyEnterpriseUserPro(userPage.getContent().get(i));
            List<Department> departmentList = departmentService.getByEnterpriseUserId(enterpriseId, userPage.getContent().get(i).getId());
            String deptsName="";
            for(int j=0;j<departmentList.size();j++){
            	if(deptsName.equals("")){
            		deptsName=departmentList.get(j).getName();
            	}else{
            		deptsName=deptsName+","+departmentList.get(j).getName();
            	}
            }
            enterpriseUserExtend.setDepartmentName(deptsName);
            content.add(enterpriseUserExtend);
        }
        PageRequest pageRequest=new PageRequest();
        pageRequest.setPage(page);
        pageRequest.setSize(pageSize);
        Page<EnterpriseUserExtend> userPageExtend=new PageImpl<>(content, pageRequest, userPage.getTotalElements());
        
        return new ResponseEntity<>(userPageExtend, HttpStatus.OK);
    }

    @RequestMapping(value = "addArchiveOwner/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addArchiveOwner(@PathVariable(value = "authServerId") Long authServerId, @RequestParam(value = "deptId") long deptId,
    		@RequestParam(value = "enterpriseUserId") long enterpriseUserId,String appId) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();

//        enterpriseUserManager.addArchiveOwner(enterpriseId, deptId, enterpriseUserId,appId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deletePrivilegeOwner/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deletePrivilegeOwner(@PathVariable(value = "authServerId") Long authServerId,byte roleType, @RequestParam(value = "enterpriseUserIds") String enterpriseUserIds) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        String[] ids=enterpriseUserIds.split(",");
        for(int i=0;i<ids.length;i++){
        	enterpriseUserManager.deletePrivilegeOwner(sessAdmin.getEnterpriseId(),Long.parseLong(ids[i]),roleType);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "getEnterPriseUserName/{cloudUserId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getEnterPriseUserName(@PathVariable(value = "cloudUserId") long cloudUserId,String appId) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        EnterpriseAccount enterpriseAccount =enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId);
        UserAccount userAccount = userAccountManager.getUserByCloudUserId(enterpriseAccount.getAccountId(), cloudUserId);
        EnterpriseUser enterpriseUser=enterpriseUserService.get(userAccount.getUserId(), enterpriseId);
        return new ResponseEntity<>(enterpriseUser.getName(),HttpStatus.OK);
    }


    @RequestMapping(value = "setSecurityClassification/{authServerId}", method = RequestMethod.GET)
    public String setSecurityClassification(@PathVariable(value = "authServerId") Long authServerId, String ids, Model model) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
        boolean isOrganizeEnabled = enterpriseManager.checkOrganizeEnabled(enterpriseId);
        model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
        model.addAttribute("localTypeId", authServer.getId());
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("authServerList", authServerList);

        if(!StringUtils.isBlank(ids)) {
            EnterpriseUser user = enterpriseUserService.get(Long.parseLong(ids), enterpriseId);
            model.addAttribute("user", user);
        }

        return "enterprise/admin/user/setSecurityClassification";
    }

    @RequestMapping(value = "setSecurityClassification/{authServerId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setSecurityClassification(@PathVariable(value = "authServerId") Long authServerId, @RequestParam(value = "enterpriseUserId") long enterpriseUserId, byte staffSecretLevel) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();

        EnterpriseUser user = enterpriseUserService.get(enterpriseUserId, enterpriseId);
        user.setStaffSecretLevel(staffSecretLevel);
        enterpriseUserService.updateEnterpriseUser(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "chooseEnterpriseUser/{authServerId}", method = RequestMethod.GET)
    public String chooseEnterpriseUser(@PathVariable(value = "authServerId") Long authServerId,
    		@RequestParam(value = "dataId", defaultValue = "id") String dataId,
    		@RequestParam(value = "rootDeptId", defaultValue = "-1") long rootDeptId,
    		String mode, Model model) {
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId, AuthServer.AUTH_TYPE_LOCAL);
        boolean isOrganizeEnabled = enterpriseManager.checkOrganizeEnabled(enterpriseId);
        model.addAttribute("isOrganizeEnabled", isOrganizeEnabled);
        model.addAttribute("localTypeId", authServer.getId());
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("dataId", dataId);
        model.addAttribute("rootDeptId", rootDeptId);
        model.addAttribute("authServerList", authServerList);
        model.addAttribute("mode", mode);

        return "enterprise/admin/user/chooseEnterpriseUser";
    }

    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    public ResponseEntity<String> resetPassword(@RequestParam(value = "token") String token, @RequestParam(value = "ids")String ids) {
        super.checkToken(token);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = admin.getEnterpriseId();

        if(StringUtils.isBlank(ids)) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        }

        for(String id : ids.split(",")) {
            long userId = Long.parseLong(id);
            EnterpriseUser user = enterpriseUserService.resetPassword(enterpriseId, userId);

            //新密码
            if(user != null) {
                //通过消息模板来构造消息
                MessageTemplate template = messageTemplateService.getById("resetPassword");
                if(template != null) {
                    String message = template.getContent().replaceAll("(\\$\\{username})", user.getName());
                    message = message.replaceAll("(\\$\\{password})", user.getPassword());

                    userNotifyService.sendMessage(enterpriseId, userId, message);
                } else {
                    logger.warn("No message template found: id=resetPassword");
                }
            }
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
