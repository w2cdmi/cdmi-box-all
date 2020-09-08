package pw.cdmi.box.disk.user.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.cdmi.box.disk.declare.manager.UserSignDeclareManager;
import pw.cdmi.box.disk.doctype.domain.DocUserConfig;
import pw.cdmi.box.disk.doctype.service.DocTypeService;
import pw.cdmi.box.disk.enterpriseindividual.manager.impl.EnterpriseIndividualConfigManagerImpl;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.ClientManageService;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.disk.weixin.service.WxUserEnterpriseService;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.domain.UserSignDeclare;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

@Controller
@RequestMapping(value = "/")
public class IndexController extends CommonController {
	public static final String THEME_KEY = "THEME_KEY";

	private static final long ROOT_FOLDER_ID = 0L;

	@Autowired
	private AccountUserService accountUserService;

	@Autowired
	private ClientManageService clientManageService;

	@Autowired
	private CustomizeLogoService customizeLogoService;

	@Autowired
	private UserSignDeclareManager userSignDeclareManager;

	@Autowired
	private TeamSpaceService teamSpaceService;

	@Autowired
	private DocTypeService docTypeService;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceBundleMessageSource messageSource;

	@Autowired
	WxUserEnterpriseService wxUserEnterpriseService;

	private String wwAppId = "wwc7342fa63c523b9a";
	private String wwRedirectUrl = "https://www.jmapi.cn/?qr=ww";

	public String getWwAppId() {
		return wwAppId;
	}

	public void setWwAppId(String wwAppId) {
		this.wwAppId = wwAppId;
	}

	public String getWwRedirectUrl() {
		return wwRedirectUrl;
	}

	public void setWwRedirectUrl(String wwRedirectUrl) {
		this.wwRedirectUrl = wwRedirectUrl;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String enter(Model model, HttpServletRequest request) {
		UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		String directString = "index";
		// 未登录时，直接返回主页内容。此处是为web定制，企业微信（手机端和PC端）都不应该走此方法。
		if (user != null) {
			Object needChange = SecurityUtils.getSubject().getSession().getAttribute("isNeedChangePassword");
			UserSignDeclare declare = new UserSignDeclare();
			declare.setAccountId(user.getAccountId());
			declare.setCloudUserId(user.getCloudUserId());
			declare.setClientType(Terminal.CLIENT_TYPE_WEB_STR);

			if (userSignDeclareManager.isNeedDeclaration(declare, userToken.getToken().split("/")[0])) {
				model.addAttribute("needDeclaration", true);
			} else {
				model.addAttribute("needDeclaration", false);
			}
			// 密码级别提高，强制修改密码
			String pwdLevel = userToken.getPwdLevel();
			if (user.getAccountId() != 0) {
				EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(user.getAccountId(), user.getId());
				model.addAttribute("email", enterpriseUser.getEmail());

				if ((accountUserService.isLocalAndFirstLogin(user.getAccountId(), user.getId())) || (null != needChange && "true".equals(needChange))) {
					if (StringUtils.isNotBlank(pwdLevel)) {
						model.addAttribute("pwdLevel", pwdLevel);
					}
					return "common/initChgPwd";
				}
			}
			model.addAttribute("parentId", ROOT_FOLDER_ID);
			model.addAttribute("clientVersion", clientManageService.getPcClient().getVersion());
			model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));
			Object docType = request.getParameter("docType");
			String isTeamDoctypeSelect = request.getParameter("teamDocType");
			model.addAttribute("teamId", -1);
			if (isTeamDoctypeSelect != null) {
				model.addAttribute("modelDocType", isTeamDoctypeSelect);
				String teamId = request.getParameter("teamId");
				model.addAttribute("teamId", teamId);
				RestTeamSpaceInfo teamSpace = teamSpaceService.getTeamSpace(Long.valueOf(teamId), getToken());
				DocUserConfig docUserConfig = docTypeService.getDoctypeById(Long.valueOf(isTeamDoctypeSelect), getToken());
				model.addAttribute("teamName", teamSpace.getName());
				model.addAttribute("docTypeName", docUserConfig.getName());
				directString = "teamspace/spaceDetail";
			}

			if (docType != null) {
				model.addAttribute("modelDocType", docType);
			}

			String iconName = EnterpriseIndividualConfigManagerImpl.getIconName(user.getAccountId());
			if (StringUtils.isNotBlank(iconName)) {
				SecurityUtils.getSubject().getSession().setAttribute("iconAccountId", iconName);
			} else {
				SecurityUtils.getSubject().getSession().removeAttribute("iconAccountId");
			}
		}

		try {
			model.addAttribute("wwAppId", wwAppId);
			model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}

		return directString;
	}

	@RequestMapping(value = "folder", method = RequestMethod.GET)
	public String enterFolder(Model model, HttpServletRequest request, Locale locale) {
		UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Object needChange = SecurityUtils.getSubject().getSession().getAttribute("isNeedChangePassword");

		UserSignDeclare declare = new UserSignDeclare();
		declare.setAccountId(user.getAccountId());
		declare.setCloudUserId(user.getCloudUserId());
		declare.setClientType(Terminal.CLIENT_TYPE_H5web_STR);
		if (userToken.getAccountId() != 0) {
			EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(user.getAccountId(), user.getId());
			model.addAttribute("email", enterpriseUser.getEmail());
		}

		if (userSignDeclareManager.isNeedDeclaration(declare, userToken.getToken().split("/")[0])) {
			model.addAttribute("needDeclaration", true);
		} else {
			model.addAttribute("needDeclaration", false);
		}
		// 密码级别提高，强制修改密码
		String pwdLevel = userToken.getPwdLevel();

		if ((accountUserService.isLocalAndFirstLogin(user.getAccountId(), user.getId())) || (null != needChange && "true".equals(needChange))) {
			if (StringUtils.isNotBlank(pwdLevel)) {
				model.addAttribute("pwdLevel", pwdLevel);
			}
			return "common/initChgPwd";
		}
		if (request.getParameter("rootNode") != null && !"".equals(request.getParameter("rootNode"))) {
			model.addAttribute("parentId", request.getParameter("rootNode"));
		} else {
			model.addAttribute("parentId", ROOT_FOLDER_ID);
		}

		model.addAttribute("clientVersion", clientManageService.getPcClient().getVersion());
		model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));

		model.addAttribute("teamId", -1);
		if (request.getParameter("isWx") != null) {
			model.addAttribute("isWx", request.getParameter("isWx"));
		} else {
			model.addAttribute("isWx", 0);
		}
		model.addAttribute("wxCloudUserId", userToken.getWxCloudUserId());
		// 面包屑根目录节点名称
		model.addAttribute("rootPath", messageSource.getMessage("file.label.mySpace", null, locale));
		Object docType = request.getParameter("docType");
		if (docType != null) {
			model.addAttribute("modelDocType", docType);
		}

		String iconName = EnterpriseIndividualConfigManagerImpl.getIconName(user.getAccountId());
		if (StringUtils.isNotBlank(iconName)) {
			SecurityUtils.getSubject().getSession().setAttribute("iconAccountId", iconName);
		} else {
			SecurityUtils.getSubject().getSession().removeAttribute("iconAccountId");
		}

		return "folder/folderIndex";
	}

	@RequestMapping(value = "app", method = RequestMethod.GET)
	public String enterApp(Model model, HttpServletRequest request) {
		CustomizeLogo temp = customizeLogoService.getCustomize();
		String domainName = temp.getDomainName();
		if (StringUtils.isEmpty(domainName)) {
			model.addAttribute("urlPrefix", request.getContextPath());
		} else {
			StringBuffer sb = new StringBuffer(StringUtils.trimToEmpty(domainName));
			if ('/' != sb.charAt(sb.length() - 1)) {
				sb.append('/');
			}
			model.addAttribute("urlPrefix", sb.toString());
		}

		ClientManage pcClient = clientManageService.getPcClient();
		ClientManage androidClient = clientManageService.getAndroidClient();
		ClientManage iosClient = clientManageService.getIOSClient();
		ClientManage couderClient = clientManageService.getClouderClient();
		model.addAttribute("pcClient", pcClient);
		model.addAttribute("androidClient", androidClient);
		model.addAttribute("iosClient", iosClient);
		model.addAttribute("clouderClient", couderClient);
		return "app/appIndex";
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "theme", method = RequestMethod.POST)
	public ResponseEntity<?> theme(String theme, HttpServletRequest request) {
		super.checkToken(request);
		HttpSession session = request.getSession();
		if (StringUtils.isEmpty(theme)) {
			session.removeAttribute(THEME_KEY);
		} else {
			session.removeAttribute(THEME_KEY);
			session.setAttribute(THEME_KEY, theme);
		}
		return new ResponseEntity(HttpStatus.OK);
	}

	// 为Web端增加的路径
	@RequestMapping(value = "/price", method = RequestMethod.GET)
	public String price(HttpServletRequest request, Model model) {
		try {
			model.addAttribute("wwAppId", wwAppId);
			model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}

		return "price";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String download(HttpServletRequest request, Model model) {
		try {
			model.addAttribute("wwAppId", wwAppId);
			model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}

		return "download";
	}
}
