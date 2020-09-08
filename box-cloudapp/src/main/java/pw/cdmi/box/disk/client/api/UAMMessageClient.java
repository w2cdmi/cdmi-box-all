package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.message.Announcement;
import pw.cdmi.box.disk.client.domain.message.AnnouncementList;
import pw.cdmi.box.disk.client.domain.message.ListAnnouncementRequest;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class UAMMessageClient extends BasicMessageClient
{
    public UAMMessageClient(RestClient uamClientService)
    {
        super(uamClientService);
    }
    
    public Announcement getAnnouncement(String token, long announcementId) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_ANNOUNCEMENT);
        uri.append('/');
        uri.append(announcementId);
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        TextResponse response = this.getRestClient().performGetText(uri.toString(), headers);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), Announcement.class);
    }
    
    /**
     * 
     * @param token
     * @param request
     * @return
     * @throws RestException
     */
    public AnnouncementList listAnnouncement(String token, ListAnnouncementRequest request)
        throws RestException
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_ANNOUNCEMENT);
        uri.append("/items");
        TextResponse response = this.getRestClient().performJsonPostTextResponse(uri.toString(), headers, request);
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), AnnouncementList.class);
    }
}
