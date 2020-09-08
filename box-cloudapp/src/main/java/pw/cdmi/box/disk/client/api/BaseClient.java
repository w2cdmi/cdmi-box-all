package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.core.restrpc.RestClient;

public abstract class BaseClient
{
    private RestClient clientService;
    
    public BaseClient(RestClient clientService)
    {
        this.clientService = clientService;
    }
    
    public RestClient getClientService()
    {
        return this.clientService;
    }
    
    protected Map<String, String> createHeader(String token)
    {
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        return headerMap;
    }
}
