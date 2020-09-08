/**
 * 
 */
package com.huawei.sharedrive.isystem.user.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.shiro.ShiroConstants;

/**
 * @author s00108907
 * 
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {
	@Autowired
	private AdminService adminService;

	@Value("${copyplolicy.config.ishow}")
	protected String showCopyPlocy;

	@Value("${isystem.one.enterprise}")
	protected String oneEnterprise;

	/**
	 * 打开首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String enter(Model model) {
		Subject subject = SecurityUtils.getSubject();
		Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
		if (null == sessAdmin) {
			return "redirect:" + ShiroConstants.LOGIN_PAGE;
		} else if (!isAuthorized(subject)) {
			subject.getSession().setAttribute("unAuthorized", "true");
			return ShiroConstants.LOGIN_PAGE;
		} else {
			model.addAttribute("showCopyPlocy", showCopyPlocy);
			String redirectPah = initLogIn(sessAdmin, model);
			return redirectPah == null ? ShiroConstants.INDEX_PAGE : redirectPah;
		}
	}

	private boolean isAuthorized(Subject subject) {
		boolean bool = false;

		for (AdminRole adminRole : AdminRole.values()) {
			if (subject.hasRole(adminRole.toString())) {
				bool = true;
				break;
			}
		}
		return bool;
	}

	private String initLogIn(Admin sessAdmin, Model model) {
		Admin localAdmin = adminService.get(sessAdmin.getId());
		if (oneEnterprise.equals("0")) {
			if (localAdmin.getLastLoginTime() == null && localAdmin.getRoleNames().contains("ADMIN_MANAGER")) {
				SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", false);
				return "anon/configIndex";
			} else {
				if (localAdmin.getLastLoginTime() == null
						|| ((localAdmin.getEmail() == null || localAdmin.getEmail().length() == 0)
								&& localAdmin.getStatus() == 1)) {
					Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
					model.addAttribute("email", admin.getEmail());
					return "common/initChgPwd";
				} else if ((localAdmin.getEmail() == null || localAdmin.getEmail().length() == 0)
						&& localAdmin.getStatus() == 2) {
					return "anon/configIndex";
				}
			}
		} else {
			if (localAdmin.getLastLoginTime() == null) {
				Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
	            model.addAttribute("email", admin.getEmail());
	            return "common/initChgPwd";
			}
		}
		return null;
	}

	/**
	 * 打开首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String enterPost(Model model) {
		return enter(model);
	}
}
