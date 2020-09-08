package com.huawei.sharedrive.uam.user.web;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.ExceptionUtil;
import com.huawei.sharedrive.uam.util.FormValidateUtil;

import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/account")
@SuppressWarnings({"unchecked", "rawtypes"})
public class AccountController extends AbstractCommonController
{
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @RequestMapping(value = "initPwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> initPwd(Admin inputAdmin, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        LogOwner owner = new LogOwner();
        String loginIP = IpUtils.getClientAddress(req);
        owner.setIp(loginIP);
        owner.setLoginName(admin.getLoginName());
        owner.setEnterpriseId(checkAdminAndGetId());
        String[] description = new String[]{admin.getLoginName(), admin.getEmail()};
        if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles())
            && admin.getLastLoginTime() == null)
        {
            inputAdmin.setId(admin.getId());
            inputAdmin.setLoginName(admin.getLoginName());
            try
            {
            	inputAdmin.setEnterpriseId(admin.getEnterpriseId());
                adminService.changeAdminPwdByInitLogin(inputAdmin, req, loginIP);
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INIT_CHANGE_PWD, description);
            }
            catch (Exception e)
            {
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INIT_CHANGE_PWD_ERROR, description);
                return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e),
                    HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_INIT_CHANGE_PWD_ERROR, description);
            throw new ConstraintViolationException(new HashSet(1));
        }
        SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "changePwd", method = RequestMethod.GET)
    public String initChangePwd(Model model)
    {
        return "common/localUserChgPwd";
    }

    @RequestMapping(value = "changePwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> changePwd(Admin inputAdmin, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        String[] description = new String[]{admin.getLoginName()};
        LogOwner owner = new LogOwner();
        owner.setIp(IpUtils.getClientAddress(req));
        owner.setEnterpriseId(admin.getEnterpriseId());
        owner.setLoginName(admin.getLoginName());
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
                adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_PSWD_ERROR, description);
                return new ResponseEntity<String>(ExceptionUtil.getExceptionClassName(e),
                    HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDATE_PSWD_ERROR, description);
            throw new ConstraintViolationException(new HashSet(1));
        }
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_UPDAYE_PSWD, description);
        return new ResponseEntity(HttpStatus.OK);
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
        Admin localAdmin = null;
        if (admin != null)
        {
            localAdmin = admin;
            model.addAttribute("email", localAdmin.getEmail());
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
    public ResponseEntity<?> setEmail(String email, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        
        String[] description = new String[]{admin.getLoginName()};
        String logId = systemLogManager.save(req,
            OperateType.ChangeAdminEmailAddr,
            OperateDescription.ADMIN_CHANGE_EMAIL,
            null,
            description);
        
        if (admin.getDomainType() == Constants.DOMAIN_TYPE_LOCAL && isRoleValid(admin.getRoles()))
        {
            if (!FormValidateUtil.isValidEmail(email))
            {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            adminService.updateEmail(admin.getId(), email);
            admin.setEmail(email);
        }
        else
        {
            throw new ConstraintViolationException(new HashSet(1));
        }
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity(HttpStatus.OK);
    }
    
}
