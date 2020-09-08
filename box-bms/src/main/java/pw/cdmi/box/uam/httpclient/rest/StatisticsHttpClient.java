package pw.cdmi.box.uam.httpclient.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.uam.enterprise.domain.EnterpriseAccountCryptUtil;
import pw.cdmi.box.uam.exception.InternalServerErrorException;
import pw.cdmi.box.uam.exception.RestException;
import pw.cdmi.box.uam.httpclient.domain.RestAppStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.RestAppStatisticsResponse;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.httpclient.domain.RestUserCurrentStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.RestUserHistoryStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.UserCurrentStatisticsList;
import pw.cdmi.box.uam.httpclient.domain.UserHistoryStatisticsList;
import pw.cdmi.box.uam.httpclient.domain.statistics.StatisticsExcelExportRequest;
import pw.cdmi.box.uam.httpclient.rest.common.Constants;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ConcStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.FileStoreHistoryRequest;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.NodeCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.NodeHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectCurrentStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.ObjectHistoryStatisticsResponse;
import pw.cdmi.box.uam.statistics.domain.RestUserClusterStatisticsRequest;
import pw.cdmi.box.uam.statistics.domain.UserClusterStatisticsList;
import pw.cdmi.box.uam.statistics.service.StatisticsAccesskeyService;
import pw.cdmi.common.domain.StatisticsConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

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
    
    /**
     * 
     * @param fileCurrentRequest
     * @param authApp
     * @return
     * @throws RestException
     */
    public NodeCurrentStatisticsResponse getCurrentNodeData(NodeCurrentStatisticsRequest fileCurrentRequest)
        throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("nodes/current");
        TextResponse response;
        response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, fileCurrentRequest);
        String body = response.getResponseBody();
        int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
        {
            logger.error("Fail to get current node data ,status code:" + statusCode, statusCode);
            RestException restException = JsonUtils.stringToObject(body, RestException.class);
            throw restException;
        }
        NodeCurrentStatisticsResponse statisticsResponse = JsonUtils.stringToObject(body,
            NodeCurrentStatisticsResponse.class);
        return statisticsResponse;
    }
    
    public ObjectHistoryStatisticsResponse getHistoryObjectData(
        ObjectHistoryStatisticsRequest statisticsRequest) throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("objects/history");
        TextResponse response;
        response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, statisticsRequest);
        String body = response.getResponseBody();
        int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
        {
            logger.error("Fail to get history objects node data,status code:" + statusCode, statusCode);
            RestException restException = JsonUtils.stringToObject(body, RestException.class);
            throw restException;
        }
        ObjectHistoryStatisticsResponse statisticsResponse = JsonUtils.stringToObject(body,
            ObjectHistoryStatisticsResponse.class);
        return statisticsResponse;
    }
    
    /**
     * 
     * @param fileCurrentRequest
     * @param authApp
     * @return
     * @throws RestException
     */
    public ObjectCurrentStatisticsResponse getCurrentObjectData(
        ObjectCurrentStatisticsRequest fileCurrentRequest) throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("objects/current");
        TextResponse response;
        response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, fileCurrentRequest);
        String body = response.getResponseBody();
        int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
        {
            logger.error("Fail to get current objects node data,status code:" + statusCode, statusCode);
            RestException restException = JsonUtils.stringToObject(body, RestException.class);
            throw restException;
        }
        ObjectCurrentStatisticsResponse statisticsResponse = JsonUtils.stringToObject(body,
            ObjectCurrentStatisticsResponse.class);
        return statisticsResponse;
    }
    
    /**
     * 
     * @param restStatistiscRequest
     * @param authApp
     * @return
     * @throws RestException
     */
    public UserCurrentStatisticsList getCurrUserAccount(
        RestUserCurrentStatisticsRequest restStatistiscRequest, AuthApp authApp) throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        
        String url = STATISTICS_URL + "users/current";
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url,
            headers,
            restStatistiscRequest);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("get current users amount fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        UserCurrentStatisticsList result = JsonUtils.stringToObject(responseStr,
            UserCurrentStatisticsList.class);
        return result;
    }
    
    public UserClusterStatisticsList getUserStoreCurrenData(RestUserClusterStatisticsRequest interzoneRequest)
        throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("/users/cluster");
        
        TextResponse response = null;
        try
        {
            response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, interzoneRequest);
            
            String responseStr = response.getResponseBody();
            if (response.getStatusCode() != HttpStatus.SC_OK)
            {
                logger.error("get current users amount fail code:" + response.getStatusCode());
                RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
                throw exception;
            }
            UserClusterStatisticsList result = JsonUtils.stringToObject(responseStr,
                UserClusterStatisticsList.class);
            return result;
        }
        finally
        {
            IOUtils.closeQuietly(response);
        }
    }
    
    /**
     * 
     * @param restStatistiscRequest
     * @param authApp
     * @return
     * @throws RestException
     */
    public UserHistoryStatisticsList getHistUserAccount(
        RestUserHistoryStatisticsRequest restStatistiscRequest, AuthApp authApp) throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        
        if (StringUtils.isBlank(restStatistiscRequest.getAppId()))
        {
            restStatistiscRequest.setAppId(null);
        }
        
        String url = STATISTICS_URL + "users/history";
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url,
            headers,
            restStatistiscRequest);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("get history users amount fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        UserHistoryStatisticsList result = JsonUtils.stringToObject(responseStr,
            UserHistoryStatisticsList.class);
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
    
    public ConcStatisticsResponse getConcurrenceHistoryData(ConcStatisticsRequest concStatisticsRequest)
        throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("concurrence/history");
        TextResponse response = ufmClientService.performJsonPostTextResponse(url.toString(),
            headers,
            concStatisticsRequest);
        String body = response.getResponseBody();
        int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
        {
            logger.error("Fail to get history objects node data,status code:" + statusCode, statusCode);
            RestException restException = JsonUtils.stringToObject(body, RestException.class);
            throw restException;
        }
        ConcStatisticsResponse statisticsResponse = JsonUtils.stringToObject(body,
            ConcStatisticsResponse.class);
        return statisticsResponse;
    }
    
    public NodeHistoryStatisticsResponse getNodeHistoryObjectData(FileStoreHistoryRequest historyRequest)
        throws RestException
    {
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        StringBuffer url = new StringBuffer();
        url.append(STATISTICS_URL);
        url.append("nodes/history");
        TextResponse response;
        response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, historyRequest);
        String body = response.getResponseBody();
        int statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
        {
            logger.error("Fail to get history objects node data,status code:" + statusCode, statusCode);
            RestException restException = JsonUtils.stringToObject(body, RestException.class);
            throw restException;
        }
        NodeHistoryStatisticsResponse statisticsResponse = JsonUtils.stringToObject(body,
            NodeHistoryStatisticsResponse.class);
        return statisticsResponse;
    }
    
    @SuppressWarnings("unchecked")
    public List<RestRegionInfo> getRegionInfo()
    {
        Map<String, String> headerMap = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        TextResponse response = ufmClientService.performGetText(Constants.RESOURCE_REGIONLIST_V2, headerMap);
        String content = response.getResponseBody();
        List<RestRegionInfo> regionInfo;
        if (response.getStatusCode() == HttpStatus.SC_OK)
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
    
    /**
     * 
     * @param beginTime
     * @param endTime
     * @param type
     * @return
     * @throws IOException
     */
    public byte[] historyDataStatisticsExport(Long beginTime, Long endTime, String type) throws IOException
    {
        
        Map<String, String> headers = getAppAuthHeader(statisticsAccesskeyService.getStatisticsConfig());
        String url = STATISTICS_URL + "excel";
        StatisticsExcelExportRequest exportRequest = new StatisticsExcelExportRequest();
        exportRequest.setBeginTime(beginTime);
        exportRequest.setEndTime(endTime);
        exportRequest.setType(type);
        StreamResponse response = ufmClientService.performJsonPostStreamResponse(url, headers, exportRequest);
        if (response.getStatusCode() != 200)
        {
            throw new InternalServerErrorException();
        }
        InputStream input = null;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        try
        {
            input = response.getInputStream();
            if (input == null)
            {
                return null;
            }
            byte[] buff = new byte[1024];
            int rc;
            while (-1 != (rc = input.read(buff, 0, 1024)))
            {
                swapStream.write(buff, 0, rc);
            }
            return swapStream.toByteArray();
        }
        catch (IOException e)
        {
            logger.error("IOEXCEPTION", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(swapStream);
        }
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
