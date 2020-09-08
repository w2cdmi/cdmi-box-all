package pw.cdmi.box.disk.user.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.enterprisecontrol.EnterpriseAuthControlManager;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.sso.manager.SsoManager;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.core.utils.CookieUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AutoLoginAuthenticationFilter extends UserFilter {
    private UserLoginService userLoginService;

    private SecurityService securityService;

    private EnterpriseAuthControlManager enterpriseAuthControlManager;

    private AuthAppService authAppService;

    private List<SsoManager> ssoManagerList;

    public static final String REFRESH_COOKIE = "/login/refreshLoginCookie";

    public static final String AUTH_FORWORD = "/login/authforword";

    public static final String AUTH_FOR = "/login/authfor";

    private static Logger logger = LoggerFactory.getLogger(AutoLoginAuthenticationFilter.class);

    public void setUserLoginService(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setEnterpriseAuthControlManager(EnterpriseAuthControlManager enterpriseAuthControlManager) {
        this.enterpriseAuthControlManager = enterpriseAuthControlManager;
    }

    public void setAuthAppService(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    public List<SsoManager> getSsoManagerList() {
        return ssoManagerList;
    }

    public void setSsoManagerList(List<SsoManager> ssoManagerList) {
        this.ssoManagerList = ssoManagerList;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //将corpId加入到session中, 企业微信需要使用corpId来初始化JS-SDK
        String corpId = request.getParameter("weixinCorpId");
        if(corpId != null) {
            request.getSession().setAttribute("corpId", corpId);

            //将corpId放入cookie中，会话超时，企业微信登录可以携带直接登录
            Cookie cookie = new Cookie("corpId", corpId);
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
        }

        // 版本过低直接跳转至版本过低提示页面
        if (!userLoginService.checkBrowser(request, response)) {
            return false;
        }

        //允许登录页面
        if (isLoginRequest(req, resp)) {
            return true;
        }

        //subject不为空，且token不为空，代表已经登录。
        UserToken token = (UserToken)getSubject(req, resp).getPrincipal();
        if (token != null && token.getToken() != null) {
            return true;
        }

        //刷新Login Cookie, 允许访问
        String path = getPathWithinApplication(req);
        if (StringUtils.isNotBlank(path) && pathsMatch(path, REFRESH_COOKIE)) {
            return true;
        }

        return false;
    }

    private boolean isDevicePC(String deviceType) {
        return "1".equals(deviceType);
    }

    private String getProtocolType(SecurityConfig securityConfig) {
        String protocolType = securityConfig.getProtocolType();
        if (StringUtils.isBlank(protocolType)) {
            protocolType = "https";
        }
        return protocolType;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest req, ServletResponse resp) throws IOException {
/*
        if (response != null && response.isCommitted()) {
			return false;
		}
		if (response != null) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (httpResponse.getStatus() != HttpServletResponse.SC_BAD_REQUEST) {
				saveRequestAndRedirectToLogin(request, response);
			}
		}
		return false;
*/
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        try {
            SecurityConfig securityConfig = securityService.getSecurityConfig();
            String protocolType = getProtocolType(securityConfig);
            request.getSession().setAttribute("reqProtocol", protocolType);

            String path = getPathWithinApplication(req);
            String deviceType = request.getHeader("deviceType");

            //配置了单点登录服务器
            if(ssoManagerList != null ) {
                for(SsoManager s : ssoManagerList) {
                    if(s.isSupported(request)) {
                        return s.authentication(request, response);
                    }
                }
            }

            String basePath = protocolType + "://" + request.getServerName() + securityService.changePort(String.valueOf(request.getServerPort()), protocolType) + request.getContextPath();
            //可以使用NTLM登录
            if (pathsMatch(path, AUTH_FORWORD) || isDevicePC(deviceType)) {
                boolean isAccess = userLoginService.ntlmAuthen(request, response, basePath);
                if (isAccess && null == deviceType) {
                    String savedRequestURI = request.getSession().getAttribute("savedRequestStr") == null ? basePath + '/' : request.getSession().getAttribute("savedRequestStr").toString();
                    response.sendRedirect(savedRequestURI);
                }
                return false;
            }

            //可以使用NTLM登录，跳转
            if (enterpriseAuthControlManager.isCanNtlm(authAppService.getCurrentAppId())) {
                String servletPath = request.getServletPath();
                request.getSession().setAttribute("savedRequestStr", basePath + servletPath);
                response.sendRedirect(basePath + AUTH_FOR);
                return false;
            }

            saveAndRedirectToLogin(request, response, protocolType);

            return false;
        } catch (IOException e) {
            logger.error("ntlm auth failed ");
            return false;
        }
    }

    protected void saveAndRedirectToLogin(HttpServletRequest request, HttpServletResponse response, String protocol) throws IOException {
        String requestPath = protocol + "://" + request.getServerName() + securityService.changePort(String.valueOf(request.getServerPort()), protocol) + request.getRequestURI();
        if (requestPath.contains("/v/") || requestPath.endsWith("/teamspace") || requestPath.contains("/trash")) {
            request.getSession().setAttribute("savedRequestStr", requestPath);
        } else {
            //只有Cookie中指定的链接与当前链接相同，才记住当前的访问URL. (WHY??!!)
            String hrefUrl = CookieUtils.getCookieValue(request, "hrefUrl");
            if(requestPath.equals(hrefUrl)) {
                request.getSession().setAttribute("savedRequestStr", hrefUrl);
            }
        }

        String basePath = protocol + "://" + request.getServerName() + securityService.changePort(String.valueOf(request.getServerPort()), protocol) + request.getContextPath();
        response.sendRedirect(basePath + "/login");
    }
}
