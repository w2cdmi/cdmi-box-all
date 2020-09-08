package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.api.domain.SecretStaff;
import pw.cdmi.box.disk.client.domain.user.RestAccountConfigList;
import pw.cdmi.box.disk.client.domain.user.RestUserConfigList;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class AccountConfigClient {
	
	@Autowired
	private UserTokenManager userTokenManager;
    
	
    private RestClient uamClientService;
    
    public AccountConfigClient(RestClient uamClientService)
    {
        this.uamClientService = uamClientService;
        getUserTokenManager();
    }
    
    public RestAccountConfigList getAccountConfig(long accountId, String token) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_ACCOUNTS);
        uri.append(accountId);
        uri.append("/config"); 
        
        Map<String, String> headers = assembleToken();
        TextResponse response = uamClientService.performGetText(uri.toString(), headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), RestAccountConfigList.class);
    }
    
    
    public List<?> getStaffSecret(long accountId, String token) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_ACCOUNTS);
        uri.append(accountId);
        uri.append("/staffSecret"); 
        
        Map<String, String> headers = assembleToken();
        
        TextResponse response = uamClientService.performGetText(uri.toString(), headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToList(response.getResponseBody(), List.class, SecretStaff.class);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }

    public UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }


}
