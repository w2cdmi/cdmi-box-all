package com.huawei.sharedrive.uam.enterprise.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterprise.manager.SecurityRoleManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/enterprise/security")
public class SecurityController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);
    
    @Autowired
    private SecurityRoleManager securityRoleManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @RequestMapping(value = "/{appId}/", method = RequestMethod.GET)
    public String enter(Model model, @PathVariable(value = "appId") String appId)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        return "enterprise/security/securityMain";
    }
    
    @RequestMapping(value = "list/{appId}/", method = RequestMethod.GET)
    public String enterSecurityRole(Model model, @PathVariable(value = "appId") String appId)
    {
        SecurityRole sr = new SecurityRole();
        sr.setAccountId(getAccoutId(appId));
        List<SecurityRole> list = securityRoleManager.getFilterdList(sr);
        model.addAttribute("safeRoleList", list);
        
        model.addAttribute("appId", appId);
        return "enterprise/security/securityRoleList";
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "list/{appId}/", method = {RequestMethod.POST})
    public ResponseEntity list(@PathVariable(value = "appId") String appId,
        @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
        SecurityRole securityRole, String token)
    {
        super.checkToken(token);
        PageRequest request = new PageRequest(pageNumber, Constants.DEFAULT_PAGE_SIZE);
        securityRole.setAccountId(getAccoutId(appId));
        if (securityRole.getId() != null && securityRole.getId() == -1)
        {
            securityRole.setId(null);
        }
        Page<SecurityRole> securityRolePage = securityRoleManager.getFilterd(securityRole, request);
        
        return new ResponseEntity(securityRolePage, HttpStatus.OK);
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "createSecurityRole/{appId}/", method = RequestMethod.GET)
    public String enterCreateLocal(Model model, @PathVariable(value = "appId") String appId)
    {
        model.addAttribute("appId", appId);
        return "enterprise/security/createSecurityRole";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(value = "modifySecurityRole/{appId}/", method = RequestMethod.GET)
    public String enterModify(long id, Model model, @PathVariable(value = "appId") String appId)
    {
        PageRequest request = new PageRequest();
        request.setSize(Constants.DEFAULT_PAGE_SIZE);
        SecurityRole admin = securityRoleManager.getById(id);
        if (null != admin)
        {
            model.addAttribute("securityRole", admin);
        }
        else
        {
            logger.error("the modified security role does not exists.");
        }
        return "enterprise/security/modifySecurityRole";
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "modifySecurityRole/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modify(SecurityRole securityRole,
        @PathVariable(value = "appId") String appId, HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, securityRole.getRoleName(),
            securityRole.getRoleDesc()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        @SuppressWarnings("rawtypes")
        Set violations = validator.validate(securityRole);
        if (!violations.isEmpty())
        {
            adminLogManager.saveAdminLog(owner,
                AdminLogType.KEY_SECURITY_CONTROL_ROLE_UPDATE_ERROR,
                description);
            throw new ConstraintViolationException(violations);
        }
        securityRole.setAccountId(getAccoutId(appId));
        securityRoleManager.modify(securityRole);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_CONTROL_ROLE_UPDATE, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    /**
     * 
     * @param admin
     * @return
     * @throws IOException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping(value = "createSecurityRole/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createLocal(SecurityRole securityRole,
        @PathVariable(value = "appId") String appId, HttpServletRequest req, String token) throws IOException
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), appId, securityRole.getRoleName(),
            securityRole.getRoleDesc()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        
        Set violations = validator.validate(securityRole);
        if (!violations.isEmpty())
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_CONTROL_ROLE_ADD_ERROR, description);
            throw new ConstraintViolationException(violations);
        }
        securityRole.setAccountId(getAccoutId(appId));
        securityRoleManager.create(securityRole);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_CONTROL_ROLE_ADD, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteSecurityRole/{appId}/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteSecurityRole(@PathVariable(value = "appId") String appId, long id,
        HttpServletRequest req, String token)
    {
        super.checkToken(token);
        SecurityRole dbSecurityRole = securityRoleManager.getById(id);
        String name = null;
        if (null != dbSecurityRole)
        {
            name = dbSecurityRole.getRoleName();
        }
        String[] description = new String[]{getEnterpriseName(), appId, name,
            dbSecurityRole == null ? "" : dbSecurityRole.getRoleDesc()};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setAppId(appId);
        SecurityRole securityRole = new SecurityRole();
        securityRole.setAccountId(getAccoutId(appId));
        securityRole.setId(id);
        securityRoleManager.delete(securityRole);
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_SECURITY_CONTROL_ROLE_DELETE, description);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
