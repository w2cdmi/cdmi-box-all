package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.user.RequestCreateLdapUser;
import pw.cdmi.box.disk.client.domain.user.RestListDeptAndUserRequest;
import pw.cdmi.box.disk.client.domain.user.RestUserCreateRequest;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class UserClient
{
    
    private final static String URL_USER_LDAP_CREATE = "/api/v2/users/ldapuser";
    
    private final static String URL_GET_USERS_URL = "/api/v2/users/getUsersByDepId";
    
    private RestClient uamClientService;
    
    public UserClient(RestClient uamClientService)
    {
        this.uamClientService = uamClientService;
    }
    
    public UserToken createUserFromLdap(String token, String loginName) throws RestException
    {
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        RequestCreateLdapUser req = new RequestCreateLdapUser();
        req.setLoginName(loginName);
        TextResponse response = uamClientService.performJsonPostTextResponse(URL_USER_LDAP_CREATE,
            headerMap,
            req);
        
        if (response.getStatusCode() == HttpStatus.CREATED.value())
        {
            String content = response.getResponseBody();
            UserToken newLdapUser = JsonUtils.stringToObject(content, UserToken.class);
            return newLdapUser;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    
    public List<UserToken> getUsersByDepId(String token, String depId) throws RestException
    {
    	
    	Map<String, String> headerMap = new HashMap<String, String>(1);
    	headerMap.put("Authorization", token);
    	TextResponse response = uamClientService.performJsonPostTextResponse(URL_GET_USERS_URL,
    			headerMap,
    			depId);
    	
    	if (response.getStatusCode() == HttpStatus.OK.value())
    	{	
    		String responseBody = response.getResponseBody();
    		List<UserToken> users = (List<UserToken>) JsonUtils.stringToList(responseBody,List.class,UserToken.class);
    		
    		return users;
    	}
    	RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
    	throw exception;
    }
    
    /**
     * Access to user information
     * 
     * @param token
     * @param user
     * @return
     * @throws RestException
     */
    public RestUserCreateRequest getUserInfo(Long userId, String authorization) throws RestException
    {
        String url = Constants.RESOURCE_USER + "/me";
        
        Map<String, String> headers = new HashMap<String, String>(16);
        headers.put("Authorization", authorization);
        TextResponse response = uamClientService.performGetText(url, headers);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestUserCreateRequest userInfo = JsonUtils.stringToObject(content, RestUserCreateRequest.class);
            return userInfo;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * Gets the current user information
     * 
     * @param token
     * @param user
     * @return
     * @throws RestException
     */
    public RestUserCreateRequest getUserInfoByToken(String authorization) throws RestException
    {
        String url = Constants.RESOURCE_USER + "/me";
        
        Map<String, String> headers = new HashMap<String, String>(16);
        headers.put("Authorization", authorization);
        TextResponse response = uamClientService.performGetText(url, headers);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestUserCreateRequest userInfo = JsonUtils.stringToObject(content, RestUserCreateRequest.class);
            return userInfo;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * Update user information
     * 
     * @param token
     * @param user
     * @return
     * @throws RestException
     */
    public RestUserCreateRequest updateUser(String authorization, Long userId, RestUserCreateRequest user)
        throws RestException
    {
        String url = Constants.RESOURCE_USER + "/" + userId;
        
        Map<String, String> headers = new HashMap<String, String>(16);
        headers.put("Authorization", authorization);
        TextResponse response = uamClientService.performJsonPutTextResponse(url, headers, user);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestUserCreateRequest userInfo = JsonUtils.stringToObject(content, RestUserCreateRequest.class);
            return userInfo;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }

	public String listDept(String token) {
		String url = Constants.RESOURCE_USER+"/listDept";
		Map<String, String> headers = new HashMap<String, String>(16);
		headers.put("Authorization", token);
        TextResponse response = uamClientService.performGetText(url, headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return content;
        }
		return null;
		
	}
	
	public String listUsersAndDepByPid(String token,String id) {
		String url = Constants.RESOURCE_USER+"/listDepAndUsers";
		Map<String, String> headers = new HashMap<String, String>(16);
		headers.put("Authorization", token);
		RestListDeptAndUserRequest RestRequest=new RestListDeptAndUserRequest();
		RestRequest.setDeptId(Long.parseLong(id));
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, RestRequest);
		if (response.getStatusCode() == HttpStatus.OK.value())
		{
			String content = response.getResponseBody();
			return content;
		}
		return null;
		
	}


	public boolean checkOrgEnabled(String token) {
		String url = Constants.RESOURCE_USER+"/checkOrgIsEnabled";
		Map<String, String> headers = new HashMap<String, String>(16);
		headers.put("Authorization", token);
		TextResponse response = uamClientService.performGetText(url, headers);
		if (response.getStatusCode() == HttpStatus.OK.value())
		{
			String content = response.getResponseBody();
			return StringUtils.equals(content, "true");
		}
		
		return false;
		
	}

	public long getDepMamagerCloudUserId(String token) {
		// TODO Auto-generated method stub
		String url = Constants.RESOURCE_USER+"/getDepMamagerCloudUserId";
		Map<String, String> headers = new HashMap<String, String>(16);
		headers.put("Authorization", token);
		TextResponse response = uamClientService.performGetText(url, headers);
		if (response.getStatusCode() == HttpStatus.OK.value())
		{
			String content = response.getResponseBody();
			return JsonUtils.stringToObject(content, Long.class);
		}
		
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
	    throw exception;
	}
	public long getDepAdminCloudUserId(String token) {
		// TODO Auto-generated method stub
		String url = Constants.RESOURCE_USER+"/getDepAdminCloudUserId";
		Map<String, String> headers = new HashMap<String, String>(16);
		headers.put("Authorization", token);
		TextResponse response = uamClientService.performGetText(url, headers);
		if (response.getStatusCode() == HttpStatus.OK.value())
		{
			String content = response.getResponseBody();
			return JsonUtils.stringToObject(content, Long.class);
		}
		
		RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
	    throw exception;
	}
}
