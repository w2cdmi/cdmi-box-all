package com.huawei.sharedrive.uam.httpclient.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.httpclient.rest.common.Constants;
import com.huawei.sharedrive.uam.openapi.domain.RestAppStatisticsRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestAppStatisticsResponse;
import com.huawei.sharedrive.uam.statistics.service.StatisticsAccesskeyService;

import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.StatisticsConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.JsonUtils;

public class StatisticsHttpClient
{
    private static Logger logger = LoggerFactory.getLogger(StatisticsHttpClient.class);
    
    private RestClient ufmClientService;
    
    private StatisticsAccesskeyService statisticsAccesskeyService;
    
    private static final String STATISTICS_URL = "api/v2/statistics/";
    
    public StatisticsHttpClient(RestClient ufmClientService,
        StatisticsAccesskeyService statisticsAccesskeyService)
    {
        this.ufmClientService = ufmClientService;
        this.statisticsAccesskeyService = statisticsAccesskeyService;
    }
    
    public RestAppStatisticsResponse getStatisticsInfo(String appId, EnterpriseAccount enterprise,
        RestAppStatisticsRequest request)
    {
        Map<String, String> headers = assembleAccountToken(enterprise);
        TextResponse response;
        String url = STATISTICS_URL + "info";
        try
        {
            response = ufmClientService.performJsonPostTextResponse(url, headers, request);
            
            if (response.getStatusCode() != HttpStatus.SC_OK)
            {
                logger.error("get appliance statistic information fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("get appliance statistic information fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RestAppStatisticsResponse result = JsonUtils.stringToObject(responseStr,
            RestAppStatisticsResponse.class);
        return result;
    }
    
    private Map<String, String> getAppAuthHeader(StatisticsConfig config)
    {
        Map<String, String> headers = new HashMap<String, String>(16);
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(config.getSecretKey(), dateStr);
        String authorization = "system," + config.getAccessKey() + ',' + appSignatureKey;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        return headers;
    }
    
    @SuppressWarnings("unchecked")
    public List<RestRegionInfo> getRegionInfo()
    {
        Map<String, String> headerMap = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        TextResponse response = ufmClientService.performGetText(Constants.RESOURCE_REGIONLIST_V2, headerMap);
        String content = response.getResponseBody();
        List<RestRegionInfo> regionInfo = new ArrayList<RestRegionInfo>(10);
        if (response.getStatusCode() == HttpStatus.SC_OK)
        {
            regionInfo = (List<RestRegionInfo>) JsonUtils.stringToList(content,
                List.class,
                RestRegionInfo.class);
        }
        return regionInfo;
    }
    
    private Map<String, String> assembleAccountToken(EnterpriseAccount enterprise)
    {
        String decodedKey = EDToolsEnhance.decode(enterprise.getSecretKey(),
            enterprise.getSecretKeyEncodeKey());
        Map<String, String> headers = new HashMap<String, String>(16);
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String sign = SignatureUtils.getSignature(decodedKey, dateStr);
        String authorization = "account," + enterprise.getAccessKeyId() + ',' + sign;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        
        return headers;
    }
}
