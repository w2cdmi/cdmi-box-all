package pw.cdmi.box.disk.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.disk.declare.manager.UserSignDeclareManager;
import pw.cdmi.box.disk.enterpriseindividual.manager.impl.EnterpriseIndividualConfigManagerImpl;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.ClientManageService;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.domain.UserSignDeclare;

@Controller
@RequestMapping(value = "/cloudapp")
public class WeixinIndexController extends CommonController {
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
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String enter(Model model, HttpServletRequest request) {
        UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();

        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setClientType(Terminal.CLIENT_TYPE_WEB_STR);
        EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(userToken.getAccountId(), userToken.getId());
        if (userSignDeclareManager.isNeedDeclaration(declare, userToken.getToken().split("/")[0])) {
            model.addAttribute("needDeclaration", true);
        } else {
            model.addAttribute("needDeclaration", false);
        }
        model.addAttribute("email", enterpriseUser.getEmail());

/*
        Object needChange = SecurityUtils.getSubject().getSession().getAttribute("isNeedChangePassword");
        if (accountUserService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId()) || "true".equals(needChange)) {
            // 密码级别提高，强制修改密码
            String pwdLevel = userToken.getPwdLevel();
            if (StringUtils.isNotBlank(pwdLevel)) {
                model.addAttribute("pwdLevel", pwdLevel);
            }
            return "common/initChgPwd";
        }
*/
        model.addAttribute("parentId", ROOT_FOLDER_ID);
        model.addAttribute("clientVersion", clientManageService.getPcClient().getVersion());
        model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));


        String iconName = EnterpriseIndividualConfigManagerImpl.getIconName(userToken.getAccountId());
        if (StringUtils.isNotBlank(iconName)) {
            SecurityUtils.getSubject().getSession().setAttribute("iconAccountId", iconName);
        } else {
            SecurityUtils.getSubject().getSession().removeAttribute("iconAccountId");
        }

        return "index";
    }

    @RequestMapping(value = "{parentId}", method = RequestMethod.GET)
    public String enter(@PathVariable("parentId") long parentId, Model model) {
        model.addAttribute("parentId", parentId);
        model.addAttribute("clientVersion", clientManageService.getPcClient().getVersion());
        model.addAttribute("linkHidden", StringUtils.isEmpty(CustomUtils.getValue("link.hidden")) ? false : CustomUtils.getValue("link.hidden"));
        return "index";
    }

    @RequestMapping(value = "folder", method = RequestMethod.GET)
    public String enterFolder(Model model, HttpServletRequest request) {
        UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Object needChange = SecurityUtils.getSubject().getSession().getAttribute("isNeedChangePassword");

        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(user.getAccountId());
        declare.setCloudUserId(user.getCloudUserId());
        declare.setClientType(Terminal.CLIENT_TYPE_H5web_STR);
        EnterpriseUser enterpriseUser = userService.getEnterpriseUserByUserId(user.getAccountId(), user.getId());
        if (userSignDeclareManager.isNeedDeclaration(declare, userToken.getToken().split("/")[0])) {
            model.addAttribute("needDeclaration", true);
        } else {
            model.addAttribute("needDeclaration", false);
        }
        // 密码级别提高，强制修改密码
        String pwdLevel = userToken.getPwdLevel();
        model.addAttribute("email", enterpriseUser.getEmail());

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

        Object docType = request.getParameter("docType");
        String directString = "folder/folderIndex";
        model.addAttribute("teamId", -1);
        if (docType != null) {

            model.addAttribute("modelDocType", docType);
        }

        String iconName = EnterpriseIndividualConfigManagerImpl.getIconName(user.getAccountId());
        if (StringUtils.isNotBlank(iconName)) {
            SecurityUtils.getSubject().getSession().setAttribute("iconAccountId", iconName);
        } else {
            SecurityUtils.getSubject().getSession().removeAttribute("iconAccountId");
        }

        return directString;
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
    
    @RequestMapping(value = "top", method = RequestMethod.GET)
    public String top() {
        return "common/topProgress";
    }
    
    @RequestMapping(value = "main", method = RequestMethod.GET)
    public String main() {
        return "main";
    }
    
}
