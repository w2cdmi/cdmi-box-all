package pw.cdmi.box.disk.user.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.httpclient.rest.UAMRestClientService;
import pw.cdmi.box.disk.httpclient.rest.request.RestTokenCreateResponse;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class UserTokenManager extends UAMRestClientService
{
    @Resource
    private RestClient uamClientService;
    
    public String getToken()
    {
        Date expire = (Date) SecurityUtils.getSubject().getSession().getAttribute("expiredTokenTime");
        String currentToken = (String) SecurityUtils.getSubject().getSession().getAttribute("platToken");
        Date date = new Date();
        // token expired
        if (expire != null && expire.before(date))
        {
            Map<String, String> headers = new HashMap<String, String>(1);
            String refreshToken = (String) SecurityUtils.getSubject()
                .getSession()
                .getAttribute("platRefrshToken");
            
            String deviceAddress = (String) SecurityUtils.getSubject()
                .getSession()
                .getAttribute("deviceAddress");
            headers.put("Authorization", refreshToken);
            headers.put("x-real-ip", deviceAddress);
            headers.put("x-request-ip", deviceAddress);
            String uri = "/api/v2/token";
            TextResponse restResponse = uamClientService.performJsonPutTextResponse(uri, headers, null);
            if (restResponse.getStatusCode() == 200)
            {
                String responseBody = restResponse.getResponseBody();
                RestTokenCreateResponse restTokenCreateResponse = JsonUtils.stringToObject(responseBody,
                    RestTokenCreateResponse.class);
                SecurityUtils.getSubject()
                    .getSession()
                    .setAttribute("platToken", restTokenCreateResponse.getToken());
                SecurityUtils.getSubject()
                    .getSession()
                    .setAttribute("platRefrshToken", restTokenCreateResponse.getRefreshToken());
                SecurityUtils.getSubject()
                    .getSession()
                    .setAttribute("expiredTokenTime",
                        new Date(System.currentTimeMillis() + restTokenCreateResponse.getTimeout() * 1000L));
                return restTokenCreateResponse.getToken();
            }
            return currentToken;
        }
        return currentToken;
    }
}
