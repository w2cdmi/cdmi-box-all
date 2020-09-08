package com.huawei.sharedrive.uam.accountuser.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccountExtend;
import com.huawei.sharedrive.uam.accountuser.manager.ListUserAccountManager;
import com.huawei.sharedrive.uam.accountuser.manager.UpdateUserAccountListManager;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.manager.UserManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/admin/accountuser")
public class UserAccountController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(UserAccountController.class);
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private ListUserAccountManager listUserAccountManager;

    @Autowired
    private UpdateUserAccountListManager updateUserAccountListManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private AccountAuthserverManager accountAuthserverManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @RequestMapping(value = "list/{appId}/{authServerId}", method = RequestMethod.GET)
    public String enter(@PathVariable(value = "appId") String appId,
        @PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        long enterpriseId = sessAdmin.getEnterpriseId();
        List<AuthServer> authServerList = authServerManager.getByEnterpriseId(enterpriseId);
        AuthServer authServer = authServerManager.enterpriseTypeCheck(enterpriseId,
            AuthServer.AUTH_TYPE_LOCAL);
        
        model.addAttribute("appId", HtmlUtils.htmlEscape(appId));
        model.addAttribute("localTypeId", authServer.getId());
        model.addAttribute("currentTypeId", authServerId);
        model.addAttribute("authServerList", authServerList);
        return "enterprise/admin/app/userList";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseEntity<Page<UserAccountExtend>> list(Integer size, Integer page, String appId, String dn,
        Long authServerId, String filter, Integer userStatus, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        PageRequest request = new PageRequest();
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        if (size <= 0)
        {
            size = 1;
        }
        if (page != null && page <= 0)
        {
            page = 1;
        }
        request.setSize(size);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        UserLdap tmpUserLdap = new UserLdap();
        tmpUserLdap.setSessionId(sessionId);
        tmpUserLdap.setDn(dn);
        tmpUserLdap.setAuthServerId(authServerId);
        Page<UserAccountExtend> userPage = listUserAccountManager.getPagedUserAccount(tmpUserLdap,
            appId,
            filter,
            userStatus,
            request);
        UserAccountExtend.htmlEscape(userPage.getContent());
        return new ResponseEntity<Page<UserAccountExtend>>(userPage, HttpStatus.OK);
    }
    
    @RequestMapping(value = "updateUserList/{appId}", method = RequestMethod.GET)
    public String enterUpdateUserList(@PathVariable("appId") String appId, Model model)
    {
        List<RestRegionInfo> regionList = userManager.getRegionInfo(appId);
        model.addAttribute("regionList", regionList);
        return "enterprise/admin/app/modifyUserList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked", "PMD.ExcessiveParameterList"})
    @RequestMapping(value = "updateUserList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateUserList(long authServerId, String appId,
        UpdateUserAccountList updateUserAccountList, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{
            getEnterpriseName(),
            getAuthServerName(authServerId),
            appId,
            updateUserAccountList.getSelLdapDn(),
            updateUserAccountList.getSelStatus(),
            updateUserAccountList.getSelFilter(),
            updateUserAccountList.getUserIds(),
            updateUserAccountList.getIsRegionId() + "," + updateUserAccountList.getRegionId(),
            updateUserAccountList.getIsSpaceQuota() + "," + updateUserAccountList.getSpaceQuota(),
            updateUserAccountList.getIsMaxVersions() + "," + updateUserAccountList.getMaxVersions(),
            updateUserAccountList.getIsUploadBandWidthInput() + ","
                + updateUserAccountList.getUploadBandWidth(),
            updateUserAccountList.getIsDownloadBandWidthInput() + ","
                + updateUserAccountList.getDownloadBandWidth(),
            updateUserAccountList.getIsTeamSpaceFlag() + "," + updateUserAccountList.getTeamSpaceFlag(),
            updateUserAccountList.getIsTeamSpaceMaxNum() + "," + updateUserAccountList.getTeamSpaceMaxNum()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        Set violations = validator.validate(updateUserAccountList);
        if (!violations.isEmpty())
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_APP_USER_INTO_UPDATE_ERROR, description);
            throw new ConstraintViolationException(violations);
        }
        long enterpriseId = checkAdminAndGetId();
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        try
        {
            updateUserAccountListManager.updateUserList(updateUserAccountList,
                sessionId,
                appId,
                authServerId,
                enterpriseId);
        }
        catch (InvalidParamterException e)
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_APP_USER_INTO_UPDATE_ERROR, description);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_APP_USER_INTO_UPDATE, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "changeStatus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> changeStatus(Long authServerId, int status, String dn, String filter,
        String ids, Integer selStatus, String appId, HttpServletRequest req, String token)
    {
        
        super.checkToken(token);
        long enterpriseId = checkAdminAndGetId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterpriseId);
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        AdminLogType adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_ENABLE_USER_VALIDATE.getTypeCode());
        if (status != Constants.STATUS_OF_ACCOUNT_ENABLE && status != Constants.STATUS_OF_ACCOUNT_DISABLE)
        {
            super.saveValidateLog(owner, adminlogType);
            throw new InvalidParamterException("invalid status");
        }
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), appId, dn,
            String.valueOf(selStatus), filter, ids, String.valueOf(status)};
        
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        
        adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_ENABLE_EMPLOYEE.getTypeCode());
        try
        {
            
            if (status == Constants.STATUS_OF_ACCOUNT_ENABLE)
            {
                adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_ENABLE_EMPLOYEE.getTypeCode());
            }
            else
            {
                adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_DISABLE_EMPLOYEE.getTypeCode());
            }
            UserLdap userLdap = new UserLdap();
            UserAccount userAccount = new UserAccount();
            userLdap.setAuthServerId(authServerId);
            userLdap.setSessionId(sessionId);
            userLdap.setDn(dn);
            userAccount.setAlias(ids);
            userAccount.setEnterpriseId(enterpriseId);
            userAccount.setStatus(status);
            updateUserAccountListManager.updateUserStatus(userLdap, userAccount, appId, filter, selStatus);
            adminLogManager.saveAdminLog(owner, adminlogType, description);
        }
        catch (RuntimeException e)
        {
            if (status == 0)
            {
                adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_ENABLE_EMPLOYEE_ERROR.getTypeCode());
            }
            if (status == 1)
            {
                adminlogType = AdminLogType.getAdminLogType(AdminLogType.KEY_DISABLE_EMPLOYEE_ERROR.getTypeCode());
            }
            adminLogManager.saveAdminLog(owner, adminlogType, description);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/exportUser/{authServerId}/{appId}/{userStatus}/{filter}/{dn}", method = RequestMethod.GET)
    public void exportUserInfo(HttpServletRequest request, HttpServletResponse response,
        @PathVariable("authServerId") Long authServerId, @PathVariable("appId") String appId,
        @PathVariable("userStatus") Integer userStatus, @PathVariable("filter") String filter,
        @PathVariable("dn") String dn) throws IOException
    {
        String[] description = null;
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setAppId(appId);
        try
        {
            long enterpriseId = checkAdminAndGetId();
            
            UserAccount userAccount = new UserAccount();
            userAccount.setEnterpriseId(enterpriseId);
            userAccount.setStatus(userStatus);
            userAccount.setDescription(filter);
            userAccount.setAccountId(authServerId);
            userAccount.setName(appId);
            userAccount.setAlias(dn);
            String ids = userAccountManager.exportUserList(request, response, userAccount);
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), appId, ids};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXPORT_EMPLOYEE, description);
        }
        catch (RuntimeException e)
        {
            logger.error(e.toString());
            description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), appId};
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EXPORT_EMPLOYEE_ERROR, description);
            response.getOutputStream().close();
        }
        
        finally
        {
            try
            {
                response.getOutputStream().close();
            }
            catch (IOException e)
            {
                logger.error(e.toString());
            }
        }
    }
    
    @RequestMapping(value = "setRole/{authServerId}/{appId}", method = RequestMethod.GET)
    public String setRole(@PathVariable("authServerId") Long authServerId,
        @PathVariable("appId") String authAppId, Model model)
    {
        long enterpriseId = checkAdminAndGetId();
        int accountId = accountAuthserverManager.getAccountId(authServerId, enterpriseId, authAppId);
        SecurityRole securityRole = new SecurityRole();
        securityRole.setAccountId(accountId);
        List<SecurityRole> list = securityRoleManager.getFilterdList(securityRole);
        model.addAttribute("SecurityRoles", list);
        return "enterprise/admin/app/roleSetter";
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "setRole", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setRole(long authServerId, String appId, String dn, String filter,
        Integer selStatus, String userIds, Integer roleId, Model model, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId), appId, dn,
            String.valueOf(selStatus), filter, userIds, String.valueOf(roleId)};
        long enterpriseId = checkAdminAndGetId();
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        try
        {
            UserLdap userLdap = new UserLdap();
            UserAccount userAccount = new UserAccount();
            userLdap.setAuthServerId(authServerId);
            userLdap.setSessionId(sessionId);
            userLdap.setDn(dn);
            userAccount.setEnterpriseId(enterpriseId);
            userAccount.setStatus(selStatus);
            userAccount.setRoleId(roleId);
            updateUserAccountListManager.updateRole(userLdap, userAccount, appId, userIds, filter);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SET_ROLE, description);
        }
        catch (RuntimeException e)
        {
            logger.error("set role fail");
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SET_ROLE_ERROR, description);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private String getAuthServerName(long authServerId)
    {
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        if (null == authServer)
        {
            return null;
        }
        return authServer.getName();
    }
}
