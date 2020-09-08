package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.message.ListMessageRequest;
import pw.cdmi.box.disk.client.domain.message.MessageList;
import pw.cdmi.box.disk.client.domain.message.UpdateMessageRequest;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class BasicMessageClient
{
    private RestClient restClient;
    
    public BasicMessageClient(RestClient restClient)
    {
        this.restClient = restClient;
    }
    
    public MessageList listMessage(String token, ListMessageRequest request) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_MESSAGE);
        uri.append("/items");
        
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        
        TextResponse response = restClient.performJsonPostTextResponse(uri.toString(), headers, request);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), MessageList.class);
    }
    
    
    public RestClient getRestClient()
    {
        return this.restClient;
    }
    
    public void updateMessageStatus(String token, long messageId, UpdateMessageRequest request)
        throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_MESSAGE);
        uri.append('/');
        uri.append(messageId);
        
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        
        TextResponse response = restClient.performJsonPutTextResponse(uri.toString(), headers, request);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
    }
}
