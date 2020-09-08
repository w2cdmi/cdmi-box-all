package pw.cdmi.box.disk.sso.manager;

import pw.cdmi.box.disk.oauth2.domain.UserToken;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SsoManager {
    //判断是否支持该请求的鉴权
    boolean isSupported(HttpServletRequest request);

    boolean authentication(HttpServletRequest request, HttpServletResponse response) throws IOException;

    //判断是否支持该token的logout操作
    boolean isSupported(UserToken token);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
