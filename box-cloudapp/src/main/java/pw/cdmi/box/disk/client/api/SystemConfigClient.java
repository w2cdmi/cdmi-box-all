package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.system.RestSystemConfig;
import pw.cdmi.box.disk.client.utils.Constants;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class SystemConfigClient
{
    
    private RestClient uamClientService;
    
    public SystemConfigClient(RestClient uamClientService)
    {
        this.uamClientService = uamClientService;
    }
    
    /**
     * 
     * @param option
     * @param authorization
     * @return
     * @throws RestException
     */
    @SuppressWarnings("unchecked")
    public List<RestSystemConfig> getSystemConfig(String option, String authorization) throws RestException
    {
        String url = Constants.RESOUCE_SYSTEM_CONFIG + "?option=" + option;
        
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", authorization);
        TextResponse response = uamClientService.performGetText(url, headers);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            List<RestSystemConfig> configList = (List<RestSystemConfig>) JsonUtils.stringToList(content,
                List.class,
                RestSystemConfig.class);
            return configList;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
}
