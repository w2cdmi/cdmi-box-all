package pw.cdmi.box.uam.httpclient.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogListRsp;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

public class UserLogClient
{
    
    private static Logger logger = LoggerFactory.getLogger(UserLogClient.class);
    
    private static final String USER_LOG_API_PATH = "/api/v2/userlogs";
    
    private RestClient ufmClientService;
    
    public UserLogClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public UserLogListRsp getUserLog(UserLogListReq request, AuthApp authApp)
    {
        Map<String, String> headers = new HashMap<String, String>(16);
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String sign = SignatureUtils.getSignature(authApp.getUfmSecretKey(), dateStr);
        String authorization = "application," + authApp.getUfmAccessKeyId() + ',' + sign;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        try
        {
            TextResponse response = ufmClientService.performJsonPostTextResponse(USER_LOG_API_PATH,
                headers,
                request);
            if (response.getStatusCode() == 200)
            {
                UserLogListRsp result = JsonUtils.stringToObject(response.getResponseBody(),
                    UserLogListRsp.class);
                return result;
            }
            else
            {
                logger.error("get user log fail:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("get user log fail:", e);
            return null;
        }
    }
    
}
