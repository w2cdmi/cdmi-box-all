package pw.cdmi.box.uam.httpclient.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.uam.core.RankRequest;
import pw.cdmi.box.uam.core.RankResponse;
import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountCryptUtil;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.domain.RestUserCreateRequest;
import pw.cdmi.box.uam.httpclient.rest.common.Constants;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

public class RegionClient
{
    
    private static Logger logger = LoggerFactory.getLogger(RegionClient.class);
    
    private static final String USER_URL = "api/v2/users";
    
    private static final String TEAM_URL = "api/v2/teamspaces";
    
    private RestClient ufmClientService;
    
    public RegionClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public RankResponse rankUsers(EnterpriseAccount enterprise, RankRequest rankRequest)
    {
        Map<String, String> headers = assembleAccountToken(enterprise);
        TextResponse response;
        String url = USER_URL + "/items";
        try
        {
            response = ufmClientService.performJsonPostTextResponse(url, headers, rankRequest);
            
            if (response.getStatusCode() != HttpStatus.OK.value())
            {
                logger.error("rank users fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error(" fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RankResponse result = JsonUtils.stringToObject(responseStr, RankResponse.class);
        return result;
    }
    
    public RankResponse rankTeams(AuthApp authApp, RankRequest rankRequest)
    {
        Map<String, String> headers = getAppAtuhHeader("app",
            authApp.getUfmAccessKeyId(),
            authApp.getUfmSecretKey());
        TextResponse response;
        String url = TEAM_URL + "/all";
        try
        {
            response = ufmClientService.performJsonPostTextResponse(url, headers, rankRequest);
            
            if (response.getStatusCode() != HttpStatus.OK.value())
            {
                logger.error("rank users fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error(" fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RankResponse result = JsonUtils.stringToObject(responseStr, RankResponse.class);
        return result;
    }
    
    public RestUserCreateRequest getUserInfo(long cloudUserId, EnterpriseAccount enterpriseAccount)
    {
        String sk = EnterpriseAccountCryptUtil.decodeSecretKey(enterpriseAccount);
        Map<String, String> headers = getAppAtuhHeader("account", enterpriseAccount.getAccessKeyId(), sk);
        TextResponse response;
        try
        {
            response = ufmClientService.performGetText(USER_URL + "/" + cloudUserId, headers);
            if (response.getStatusCode() != 200)
            {
                logger.error("get cloud user space fail code:" + response.getStatusCode() + " cloudUserId:"
                    + cloudUserId);
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("get cloud user space fail", e);
            return null;
        }
        RestUserCreateRequest result = JsonUtils.stringToObject(response.getResponseBody(),
            RestUserCreateRequest.class);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<RestRegionInfo> getRegionInfo(AuthApp authApp)
    {
        String date = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        Map<String, String> headerMap = new HashMap<String, String>(16);
        String authorization = "application," + authApp.getUfmAccessKeyId() + ','
            + SignatureUtils.getSignature(authApp.getUfmSecretKey(), date);
        headerMap.put("authorization", authorization);
        headerMap.put("Date", date);
        TextResponse response = ufmClientService.performGetText(Constants.RESOURCE_REGIONLIST_V2, headerMap);
        String content = response.getResponseBody();
        List<RestRegionInfo> regionInfo;
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            regionInfo = (List<RestRegionInfo>) JsonUtils.stringToList(content,
                List.class,
                RestRegionInfo.class);
        }
        else
        {
            regionInfo = new ArrayList<RestRegionInfo>(0);
        }
        return regionInfo;
    }
    
    private Map<String, String> getAppAtuhHeader(String authType, String accessKeyId, String appSecretKey)
    {
        Map<String, String> headers = new HashMap<String, String>(16);
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(appSecretKey, dateStr);
        String authorization = authType + "," + accessKeyId + ',' + appSignatureKey;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        return headers;
    }
    
    private Map<String, String> assembleAccountToken(EnterpriseAccount enterprise)
    {
        String decodedKey = EnterpriseAccountCryptUtil.decodeSecretKey(enterprise);
        Map<String, String> headers = new HashMap<String, String>(16);
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String sign = SignatureUtils.getSignature(decodedKey, dateStr);
        String authorization = "account," + enterprise.getAccessKeyId() + ',' + sign;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        
        return headers;
    }
}
