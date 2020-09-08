package pw.cdmi.box.uam.user.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.user.domain.AdminRole;
import pw.cdmi.box.uam.user.service.AdminService;

@Controller
@RequestMapping(value = "/")
public class IndexController
{
    @Autowired
    private AdminService adminService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String enter()
    {
        Subject subject = SecurityUtils.getSubject();
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Admin localAdmin = adminService.get(sessAdmin.getId());
        if (null == localAdmin)
        {
            return "common/initChgPwd";
        }
        if (localAdmin.getLastLoginTime() == null)
        {
            return "common/initChgPwd";
        }
        if (subject.hasRole(AdminRole.ADMIN_MANAGER.toString()))
        {
            return "redirect:/sys/authorize/role";
        }
        if (subject.hasRole(AdminRole.APP_MANAGER.toString()))
        {
            return "redirect:/app/appmanage/authapp";
        }
        if (subject.hasRole(AdminRole.ENTERPRISE_BUSINESS_MANAGER.toString()))
        {
            return "redirect:/enterprise/manager";
        }
        if (subject.hasRole(AdminRole.ENTERPRISE_MANAGER.toString()))
        {
            return "redirect:/enterprise/admin/enterpriseManage";
        }
        if (subject.hasRole(AdminRole.JOB_MANAGER.toString()))
        {
            return "redirect:/job";
        }
        if (subject.hasRole(AdminRole.ANNOUNCEMENT_MANAGER.toString()))
        {
            return "redirect:/announcement";
        }
        if (subject.hasRole(AdminRole.SYSTEM_CONFIG.toString()))
        {
            return "redirect:/sys/sysconfig/syslog";
        }
        if (subject.hasRole(AdminRole.STATISTICS_MANAGER.toString()))
        {
            return "redirect:/statistics";
        }
        if (subject.hasRole(AdminRole.FEEDBACK_MANAGER.toString()))
        {
        	return "redirect:/feedback/uam/manage";
        }
        return "login";
    }
}
