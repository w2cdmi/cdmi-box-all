package pw.cdmi.box.uam.authorize.web;

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

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.service.AdminService;
import pw.cdmi.box.uam.util.FormValidateUtil;

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
        return "sys/authorizeManage/changeName";
    }
    
    /**
     * 
     * @param admin
     * @return
     */
    @RequestMapping(value = "modifyName", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> modifyName(Admin inputAdmin, HttpServletRequest req, String token)
    {
        super.checkToken(token);
        if (!FormValidateUtil.isValidName(inputAdmin.getName()))
        {
            saveValidateLog(req, OperateType.ChangeAdminInfo);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        if (inputAdmin.getName().equals(sessAdmin.getName()))
        {
            saveValidateLog(req, OperateType.ChangeAdminInfo);
            return new ResponseEntity<String>("userNameNotChange", HttpStatus.BAD_REQUEST);
        }
        
        String[] description = new String[]{inputAdmin.getName()};
        String logId = systemLogManager.saveFailLog(req,
            OperateType.ChangeAdminInfo,
            OperateDescription.ADMIN_CHANGE_USERNAME,
            null,
            description);
        
        inputAdmin.setId(sessAdmin.getId());
        adminService.changeName(inputAdmin);
        systemLogManager.updateSuccess(logId);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
}
