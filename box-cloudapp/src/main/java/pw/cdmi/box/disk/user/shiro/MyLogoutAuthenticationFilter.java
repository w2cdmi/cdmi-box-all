package pw.cdmi.box.disk.user.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.openapi.rest.v1.service.UserAuthService;
import pw.cdmi.box.disk.sso.manager.SsoManager;
import pw.cdmi.box.disk.utils.CSRFTokenManager;
import pw.cdmi.core.exception.InternalServerErrorException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MyLogoutAuthenticationFilter extends LogoutFilter {
    private static Logger logger = LoggerFactory.getLogger(MyLogoutAuthenticationFilter.class);

    private UserAuthService userAuthService;

    private List<SsoManager> ssoManagerList;

    private String forwardUrl;

    public void setUserAuthService(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    public List<SsoManager> getSsoManagerList() {
        return ssoManagerList;
    }

    public void setSsoManagerList(List<SsoManager> ssoManagerList) {
        this.ssoManagerList = ssoManagerList;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Object sessionToken = SecurityUtils.getSubject().getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
        Subject subject = getSubject(request, response);

        UserToken userToken = (UserToken) subject.getPrincipal();
        if (userToken == null) {
            logger.error("no user is login, userToken is null");
            handelResponse(req, res);
            return false;
        }

/*
        String token = request.getParameter("token");
        if (token == null || (null != sessionToken && !token.equals(sessionToken))) {
            logger.error("Bad Request for CSRF:");
            req.setAttribute("redirect", "CSRF-FLAG");
            res.sendError(401);
            return false;
        }
*/
        try {
            String platToken = (String) SecurityUtils.getSubject().getSession().getAttribute("platToken");
            if (platToken != null) {
                userAuthService.deleteToken(platToken);
            }
        } catch (InvalidSessionException e) {
            logger.info("Error occurred while deleting token: " + e.getMessage());
//            e.printStackTrace();
        }

        try {
            subject.logout();
            subject.releaseRunAs();

            //配置了单点登录服务器, 通知logout操作
            if (ssoManagerList != null) {
                for (SsoManager s : ssoManagerList) {
                    if (s.isSupported(userToken)) {
                        s.logout((HttpServletRequest) request, (HttpServletResponse) response);
                    }
                }
            }

            //issueRedirect(request, response, redirectUrl);
            handelResponse(req, res);
        } catch (SessionException ise) {
            logger.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        } catch (InternalServerErrorException e) {
            logger.warn("", e);
        }

        return false;
    }

    private void handelResponse(HttpServletRequest req, HttpServletResponse res) {
        try {
            //Ajax请求
            if ("XMLHttpRequest".equalsIgnoreCase(req.getHeader("X-Requested-With"))) {
                res.getOutputStream().println("OK");
                res.flushBuffer();
            } else {
                jumpToLogin(req, res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到登录页面
     */
    private void jumpToLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        StringBuilder contextPath = new StringBuilder();
        contextPath.append(req.getContextPath());

        String url = forwardUrl;
        if(StringUtils.isBlank(url)) {
            url = "/login";
        }

        //确保url以/开头
        if(!url.startsWith("/")){
            url = "/" + url;
        }

        //确保不以/结尾
        if (contextPath.length() > 0 && contextPath.charAt(contextPath.toString().length() - 1) == '/') {
            contextPath.deleteCharAt(contextPath.toString().length() - 1);
        }
        contextPath.append(url);

        res.sendRedirect(contextPath.toString());
    }
}
