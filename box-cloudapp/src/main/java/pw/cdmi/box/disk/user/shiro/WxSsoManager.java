package pw.cdmi.box.disk.user.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.sso.manager.SsoManager;
import pw.cdmi.box.disk.user.service.impl.UserServiceImpl;
import pw.cdmi.box.disk.utils.RequestUtils;
import pw.cdmi.common.useragent.UserAgent;
import pw.cdmi.core.exception.InvalidParamException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*使用微信做单点登录*/
public class WxSsoManager implements SsoManager {
    private static Logger logger = LoggerFactory.getLogger(WxSsoManager.class);

    @Override
    public boolean isSupported(HttpServletRequest request) {
        //从云盘网站入口进入，企业微信扫码后跳转回来的URL中携带了qr参数：ww
        String qr = request.getParameter("qr");
        return qr != null && qr.equals("wx");
    }

    @Override
    public boolean authentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserToken token = (UserToken)SecurityUtils.getSubject().getPrincipal();
        if(token != null && StringUtils.isNotBlank(token.getToken())) {
            return true;
        }
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

        String code = request.getParameter("code");
        if(StringUtils.isBlank(code)) {
            code = request.getParameter("wxCode");
        }
        String type=request.getParameter("type");
        //未携带code，重定向开始鉴权
        if (StringUtils.isNotBlank(code)) {
            try {
            	if(type!=null&&type.equals("person")){
            		 login(request, code,type);
            	}else{
            		 login(request, code);
            	}

                //检查登录结果
                WxUser wxUser = (WxUser)SecurityUtils.getSubject().getPrincipal();
                //enterpriseId=0, 且enterpriseList不为空，说明需要用户选择企业，继续登录
                if(wxUser.getEnterpriseId() == 0 && wxUser.getEnterpriseList() != null) {
                    response.sendRedirect(request.getContextPath() + "/login/enterpriseList");
                    return false;
                }
/*
                //使用cookie保存
                cookie = new Cookie(COOKIE_USER, code);
                cookie.setMaxAge(60*60*24);
                response.addCookie(cookie);
*/
                return true;
            } catch (Exception e) {
                //加入登录错误信息
                request.getSession().setAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, e.getClass().getName());
/*
                //使用错误码+redirect属性，控制跳转到login页面
                request.setAttribute("redirect", "login.fail.security.forbidden");
                response.sendError(401);
*/
                //直接跳转到登录页面
                request.setAttribute("redirect", "login.fail.security.forbidden");
                response.sendRedirect(request.getContextPath() + "/login");
                logger.info("Login Failed In WxSsoManager: ", e);
            }

            return false;
        } else {
            //正常情况下，从open.weixin.qq.com跳转过来的URL中，都会带有code.
            logger.error("No code found in url: " + request.getRequestURI());
        }

        return false;
    }

    private void login(HttpServletRequest request, String code) {
        WxUserToken authToken = new WxUserToken(code);
        String enterpriseId = request.getParameter("enterpriseId");
        if(StringUtils.isNotBlank(enterpriseId)) {
            authToken.setEnterpriseId(Long.valueOf(enterpriseId));
        }

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

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(authToken);
    }
    
    
    private void login(HttpServletRequest request, String code,String type) {
        WxUserToken authToken = new WxUserToken(code);
        authToken.setIdentity(WxUserToken.IDENTITY_PERSON);
        authToken.setEnterpriseId(0L);
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
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(authToken);
    }

    @Override
    public boolean isSupported(UserToken token) {
        return (token instanceof WxUser);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser != null) {
            currentUser.logout();
        }
    }
}