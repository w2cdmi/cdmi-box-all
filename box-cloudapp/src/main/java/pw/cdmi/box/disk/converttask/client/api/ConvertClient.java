package pw.cdmi.box.disk.converttask.client.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.converttask.client.domain.ConvertInfo;
import pw.cdmi.box.disk.converttask.client.domain.DeleteConvertRequest;
import pw.cdmi.box.disk.converttask.client.domain.RetryConvertRequest;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class ConvertClient
{
    
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertClient.class);
	
    private RestClient ufmClientService;
    
    private UserTokenManager userTokenManager;
    
    public ConvertClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    public ConvertInfo queryDoingConvert(String token, long ownerId,int spaceType,Integer curpage,Integer size)
        throws RestException
    {
    	String action="doing";
        String uri = Constants.CONVERT_TASK + '/' + action + '/' +ownerId;
        
        LOGGER.info("queryDoingConvert==>uri:"+uri);
        
        Map<String, String> headerMap = new HashMap<String, String>(4);
        headerMap.put("Authorization", token);
        headerMap.put("spaceType", String.valueOf(spaceType));
        headerMap.put("curpage", String.valueOf(curpage));
        headerMap.put("size", String.valueOf(size));
        TextResponse response = ufmClientService.performGetText(uri, headerMap);
        
        LOGGER.info("queryDoingConvert==>response:"+response.getResponseBody());
        
        if (response.getStatusCode() == 200)
        {
        	ConvertInfo DoingConvertInfo = JsonUtils.stringToObject(response.getResponseBody(),
        			ConvertInfo.class);
                return DoingConvertInfo;
        }
            
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            throw exception;
    }
    
    
    public ConvertInfo queryDoneConvert(String token, long ownerId,int spaceType,Integer curpage,Integer size)
            throws RestException
        {
    	    String action="done";
            String uri = Constants.CONVERT_TASK + '/' + action + '/' +ownerId;
            
            LOGGER.info("queryDoneConvert==>uri:"+uri);
            
            Map<String, String> headerMap = new HashMap<String, String>(4);
            headerMap.put("Authorization", token);
            headerMap.put("spaceType", String.valueOf(spaceType));
            headerMap.put("curpage", String.valueOf(curpage));
            headerMap.put("size", String.valueOf(size));
            TextResponse response = ufmClientService.performGetText(uri, headerMap);
            
            LOGGER.info("queryDoneConvert==>response:"+response.getResponseBody());
            
            if (response.getStatusCode() == 200)
            {
            	ConvertInfo DoingConvertInfo = JsonUtils.stringToObject(response.getResponseBody(),
            			ConvertInfo.class);
                    return DoingConvertInfo;
            }
                
                RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
                throw exception;
        }
    
    public String deleteDoneConvert(String token,DeleteConvertRequest request,int spaceType)
            throws RestException
        {
            String uri = Constants.CONVERT_TASK  ;
            
            LOGGER.info("deleteDoneConvert==>uri:"+uri);
            
            Map<String, String> headerMap = new HashMap<String, String>(2);
            headerMap.put("Authorization", token);
            headerMap.put("spaceType", String.valueOf(spaceType));
            TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
            
            LOGGER.info("deleteDoneConvert==>response:"+response.getResponseBody());
            
            if (response.getStatusCode() == 200)
            {
            	 return String.valueOf(response.getStatusCode());
            }
            return null;
        }
    
    public String retryDingConvert(String token,RetryConvertRequest request,int spaceType,String taskId)
            throws RestException
        {
            String uri = Constants.CONVERT_TASK  + '/' + taskId;
            
            LOGGER.info("retryDingConvert==>uri:"+uri);
            
            Map<String, String> headerMap = new HashMap<String, String>(2);
            headerMap.put("Authorization", token);
            headerMap.put("spaceType", String.valueOf(spaceType));
            TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, request);
            
            LOGGER.info("retryDingConvert==>response:"+response.getResponseBody());
            
            if (response.getStatusCode() == 200)
            {
            	 return String.valueOf(response.getStatusCode());
            }
            return null;
        }
    
    
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
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
