package com.huawei.sharedrive.uam.user.shiro;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.common.useragent.UserAgent;

/*使用企业微信做单点登录*/
public class WxWorkSsoManager implements SsoManager {
    private static Logger logger = LoggerFactory.getLogger(WxWorkSsoManager.class);

    @Override
    public boolean isSupported(HttpServletRequest request) {
        String query = request.getQueryString();
        //带有auth_code参数，说明是从企业微信后台跳转过来的
        return query != null && query.contains("auth_code=");
    }

    @Override
    public boolean authentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("auth_code");
        try {
            login(request, code);
            return true;
        } catch (Exception e) {
//                    e.printStackTrace();
            request.setAttribute("redirect", "login.fail.security.forbidden");
            response.sendError(401);
        }

        return false;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser != null) {
            currentUser.logout();
        }
    }

    private void login(HttpServletRequest request, String code) {
        WxWorkAuthCodeToken authToken = new WxWorkAuthCodeToken(code);
        authToken.setSessionWebId(request.getSession().getId());
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        authToken.setDeviceAgent(userAgent.getBrowser().getName());
        authToken.setDeviceOS(userAgent.getOperatingSystem().getName());

        Subject currentUser = SecurityUtils.getSubject();
        currentUser.getPrincipal();
        currentUser.login(authToken);
    }
}