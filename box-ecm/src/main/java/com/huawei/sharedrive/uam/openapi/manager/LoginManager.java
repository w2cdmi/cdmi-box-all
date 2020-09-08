package com.huawei.sharedrive.uam.openapi.manager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.openapi.domain.*;

import pw.cdmi.uam.domain.AuthApp;

public interface LoginManager {
    //普通用户登录
    RestLoginResponse userLogin(HttpServletRequest request, RestUserLoginCreateRequest requestDomain, AuthApp authApp) throws IOException;

    //微信用户登录
    RestLoginResponse userLogin(HttpServletRequest request, RestWxUserLoginRequest loginRequest) throws IOException;

    //企业微信用户登录
    RestLoginResponse userLogin(HttpServletRequest request, RestWxworkUserLoginRequest loginRequest) throws IOException;

    //微信小程序用户登录
    RestLoginResponse userLogin(HttpServletRequest request, RestWxMpUserLoginRequest loginRequest) throws IOException;
    
    //企业微信中小程序登录
    RestLoginResponse userLogin(HttpServletRequest request, RestWxworkWxMpUserLoginRequest loginRequest) throws IOException;
    
    RestLoginResponse robotLogin(HttpServletRequest request, RestRobotLoginRequest requestDomain, AuthApp authApp) throws IOException;


	RestLoginResponse loginWithWxUser(HttpServletRequest request, String unionId, String appId) throws IOException;
}
