package com.huawei.sharedrive.uam.authorize.web;

import javax.servlet.http.HttpServletRequest;

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
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.util.FormValidateUtil;

@Controller
@RequestMapping(value = "/sys/authorize/user")
public class AuthorizeUserPwdController extends AbstractCommonController
{
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model)
    {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("account", adminService.get(admin.getId()));
        return "sys/authorizeManage/changeLoginName";
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modifyLoginName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modifyLoginName(Admin inputAdmin, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{inputAdmin.getLoginName()};
        String logId = systemLogManager.save(req,
            OperateType.ChangeAdminInfo,
            OperateDescription.ADMIN_CHANGE_USERNAME,
            null,
            description);
        
        Admin checkAdmin = adminService.getAdminByLoginName(inputAdmin.getLoginName());
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (checkAdmin != null)
        {
            if (checkAdmin.getId() == sessAdmin.getId())
            {
                return new ResponseEntity<String>("userNameNotChange", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<String>("repeatLoginName", HttpStatus.BAD_REQUEST);
        }
        if (!FormValidateUtil.isValidLoginName(inputAdmin.getLoginName()))
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        inputAdmin.setId(sessAdmin.getId());
        adminService.changeLoginName(inputAdmin);
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
