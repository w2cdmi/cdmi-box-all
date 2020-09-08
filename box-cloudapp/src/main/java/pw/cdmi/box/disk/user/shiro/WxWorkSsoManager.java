package pw.cdmi.box.disk.user.shiro;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.sso.manager.SsoManager;
import pw.cdmi.box.disk.user.service.impl.UserServiceImpl;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.core.exception.InvalidParamException;

/*使用企业微信做单点登录*/
public class WxWorkSsoManager implements SsoManager {
    private static Logger logger = LoggerFactory.getLogger(WxWorkSsoManager.class);

    @Override
    public boolean isSupported(HttpServletRequest request) {
        //从企业微信浏览器中登录（包括手机端和PC端）
        if(isFromWxWorkBrowser(request)) {
            return true;
        }

        //从云盘网站入口进入，企业微信扫码后跳转回来的URL中携带了qr参数：ww
        String qr = request.getParameter("qr");
        return qr != null && qr.equals("ww");
    }

    @Override
    public boolean authentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
/*
        //Cookie中有当前用户信息，直接登录。
        String COOKIE_USER = "wx_session";
        Cookie cookie = WebUtils.getCookie(request, COOKIE_USER);
        if(cookie != null) {
            try {
                login(request, cookie.getValue());
                return true;
            } catch (Exception e) {
//                e.printStackTrace();
                request.setAttribute("redirect", "login.fail.security.forbidden");
                response.sendError(401);
                return false;
            }
        }
*/
        //
        try {
            String authCode = request.getParameter("auth_code");
            if(StringUtils.isNotBlank(authCode)) {
                //从云盘网站入口进入，最后的URL携带auth_code=xxx
                loginWithAuthCode(request, authCode);
                return true;
            }

            String code = request.getParameter("code");
            if(StringUtils.isNotBlank(code)) {
                //从企业微信浏览器入口进入，最后的URL携带code=xxx
                loginWithCode(request, code);

                return true;
            }

            //来自企业微信浏览器
            if(isFromWxWorkBrowser(request)) {
                //如果是AJAX调用，返回401错误，让浏览器自动跳转。
                if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.sendError(HttpStatus.SC_UNAUTHORIZED);
                    return false;
                }

                //未携带code，重定向开始鉴权流程
                redirectToLogin(request, response);
                return false;
            } else {
                //正常情况下不应该进入
                logger.warn("Invalid URL in WxWorkSsoManager: " + request.getRequestURL());
            }
        } catch (Exception e) {
            //加入登录错误信息
            request.getSession().setAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, e.getClass().getName());
            //使用错误码+redirect属性，控制跳转到login页面
            request.setAttribute("redirect", "login.fail.security.forbidden");
            response.sendError(401);
            logger.warn("Login Failed In WxWorkSsoManager: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public boolean isSupported(UserToken token) {
        return (token instanceof WxWorkUser);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser != null) {
            currentUser.logout();
        }
    }

    //判断是否来自企业微信浏览器
    private boolean isFromWxWorkBrowser(HttpServletRequest request) {
        String agent = request.getHeader("user-agent");
        return agent != null && agent.contains("wxwork");
    }

    //判断是否来自企业微信PC端浏览器
    private boolean isFromPcWxWorkBrowser(HttpServletRequest request) {
        String agent = request.getHeader("user-agent");
        return agent != null && agent.contains("wxwork") && (agent.contains("WindowsWechat") || agent.contains("Macintosh"));
    }

    protected void redirectToLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            //将原有URL作为redirect_uri参数传入到微信
            StringBuffer buffer = request.getRequestURL();
            if(request.getQueryString() != null) {
                buffer.append("?").append(request.getQueryString());
            }

            String corpId = RequestUtils.getCorpId(request);
            String redirect = buffer.toString();

            redirect = URLEncoder.encode(redirect, "UTF-8");
            String url = getLoginUrl(corpId, redirect);
            response.sendRedirect(url);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    private void loginWithCode(HttpServletRequest request, String code) {
        WxWorkUserToken authToken = createLoginToken(request);
        authToken.setCorpId(RequestUtils.getCorpId(request));
        authToken.setCode(code);

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(authToken);
    }

    private void loginWithAuthCode(HttpServletRequest request, String code) {
        WxWorkUserToken authToken = createLoginToken(request);
        authToken.setAuthCode(code);

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(authToken);
    }

    private WxWorkUserToken createLoginToken(HttpServletRequest request) {
        WxWorkUserToken authToken = new WxWorkUserToken();
        authToken.setSessionWebId(request.getSession().getId());

        authToken.setDeviceAddress(RequestUtils.getRealIP(request));
        authToken.setProxyAddress(RequestUtils.getProxyIP(request));
        try {
            String deviceType = request.getHeader("deviceType");
            deviceType = (deviceType == null ? UserServiceImpl.transDeviceStrType(0) : UserServiceImpl.transDeviceStrType(Integer.parseInt(deviceType)));
            authToken.setDeviceType(deviceType);
        } catch (NumberFormatException e) {
            logger.error("deviceType is not number", e);
            throw new InvalidParamException();
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        authToken.setDeviceAgent(userAgent.getBrowser().getName());
        authToken.setDeviceOS(userAgent.getOperatingSystem().getName());
        authToken.setRegionIp(request.getHeader(Constants.HTTP_X_REGION_IP));
        logger.info("User login region ip: {}", authToken.getRegionIp());

        return authToken;
    }

    protected String getLoginUrl(String corpId, String redirect) {
        //按接口要求，appid参数为服务商的CorpID; agentid为企业应用的id。。
        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + corpId + "&redirect_uri=" + redirect + "&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect";
    }
}