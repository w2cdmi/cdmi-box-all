package com.huawei.sharedrive.uam.user.shiro;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SsoManager {
    boolean isSupported(HttpServletRequest request);

    boolean authentication(HttpServletRequest request, HttpServletResponse response) throws IOException;

    void logout(HttpServletRequest request, HttpServletResponse response);
}
