package pw.cdmi.box.disk.client.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.task.RequestAddAsyncTask;
import pw.cdmi.box.disk.client.domain.task.ResponseAddAsyncTask;
import pw.cdmi.box.disk.client.domain.task.ResponseGetTask;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

public class AsyncTaskClient
{
    
    public AsyncTaskClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    private static final String TYPE_COPY = "copy";
    
    private static final String PATH_TASK = "/api/v2/tasks/nodes";
    
    private RestClient ufmClientService;
    
    public ResponseAddAsyncTask addAsyncTask(String ufmToken, RequestAddAsyncTask restRequest)
        throws RestException
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headers.put(RestConstants.HEADER_AUTHORIZATION, ufmToken);
        fillSpecialLinkCopyHeader(restRequest, headers);
        TextResponse result = ufmClientService.performJsonPutTextResponse(PATH_TASK, headers, restRequest);
        if (result.getStatusCode() == HttpStatus.CREATED.value())
        {
            ResponseAddAsyncTask task = JsonUtils.stringToObject(result.getResponseBody(),
                ResponseAddAsyncTask.class);
            return task;
        }
        RestException restEx = JsonUtils.stringToObject(result.getResponseBody(), RestException.class);
        throw restEx;
    }
    
    public ResponseGetTask getTaskStatus(String ufmToken, String taskId) throws RestException
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headers.put(RestConstants.HEADER_AUTHORIZATION, ufmToken);
        
        TextResponse result = ufmClientService.performGetText(PATH_TASK + '/' + taskId, headers);
        if (result.getStatusCode() == HttpStatus.OK.value())
        {
            ResponseGetTask res = JsonUtils.stringToObject(result.getResponseBody(), ResponseGetTask.class);
            return res;
        }
        throw new InternalServerErrorException(result.getStatusCode() + "");
    }
    
    /**
     * 
     * @param restRequest
     * @param headers
     */
    private void fillSpecialLinkCopyHeader(RequestAddAsyncTask restRequest, Map<String, String> headers)
    {
        if (!restRequest.getType().equals(TYPE_COPY))
        {
            return;
        }
        if (null == restRequest.getLink())
        {
            return;
        }
        if (restRequest.getLink().getPlainAccessCode() != null)
        {
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String signAccessCode = SignatureUtils.getSignature(restRequest.getLink().getPlainAccessCode(),
                dateStr);
            restRequest.getLink().setPlainAccessCode(signAccessCode);
            headers.put(RestConstants.HEADER_DATE, dateStr);
        }
    }
    
}
