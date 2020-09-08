package pw.cdmi.box.disk.oauth2.client;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.core.exception.AuthFailedException;
import pw.cdmi.core.exception.PasswordInitException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

@Component
public class UserTokenHelper
{
    @Autowired
    private AuthAppService authAppService;
    
    @Resource
    private RestClient uamClientService;
    
    /**
     * Auth <br/>
     * 
     * @param auth
     * @return
     * @throws AuthFailedException
     */
    public UserToken checkTokenAndGetUserForV2(String authToken) throws AuthFailedException
    {
        String appId = authAppService.getCurrentAppId();
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (authApp == null)
        {
            throw new AuthFailedException();
        }
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", authToken);
        TextResponse response = uamClientService.performGetText("/api/v2/users/me", headerMap);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            UserToken rspUser = JsonUtils.stringToObject(content, UserToken.class);
            
            if (rspUser != null)
            {
                rspUser.setId(rspUser.getCloudUserId());
            }
            if (rspUser != null && rspUser.isNeedChangePassword())
            {
                throw new PasswordInitException("user has not inited password, operation not allowed");
            }
            return rspUser;
        }
        throw new AuthFailedException();
    }
    
    /**
     * Auth <br/>
     * 
     * @param auth
     * @return
     * @throws AuthFailedException
     */
    public UserToken imperfectCheckTokenAndGetUserForV2(String authToken) throws AuthFailedException
    {
        String appId = authAppService.getCurrentAppId();
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (authApp == null)
        {
            throw new AuthFailedException();
        }
        Map<String, String> headerMap = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headerMap.put("Authorization", authToken);
        TextResponse response = uamClientService.performGetText("/api/v2/users/me", headerMap);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            UserToken rspUser = JsonUtils.stringToObject(content, UserToken.class);
            
            if (rspUser != null)
            {
                rspUser.setId(rspUser.getCloudUserId());
            }
            return rspUser;
        }
        throw new AuthFailedException();
    }
}
