package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.api.domain.UpdateTerminalRequest;
import pw.cdmi.box.disk.client.domain.user.ListTerminalRequest;
import pw.cdmi.box.disk.client.domain.user.ListTerminalResonse;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class TerminalClient
{
    
    private RestClient uamClientService;
    
    private static final String TERMINAL_LIST_URL = "/api/v2/terminal/items";
    
    private static final String TERMINAL_URL = "/api/v2/terminal/";
    
    public TerminalClient(RestClient uamClientService)
    {
        this.uamClientService = uamClientService;
    }
    
    public ListTerminalResonse listTerminal(String token, ListTerminalRequest listTerminalRequest)
        throws RestException
    {
        String uri = TERMINAL_LIST_URL;
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        
        TextResponse response = uamClientService.performJsonPostTextResponse(uri,
            headerMap,
            listTerminalRequest);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, ListTerminalResonse.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public void updateTerminalStatus(String token, String deviceSn, Byte status) throws RestException
    {
        String uri = TERMINAL_URL + "/status";
        
        Map<String, String> headerMap = new HashMap<String, String>(2);
        headerMap.put("Authorization", token);
        UpdateTerminalRequest request = new UpdateTerminalRequest();
        request.setDeviceSn(deviceSn);
        request.setStatus(status);
        TextResponse response = uamClientService.performJsonPutTextResponse(uri, headerMap, request);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
}
