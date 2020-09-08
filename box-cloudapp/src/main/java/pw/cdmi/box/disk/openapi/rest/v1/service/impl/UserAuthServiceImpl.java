package pw.cdmi.box.disk.openapi.rest.v1.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.httpclient.rest.UAMRestClientService;
import pw.cdmi.box.disk.httpclient.rest.request.RestTokenCreateResponse;
import pw.cdmi.box.disk.openapi.rest.v1.service.UserAuthService;
import pw.cdmi.core.exception.DisabledTerminalStatusException;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class UserAuthServiceImpl extends UAMRestClientService implements UserAuthService
{
    
    @Resource
    private RestClient uamClientService;
    
    @Override
    public RestTokenCreateResponse checkRefresh(String refreshToken)
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", refreshToken);
        TextResponse restResponse = uamClientService.performJsonPutTextResponse("/api/v2/token",
            headers,
            null);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            throw new DisabledTerminalStatusException();
        }
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            throw new DisabledTerminalStatusException();
        }
        RestTokenCreateResponse restTokenCreateResponse = JsonUtils.stringToObject(responseBody,
            RestTokenCreateResponse.class);
        return restTokenCreateResponse;
    }

    @Override
    public void deleteToken(String token) {
        if (token == null) {
            return;
        }
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        TextResponse restResponse = uamClientService.performDelete("/api/v2/token", headers);
        if (restResponse == null || restResponse.getStatusCode() != 200) {
            throw new InternalServerErrorException();
        }
    }
}
