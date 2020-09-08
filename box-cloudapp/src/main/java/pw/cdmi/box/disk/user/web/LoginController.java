package pw.cdmi.box.disk.user.web;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.box.disk.user.shiro.WxUser;
import pw.cdmi.box.disk.utils.CustomUtils;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.core.utils.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
@RequestMapping(value = "/login")
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private ResourceBundleMessageSource messageSource;

    //微信
    private String wxAppId = "wxf54677c64020f6f1";
    private String wxRedirectUrl = "https://www.jmapi.cn/personal/?qr=wx&type=person";
    private String wxRegisterUrl = "https://www.jmapi.cn/personal/register?qr=wx";
    private String wxRobotRedirectUrl = "https://www.jmapi.cn/personal/wxrobot/wxBak?qr=wx&type=person";

    //企业微信
    private String wwAppId = "wwc7342fa63c523b9a";
    private String wwRedirectUrl = "https://www.jmapi.cn/?qr=ww";

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWxRedirectUrl() {
        return wxRedirectUrl;
    }

    public void setWxRedirectUrl(String wxRedirectUrl) {
        this.wxRedirectUrl = wxRedirectUrl;
    }

    public String getWxRegisterUrl() {
        return wxRegisterUrl;
    }

    public void setWxRegisterUrl(String wxRegisterUrl) {
        this.wxRegisterUrl = wxRegisterUrl;
    }

    public String getWxRobotRedirectUrl() {
        return wxRobotRedirectUrl;
    }

    public void setWxRobotRedirectUrl(String wxRobotRedirectUrl) {
        this.wxRobotRedirectUrl = wxRobotRedirectUrl;
    }

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
    public String login(HttpServletRequest request, Model model) {
        if (!userLoginService.checkBrowser(request)) {
            return "user/browserVersionTips";
        }

        if ("true".equals(CustomUtils.getValue("cloudapp.login"))) {
            return "user/" + CustomUtils.getParams("cloudapp.login");
        }

        UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();

        //判断已经登录，直接返回主页
        if (userToken != null && StringUtils.isNotBlank(userToken.getToken())) {
            return "redirect:/";
        }

        //获取Cookie中的企业名称，放入登录页面，避免用户每次登录都要输入
        String enterpriseName = CookieUtils.getCookieValue(request, "enterpriseName");
        if(StringUtils.isNotBlank(enterpriseName)) {
            try {
                model.addAttribute("enterpriseName", URLDecoder.decode(enterpriseName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            }
        }
//        model.addAttribute("username", CookieUtils.getCookieValue(request, "username"));

        //处理流程：
        // 1. 登录发生错误，在session中存储错误信息，设置错误码401和redirect属性。
        // 2. tomcat根据错误码401跳转到/login/turnToError
        // 3. turnToError返回给浏览器，判断redirect然后通过js让浏览器跳转到/login页面（window.location.href）。
        // 4. 在此处理/login请求

        // 判断是否存在上次登录错误, 显示给用户。
        String error = (String)request.getSession().getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        if(StringUtils.isNotBlank(error)) {
            model.addAttribute("errorMessage", translateErrorMessage(request, error));
            //删除，防止发生错误后会话中一直存在此属性
            request.getSession().removeAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        }

        try {
            String corpId = RequestUtils.getCorpId(request);
            model.addAttribute("corpId", corpId);

            //微信扫码
            model.addAttribute("wxAppId", wxAppId);
            model.addAttribute("wxRedirectUrl", URLEncoder.encode(wxRedirectUrl, "UTF-8"));
            model.addAttribute("wxRegisterUrl", URLEncoder.encode(wxRegisterUrl, "UTF-8"));
            model.addAttribute("wxRobotRedirectUrl", URLEncoder.encode(wxRobotRedirectUrl, "UTF-8"));

            //企业微信扫码
            model.addAttribute("wwAppId", wwAppId);
            model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
//            e.printStackTrace();
        }

        return "user/login";
    }

    private String translateErrorMessage(HttpServletRequest request, String error) {
        if(error != null) {
            if(error.contains(".LockedAccountException")) {
                return messageSource.getMessage("login.locked.prefix", null, request.getLocale()) + (String)request.getAttribute("lockWaitTip") + messageSource.getMessage("login.locked.suffix", null, request.getLocale());
            }
            if(error.contains(".NoSuchEnterpriseException")) {
                return messageSource.getMessage("login.errorMsg.noEnterpriseExist", null, request.getLocale());
            }
            if(error.contains(".IncorrectCredentialsException")||error.contains(".ADLoginAuthFailedException")) {
                return messageSource.getMessage("login.errorMsg.notExists", null, request.getLocale());
            }
            if(error.contains(".AccountException")) {
                return messageSource.getMessage("login.errorMsg.idivalUser", null, request.getLocale());
            }
            if(error.contains(".DisabledAccountException")) {
                return messageSource.getMessage("login.fail.network.forbid", null, request.getLocale());
            }
            if(error.contains(".SecurityMartixException")) {
                return messageSource.getMessage("login.fail.security.forbidden", null, request.getLocale());
            }
            if(error.contains(".NoCaptchaException")) {
                return messageSource.getMessage("verifycode.error.empty", null, request.getLocale());
            }
            if(error.contains(".InvalidCaptchaException")) {
                return messageSource.getMessage("verifycode.error.invalid", null, request.getLocale());
            }
            if(error.contains(".LoginAuthFailedException") || error.contains(".AuthenticationException")) {
                return messageSource.getMessage("login.fail", null, request.getLocale());
            }
            if(error.contains(".WxAuthFailedException")) {
                return messageSource.getMessage("login.fail.wxQr", null, request.getLocale());
            }
            if(error.contains(".WxWorkAuthFailedException")) {
                return messageSource.getMessage("login.fail.wwQr", null, request.getLocale());
            }
        }

        return messageSource.getMessage("login.fail", null, request.getLocale());
    }

    /**
     * @param userName
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName, Model model) {
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, userName);
        return "user/login";
    }

    @RequestMapping(value = "/turnToError", method = RequestMethod.GET)
    public String goToErrorPage(HttpServletRequest request, Model model) {
        String corpId = RequestUtils.getCorpId(request);
        model.addAttribute("corpId", corpId);

        return "error";
    }

    @RequestMapping(value = "/turnToError/{error}", method = RequestMethod.GET)
    public String goToErrorPage(@PathVariable("error") String error, HttpServletRequest request, Model model) {
        String corpId = RequestUtils.getCorpId(request);
        model.addAttribute("corpId", corpId);

        if(StringUtils.isNotBlank(error)) {
            if("400".equals(error)) {
                return "common/400";
            }

            if("401".equals(error)) {
                return "common/401";
            }

            if("404".equals(error)) {
                return "common/404";
            }

            if("500".equals(error)) {
                return "common/500";
            }
        }

        return "error";
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/refreshLoginCookie", method = RequestMethod.GET)
    public ResponseEntity refreshLoginCookie() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/authfor", method = RequestMethod.GET)
    public String loginAuthfor() {
        return "user/loginAuth";
    }

    @RequestMapping(value = "/enterpriseList", method = RequestMethod.GET)
    public String enterpriseList(HttpServletRequest request, Model model) {
        UserToken userToken = (UserToken) SecurityUtils.getSubject().getPrincipal();

        if(userToken == null) {
            return "user/login";
        }

        //判断已经登录，直接返回主页
        if (StringUtils.isNotBlank(userToken.getToken())) {
            return "redirect:/";
        }

        if(userToken instanceof WxUser) {
            WxUser wxUser = (WxUser)userToken;
            model.addAttribute("code", wxUser.getCode());
            model.addAttribute("enterpriseList", wxUser.getEnterpriseList());
            return "user/enterpriseList";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/chooseEnterprise", method = RequestMethod.POST)
    public String chooseEnterprise(HttpServletRequest request, Model model) {
        return "redirect:/";
    }
}
