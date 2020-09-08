package pw.cdmi.box.disk.user.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.httpclient.rest.request.UserLoginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.WxUserLoginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.WxWorkUserLoginRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.LoginAuthFailedException;

public interface UserLoginService
{
    /**
     * 
     * @param request
     * @param response
     * @return
     */
    boolean ntlmAuthen(HttpServletRequest request, HttpServletResponse response, String basePath);
    
    /**
     * 
     * @param loginName
     * @param password
     * @param requestIp
     * @param appId
     * @return
     * @throws LoginAuthFailedException
     */
    RestLoginResponse checkFormUser(UserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException;

    //微信用户登录
    RestLoginResponse checkFormUser(WxUserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException;

    //企业微信登录
    RestLoginResponse checkFormUser(WxWorkUserLoginRequest userLoginRequest, UserToken userToken, String regionIp) throws LoginAuthFailedException, InternalServerErrorException;

    /**
     * 校验浏览器版本，低于IE8的版本跳转至于提示页面
     * 
     * @param request
     * @return
     */
    boolean checkBrowser(HttpServletRequest request, HttpServletResponse response);
    
    boolean checkBrowser(HttpServletRequest request);
    
    /**
     * 校验浏览器版本，低于IE8的版本跳转至于提示页面
     * 
     * @param request
     * @return
     */
    String getBasePath(HttpServletRequest request);
}
