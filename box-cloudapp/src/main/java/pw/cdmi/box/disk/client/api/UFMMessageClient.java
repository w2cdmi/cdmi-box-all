package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.message.MessageListenUrl;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class UFMMessageClient extends BasicMessageClient
{
    
    public UFMMessageClient(RestClient ufmClientService)
    {
        super(ufmClientService);
    }
    
    public void deleteMessage(String token, long messageId) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_MESSAGE);
        uri.append('/');
        uri.append(messageId);
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        TextResponse response = this.getRestClient().performDelete(uri.toString(), headers);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
    }
    
    public MessageListenUrl getMessageListener(String token) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_MESSAGE);
        uri.append("/listener");
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        TextResponse response = this.getRestClient().performGetText(uri.toString(), headers);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), MessageListenUrl.class);
    }
}
