package com.huawei.sharedrive.uam.httpclient.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountModifyRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountResponse;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

public class EnterpriseHttpClient
{
    private static Logger logger = LoggerFactory.getLogger(EnterpriseHttpClient.class);
    
    private RestClient ufmClientService;
    
    private static final String ACCOUNT_URL = "api/v2/accounts";
    
    public EnterpriseHttpClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public RestEnterpriseAccountResponse getAccountInfo(AuthApp authApp, RestEnterpriseAccountRequest request)
    {
        Map<String, String> headers = getAppAuthHeader(authApp.getUfmAccessKeyId(), authApp.getUfmSecretKey());
        TextResponse response;
        try
        {
            response = ufmClientService.performJsonPostTextResponse(ACCOUNT_URL, headers, request);
            
            if (response.getStatusCode() != HttpStatus.SC_CREATED)
            {
                logger.error("Create appliance account information fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("Create appliance account information fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RestEnterpriseAccountResponse result = JsonUtils.stringToObject(responseStr,
            RestEnterpriseAccountResponse.class);
        return result;
    }
    
    public RestEnterpriseAccountResponse modifyAccount(AuthApp authApp,
        RestEnterpriseAccountModifyRequest request, Long accountId)
    {
        
        Map<String, String> headers = getAppAuthHeader(authApp.getUfmAccessKeyId(), authApp.getUfmSecretKey());
        TextResponse response;
        try
        {
            response = ufmClientService.performJsonPutTextResponse(ACCOUNT_URL + "/" + accountId,
                headers,
                request);
            
            if (response.getStatusCode() != HttpStatus.SC_OK)
            {
                logger.error("Modify appliance account information fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("Modify appliance account information fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RestEnterpriseAccountResponse result = JsonUtils.stringToObject(responseStr,
            RestEnterpriseAccountResponse.class);
        return result;
    }
    
    public RestEnterpriseAccountResponse getAccountById(AuthApp authApp, Long id)
    {
        
        Map<String, String> headers = getAppAuthHeader(authApp.getUfmAccessKeyId(), authApp.getUfmSecretKey());
        TextResponse response;
        try
        {
            response = ufmClientService.performGetText(ACCOUNT_URL + "/" + id, headers);
            if (response.getStatusCode() != HttpStatus.SC_OK)
            {
                logger.error("Get appliance account information fail code:" + response.getStatusCode());
                return null;
            }
        }
        catch (ServiceException e)
        {
            logger.error("Get appliance account information fail", e);
            return null;
        }
        String responseStr = "";
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        RestEnterpriseAccountResponse result = JsonUtils.stringToObject(responseStr,
            RestEnterpriseAccountResponse.class);
        return result;
    }
    
    private Map<String, String> getAppAuthHeader(String accessKeyId, String appSecretKey)
    {
        Map<String, String> headers = new HashMap<String, String>(16);
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(appSecretKey, dateStr);
        String authorization = "application," + accessKeyId + ',' + appSignatureKey;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);
        return headers;
    }
    
}
