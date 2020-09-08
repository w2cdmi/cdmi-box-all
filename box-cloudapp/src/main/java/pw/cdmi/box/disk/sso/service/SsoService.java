package pw.cdmi.box.disk.sso.service;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.LoginAuthFailedException;

public interface SsoService
{
    RestLoginResponse checkTmpToken(HttpServletRequest request, String ssotoken)
        throws LoginAuthFailedException, InternalServerErrorException;
}
