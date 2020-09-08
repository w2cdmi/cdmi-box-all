package pw.cdmi.box.disk.openapi.rest.v2.manager;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.box.disk.client.domain.user.RestUserloginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.UserApiLoginRequest;

public interface UserApiCheckManager
{
    void checkV1LoginParam(RestUserloginRequest userlogin) throws LoginAuthFailedException,
        BadRquestException;
    
    void checkV2LoginParam(UserApiLoginRequest userlogin, HttpServletRequest request)
        throws LoginAuthFailedException, InvalidParamException;
}
