package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.share.domain.MySharesPage;
import pw.cdmi.box.disk.share.domain.RestListShareResourceRequestV2;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class MySharesClient
{
    private static final String PATH_SEND_SHARES = "/api/v2/shares/distributed";
    
    private RestClient ufmClientService;
    
    public MySharesClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public MySharesPage getMySharesPage(String token, RestListShareResourceRequestV2 req)
        throws RestException
    {
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", token);
        TextResponse response = ufmClientService.performJsonPostTextResponse(PATH_SEND_SHARES, headerMap, req);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, MySharesPage.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
}
