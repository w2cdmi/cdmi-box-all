package pw.cdmi.box.disk.openapi.rest.v2;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.httpclient.rest.request.RestUserLoginResponse;
import pw.cdmi.box.disk.httpclient.rest.request.WxUserLoginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.WxWorkUserLoginRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientLoginRequest;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.box.disk.user.web.LoginController;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.core.exception.InvalidParamException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/* 此类用于协助客户端（目前只有PC端）完成登录功能。
 * 1. 用户打开客户端，直接打开此处的login.jsp
 * 2. 用户输入用户密码或扫码后，最后再跳转回来。
 * 3. 服务器将登录的token，写入到js代码中，然后发送给客户端，js在客户端直接执行拉起本地的页面。
* */

@Controller
@RequestMapping(value = "/api/v2/client")
public class ClientLoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private AuthAppService authAppService;

    private String authAppId;

    private String wxRedirectUrl = "https://www.jmapi.cn/api/v2/client/takeToken?qr=wx";

    private String wxAppId = "wxf54677c64020f6f1";

    private String wwRedirectUrl = "https://www.jmapi.cn/api/v2/client/takeToken?qr=ww";

    private String wwAppId = "wwc7342fa63c523b9a";

    public String getAuthAppId() {
        if(authAppId == null) {
            authAppId = authAppService.getCurrentAppId();
        }

        return authAppId;
    }

    public String getWxRedirectUrl() {
        return wxRedirectUrl;
    }

    public void setWxRedirectUrl(String wxRedirectUrl) {
        this.wxRedirectUrl = wxRedirectUrl;
    }

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getWwRedirectUrl() {
        return wwRedirectUrl;
    }

    public void setWwRedirectUrl(String wwRedirectUrl) {
        this.wwRedirectUrl = wwRedirectUrl;
    }

    public String getWwAppId() {
        return wwAppId;
    }

    public void setWwAppId(String wwAppId) {
        this.wwAppId = wwAppId;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        try {
            model.addAttribute("wxRedirectUrl", URLEncoder.encode(wxRedirectUrl, "UTF-8"));
            model.addAttribute("wxAppId", wxAppId);
            model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
            model.addAttribute("wwAppId", wwAppId);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }

        return "client/login";
    }

    //从微信服务器跳转过来的链接(302)
    @RequestMapping(value = "/takeToken", method = RequestMethod.GET)
    public String getToken(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "client/token";
    }

    @RequestMapping(value = "/takeToken", method = RequestMethod.POST)
    @ResponseBody
    public RestLoginResponse takeToken(HttpServletRequest request, @RequestBody ClientLoginRequest loginRequest) {
        String qr = loginRequest.getQr();
        if(StringUtils.isBlank(qr)) {
            logger.warn("No qr parameter found.");
            return null;
        }

        String code = loginRequest.getCode();
        if(StringUtils.isBlank(code)) {
            logger.warn("No code found in request.");
            return null;
        }

        RestUserLoginResponse loginResponse = null;
        try {
            if(qr.equals("wx")) {
                //微信扫码登录
                loginResponse = getTokenOfWxUser(request, loginRequest.getEnterpriseId(), code);
            } else if(qr.equals("ww")) {
                //企业微信扫码登录
                loginResponse = getTokenOfWxWorkUser(request, code);
            } else {
                logger.warn("Not supported qr [{}].", qr);
            }
        } catch (Exception e) {
            logger.warn("Error occurred while getting toke from ecm: {}", e.getMessage());
            logger.debug("Error occurred while getting toke from ecm: ", e);
//            e.printStackTrace();
        }

        return loginResponse;
    }

    private RestUserLoginResponse getTokenOfWxUser(HttpServletRequest request, Long enterpriseId, String code) {
        WxUserLoginRequest loginRequest = new WxUserLoginRequest();
        loginRequest.setAppId(getAuthAppId());
        loginRequest.setEnterpriseId(enterpriseId);
        loginRequest.setCode(code);

        UserToken userToken = createUserToken(request);

        return (RestUserLoginResponse)userLoginService.checkFormUser(loginRequest, userToken, userToken.getRegionIp());
    }

    private RestUserLoginResponse getTokenOfWxWorkUser(HttpServletRequest request, String authCode) {
        WxWorkUserLoginRequest loginRequest = new WxWorkUserLoginRequest();
        loginRequest.setAppId(getAuthAppId());
        loginRequest.setAuthCode(authCode);
        UserToken userToken = createUserToken(request);

        return (RestUserLoginResponse)userLoginService.checkFormUser(loginRequest, userToken, userToken.getRegionIp());
    }

    private static UserToken createUserToken(HttpServletRequest request) {
        UserToken userToken = new UserToken();

        userToken.setDeviceAddress(RequestUtils.getRealIP(request));
        userToken.setProxyAddress(RequestUtils.getProxyIP(request));
        try {
            String deviceType = request.getHeader("deviceType");
            if(deviceType == null) {
                deviceType = "0";
            }
            userToken.setDeviceType(Integer.parseInt(deviceType));
        } catch (NumberFormatException e) {
            logger.error("deviceType is not number", e);
            throw new InvalidParamException();
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        userToken.setDeviceAgent(userAgent.getBrowser().getName());
        userToken.setDeviceOS(userAgent.getOperatingSystem().getName());
        userToken.setRegionIp(request.getHeader(Constants.HTTP_X_REGION_IP));

        return userToken;
    }

}
