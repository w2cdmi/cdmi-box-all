package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.mailmsg.MailMsg;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.request.RestMailMsgSetRequest;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class MailMsgClient
{
    private RestClient ufmClientService;
    
    public MailMsgClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public MailMsg setMailMsg(String token, long ownerId, long nodeId, RestMailMsgSetRequest rl)
    {
        Map<String, String> headers = assembleToken(token);
        String path = Constants.NODES_API_MAILMSG + ownerId + '/' + nodeId;
        TextResponse response = ufmClientService.performJsonPutTextResponse(path, headers, rl);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), MailMsg.class);
        }
        return null;
    }

    public MailMsg getMailMsg(String token, String source, long ownerId, long nodeId)
    {
        Map<String, String> headers = assembleToken(token);
        StringBuffer buffer = new StringBuffer(20);
        buffer.append(Constants.NODES_API_MAILMSG).append(ownerId).append('/');
        buffer.append(nodeId).append("?source=").append(source);
        
        TextResponse response = ufmClientService.performGetText(buffer.toString(), headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), MailMsg.class);
        }
        
        return null;
    }
    
    private Map<String, String> assembleToken(String token)
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put(RestConstants.HEADER_AUTHORIZATION, token);
        return headers;
    }
}
