package pw.cdmi.box.disk.openapi.rest.v1.service;

import pw.cdmi.box.disk.httpclient.rest.request.RestTokenCreateResponse;

public interface UserAuthService
{
    RestTokenCreateResponse checkRefresh(String refreshToken);
    
    void deleteToken(String token);
}
