package pw.cdmi.box.uam.user.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.ExceptionUtil;
import pw.cdmi.box.uam.util.FormValidateUtil;
import pw.cdmi.box.uam.util.PasswordValidateUtil;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/account")
public class AccountController extends AbstractCommonController
{
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "enterChange", method = RequestMethod.GET)
    public String enter()
    {
        return "common/localUserChgPwd";
    }
    
    @RequestMapping(value = "initChangePwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> initChangePwd(Admin inputAdmin, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        String loginIP = IpUtils.getClientAddress(req);
        String[] description = new String[]{admin.getLoginName()};
        String logId = systemLogManager.saveFailLog(req,
            OperateType.ChangeInitPassword,
            OperateDescription.ADMN_CHANGE_DEFAULT_PWD,
            null,
            description);
        if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles())
            && admin.getLastLoginTime() == null)
        {
            inputAdmin.setId(admin.getId());
            inputAdmin.setLoginName(admin.getLoginName());
            try
            {
                adminService.changeAdminPwdByInitLogin(inputAdmin, req, loginIP);
            }
            catch (Exception e)
            {
                return new ResponseEntity<String>(
                    HtmlUtils.htmlEscape(ExceptionUtil.getExceptionClassName(e)), HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            throw new InvalidParamterException();
        }
        checkAccountPassword(inputAdmin);
        systemLogManager.updateSuccess(logId);
        SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void checkAccountPassword(Admin inputAdmin)
    {
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getPassword()))
        {
            throw new BadRquestException();
        }
        if (!PasswordValidateUtil.isValidPassword(inputAdmin.getOldPasswd()))
        {
            throw new BadRquestException();
        }
        if (StringUtils.equals(inputAdmin.getPassword(), inputAdmin.getOldPasswd()))
        {
            throw new BadRquestException();
        }
    }
    
    @RequestMapping(value = "changePwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> changePwd(Admin inputAdmin, HttpServletRequest req, String token)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        String[] description = new String[]{admin.getLoginName()};
        String logId = systemLogManager.saveFailLog(req,
            OperateType.ChangePassword,
            OperateDescription.ADMIN_CHANGE_PWD,
            null,
            description);
        
        super.checkToken(token);
        if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles()))
        {
            inputAdmin.setId(admin.getId());
            inputAdmin.setLoginName(admin.getLoginName());
            try
            {
                adminService.changeAdminPwd(inputAdmin, req);
            }
            catch (Exception e)
            {
                return new ResponseEntity<String>(
                    HtmlUtils.htmlEscape(ExceptionUtil.getExceptionClassName(e)), HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            throw new InvalidParamterException();
        }
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private boolean isRoleValid(Set<AdminRole> roleSet)
    {
        AdminRole[] roles = AdminRole.values();
        for (AdminRole role : roles)
        {
            if (roleSet.contains(role))
            {
                return true;
            }
        }
        return false;
    }
    
    @RequestMapping(value = "enteremail", method = RequestMethod.GET)
    public String enterSetEmail(Model model)
    {
        Admin cacheAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Admin admin = adminService.get(cacheAdmin.getId());
        if (admin != null)
        {
            model.addAttribute("email", admin.getEmail());
        }
        return "common/changeEmail";
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "setemail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> setEmail(String email, String password, HttpServletRequest req, String token)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        String[] description = new String[]{admin.getLoginName()};
        String logId = systemLogManager.saveFailLog(req,
            OperateType.ChangeAdminEmailAddr,
            OperateDescription.ADMIN_CHANGE_EMAIL,
            null,
            description);
        
        super.checkToken(token);
        
        if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles()))
        {
            if (!FormValidateUtil.isValidEmail(email))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            boolean invalidPassword = adminService.isCurrentPassword(password);
            if (!invalidPassword)
            {
                return new ResponseEntity<String>("OldPasswordErrorException", HttpStatus.BAD_REQUEST);
            }
            adminService.updateEmail(admin.getId(), email);
            admin.setEmail(email);
        }
        else
        {
            throw new InvalidParamterException();
        }
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
