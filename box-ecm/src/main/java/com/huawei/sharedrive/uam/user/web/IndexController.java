package com.huawei.sharedrive.uam.user.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseManager;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.service.AdminService;
import pw.cdmi.core.utils.I18nUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@RequestMapping(value = "/")
public class IndexController
{
    @Autowired
    private AdminService adminService;
    @Autowired
	private EnterpriseManager enterpriseManager;

    @RequestMapping(method = RequestMethod.GET)
    public String enter(HttpServletRequest request, Model model)
    {
        Subject subject = SecurityUtils.getSubject();
        Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
        Admin localAdmin = adminService.get(sessAdmin.getId());
        model.addAttribute("checkOrganizeOperPrivilege",enterpriseManager.firstEnterCheckOrganizeOperPrivilege(sessAdmin.getEnterpriseId()));
        model.addAttribute("email", sessAdmin.getEmail());

        //根据资源文件获取企业名称
        Locale locale = request.getLocale();
        if(locale == null) {
            locale = Locale.CHINA;
        }
        String productName = I18nUtils.toI18n("main.product", locale);
        model.addAttribute("productName", productName);

        if (null == localAdmin)
        {
            return "common/initPwd";
        }
        if (localAdmin.getLastLoginTime() == null)
        {
            return "common/initPwd";
        }
        if (subject.hasRole(AdminRole.ENTERPRISE_MANAGER.toString()))
        {
            return "index";
        }
        return "login";
    }
}
