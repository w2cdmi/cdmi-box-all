package com.huawei.sharedrive.uam.httpclient.rest;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.core.RankResponse;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.MigrationRecord;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationResponseDto;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.httpclient.rest.common.Constants;
import com.huawei.sharedrive.uam.openapi.domain.RestUserCreateRequest;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import pw.cdmi.box.http.request.RestRegionInfo;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

import java.util.*;

public class UserHttpClient
{
    
    private static Logger logger = LoggerFactory.getLogger(UserHttpClient.class);
    
    private static final String USER_URL = "api/v2/users";
    
    private static final String TEAM_URL = "api/v2/teamspaces";
    
    private static final String GET_USER_URL = "/api/v2/users/details";
    
    private static final String DATA_MIGRATION = "/api/v2/migration";
    
    private RestClient ufmClientService;
    
    public UserHttpClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public long createUser(UserAccount userAccount, EnterpriseUser enterpriseUser, EnterpriseAccount enterpriseAccount, boolean isThrowLicense) throws InternalServerErrorException
    {
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        RestUserCreateRequest restUserCreateRequest = getUserCreateRequest(enterpriseUserAccount);
        //创建的为临时账号时候，云存储空间大小设置为0
        if(enterpriseUser.getType() == EnterpriseUser.TYPE_TEMPORARY){
        	restUserCreateRequest.setSpaceUsed((long)(0));
        	restUserCreateRequest.setSpaceQuota((long)(0));
        }

        return createUser(enterpriseAccount, restUserCreateRequest, isThrowLicense);
    }

    public long createUser(EnterpriseAccount enterpriseAccount, RestUserCreateRequest createRequest, boolean isThrowLicense) {
        String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(), enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(), decodedKey);
        TextResponse response = ufmClientService.performJsonPostTextResponse(USER_URL, headers, createRequest);
        String responseStr = "";
        if (response.getStatusCode() == 409) {
            logger.warn("conflict user 409, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
            TextResponse responseSel = ufmClientService.performJsonPostTextResponse(GET_USER_URL, headers, createRequest);
            if (responseSel.getStatusCode() != 200) {
                logger.error("find user failed " + responseSel.getStatusCode());
                throw new InternalServerErrorException();
            }
            responseStr = responseSel.getResponseBody();
        } else if (response.getStatusCode() == 412) {
            logger.warn("exceed max user 412, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
            throw new ExceedQuotaException();
        } else if (response.getStatusCode() != 201) {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
            if (null == exception) {
                logger.error("exception is null, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
                throw new InternalServerErrorException();
            }
            if (ErrorCode.LICENSE_FORBIDDEN.getCode().equals(exception.getCode()) && isThrowLicense) {
                logger.error("License is invalid, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
                throw new LicenseException();
            }
            logger.error("create user failed, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
            throw new InternalServerErrorException();
        }
        if (StringUtils.isBlank(responseStr)) {
            responseStr = response.getResponseBody();
        }
        Map<String, Object> responseMap = JsonUtils.stringToMap(responseStr);
        Object object = null;
        if (null != responseMap) {
            object = responseMap.get("id");
        }

        if (null == object) {
            logger.error("user id is null, userId={}, loginName={}", createRequest.getId(), createRequest.getLoginName());
            throw new InternalServerErrorException();
        }
        long cloudUserId;
        try {
            cloudUserId = Long.parseLong(object.toString());
        } catch (NumberFormatException e) {
            throw new InvalidParamterException(e);
        }
        return cloudUserId;
    }
    
    
    
    public long createWxUserAccount(WxUser wxUser, AuthApp authApp) throws InternalServerErrorException
        {
        RestUserCreateRequest restUserCreateRequest = new RestUserCreateRequest();
        restUserCreateRequest.setLoginName(wxUser.getUnionId());
        restUserCreateRequest.setSpaceQuota((long)104857600);
        restUserCreateRequest.setName(wxUser.getNickName());
        restUserCreateRequest.setDescription(authApp.getAuthAppId());
        restUserCreateRequest.setEmail(wxUser.getEmail());
        restUserCreateRequest.setStatus((byte) 0);
        restUserCreateRequest.setMaxVersions(200);
        restUserCreateRequest.setSpaceUsed(0L);
        String date = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        Map<String, String> headerMap = new HashMap<String, String>(16);
        String authorization = "application," + authApp.getUfmAccessKeyId() + ',' + SignatureUtils.getSignature(authApp.getUfmSecretKey(), date);
        headerMap.put("authorization", authorization);
        headerMap.put("Date", date);
        TextResponse response = ufmClientService.performJsonPostTextResponse(USER_URL+"/addPersonUser",headerMap,restUserCreateRequest);
        String responseStr = "";
        if (response.getStatusCode() == 409)
        {
            logger.warn("conflict user 409, userId={}, loginName={}", restUserCreateRequest.getId(), restUserCreateRequest.getLoginName());
            TextResponse responseSel = ufmClientService.performJsonPostTextResponse(GET_USER_URL, headerMap, restUserCreateRequest);
            if (responseSel.getStatusCode() != 200) {
                logger.error("find user failed " + responseSel.getStatusCode());
                throw new InternalServerErrorException();
            }
            responseStr = responseSel.getResponseBody();
        }
        else if (response.getStatusCode() == 412)
        {
                logger.warn("exceed max user 412");
                throw new ExceedQuotaException();
        }
        else if (response.getStatusCode() != 201)
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            if (null == exception)
            {
                logger.error("exception is null");
                throw new InternalServerErrorException();
            }
            logger.error("create user failed " + exception.getCode() + " msg:" + exception.getMessage());
            throw new InternalServerErrorException();
        }
        if (StringUtils.isBlank(responseStr))
        {
            responseStr = response.getResponseBody();
        }
        Map<String, Object> responseMap = JsonUtils.stringToMap(responseStr);
        Object object = null;
        if (null != responseMap)
        {
            object = responseMap.get("id");
        }
        
        if (null == object)
        {
            logger.error("user id is null");
            throw new InternalServerErrorException();
        }
        long cloudUserId;
        try
        {
            cloudUserId = Long.parseLong(object.toString());
            wxUser.setRegionId(Integer.parseInt(responseMap.get("regionId").toString()));
            wxUser.setQuota(Long.parseLong(responseMap.get("spaceQuota").toString()));
            wxUser.setCloudUserId(cloudUserId);
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException(e);
        }
        return cloudUserId;
          
        }
    
    public long updateWxUserAccount(WxUser wxUser, AuthApp authApp) throws InternalServerErrorException
    {
        RestUserCreateRequest restUserCreateRequest = new RestUserCreateRequest();
        restUserCreateRequest.setLoginName(wxUser.getUnionId());
        restUserCreateRequest.setSpaceQuota((long)104857600 + wxUser.getQuota());
        restUserCreateRequest.setName(wxUser.getNickName());
        restUserCreateRequest.setStatus((byte) 0);
        restUserCreateRequest.setMaxVersions(200);
        String date = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        Map<String, String> headerMap = new HashMap<String, String>(16);
        String authorization = "application," + authApp.getUfmAccessKeyId() + ',' + SignatureUtils.getSignature(authApp.getUfmSecretKey(), date);
        headerMap.put("authorization", authorization);
        headerMap.put("Date", date);
        TextResponse response = ufmClientService.performJsonPutTextResponse(USER_URL + "/" + wxUser.getCloudUserId() + "/update", headerMap, restUserCreateRequest);
        if (response.getStatusCode() == 409)
        {
            logger.warn("conflict user 409");
            throw new InternalServerErrorException();
        }
        else if (response.getStatusCode() == 412)
        {
                logger.warn("exceed max user 412");
                throw new ExceedQuotaException();
        }
        return 0;
    }
    
    public void updateUser(UserAccount userAccount, EnterpriseUser enterpriseUser,
                           EnterpriseAccount enterpriseAccount) throws InternalServerErrorException {
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        RestUserCreateRequest restUserCreateRequest = getUserCreateRequest(enterpriseUserAccount);
        String sk = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(),
                enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(), sk);
        TextResponse response = ufmClientService.performJsonPutTextResponse(USER_URL + "/"
                        + userAccount.getCloudUserId(),
                headers,
                restUserCreateRequest);
        if (response.getStatusCode() != 200) {
            logger.error("update user failed statusCode:" + response.getStatusCode() + " cloudUserId:"
                    + userAccount.getCloudUserId());
            throw new InternalServerErrorException();
        }
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
        RankResponse result = (RankResponse) JsonUtils.stringToObject(responseStr, RankResponse.class);
        return result;
    }
    
    public RankResponse rankTeams(AuthApp authApp, RankRequest rankRequest)
    {
        Map<String, String> headers = getAppAuthHeader("app",
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
        RankResponse result = (RankResponse) JsonUtils.stringToObject(responseStr, RankResponse.class);
        return result;
    }
    
    public RestUserCreateRequest getUserInfo(long cloudUserId, EnterpriseAccount enterpriseAccount)
    {
        String sk = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(),
            enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(), sk);
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
    
    
    public RestUserCreateRequest getPersonUserInfo(long cloudUserId,String token)
    {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", token);
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
    
    
    public RestUserCreateRequest deleteUserInfo(long cloudUserId, EnterpriseAccount enterpriseAccount)
    {
        String sk = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(),
            enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(), sk);
        TextResponse response;
        try
        {
            response = ufmClientService.performDelete(USER_URL + "/" + cloudUserId, headers);
            if (response.getStatusCode() != 200)
            {
                logger.error("get cloud user space fail code:" + response.getStatusCode());
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
        List<RestRegionInfo> regionInfo = new ArrayList<RestRegionInfo>(10);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            regionInfo = (List<RestRegionInfo>) JsonUtils.stringToList(content,
                List.class,
                RestRegionInfo.class);
        }
        return regionInfo;
    }
    
    private Map<String, String> getAppAuthHeader(String authType, String accessKeyId, String appSecretKey)
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
    
    private RestUserCreateRequest getUserCreateRequest(EnterpriseUserAccount user)
    {
        RestUserCreateRequest restUserCreateRequest = new RestUserCreateRequest();
        restUserCreateRequest.setEmail(user.getEmail());
        restUserCreateRequest.setId(user.getUserId());
        restUserCreateRequest.setLoginName(user.getName());
        restUserCreateRequest.setName(user.getAlias());
        restUserCreateRequest.setVersionFileSize(user.getVersionFileSize());
        restUserCreateRequest.setVersionFileType(user.getVersionFileType());
        try
        {
            restUserCreateRequest.setRegionId(Byte.parseByte(String.valueOf(user.getRegionId())));
        }
        catch (NumberFormatException e)
        {
            throw new InvalidParamterException(e);
        }
        if (user.getSpaceQuota() == 0 || null == user.getSpaceQuota())
        {
            restUserCreateRequest.setSpaceQuota(-1L);
        }
        else
        {
            restUserCreateRequest.setSpaceQuota(user.getSpaceQuota());
        }
        
        if (user.getMaxVersions() == 0 || null == user.getMaxVersions())
        {
            restUserCreateRequest.setMaxVersions(-1);
        }
        else
        {
            restUserCreateRequest.setMaxVersions(user.getMaxVersions());
        }
        restUserCreateRequest.setDescription(user.getDescription());
        if (user.getMaxVersions() == 0)
        {
            user.setMaxVersions(-1);
        }
        restUserCreateRequest.setMaxVersions(user.getMaxVersions());
        restUserCreateRequest.setStatus(user.getStatus());
        return restUserCreateRequest;
    }

    public static Map<String, String> assembleAccountToken(EnterpriseAccount enterpriseAccount) {
        if(enterpriseAccount == null) {
            return null;
        }
        String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(), enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = new HashMap<String, String>(16);
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String sign = SignatureUtils.getSignature(decodedKey, dateStr);
        String authorization = "account," + enterpriseAccount.getAccessKeyId() + ',' + sign;
        headers.put("Authorization", authorization);
        headers.put("Date", dateStr);

        return headers;
    }
    
    /**
     * 迁移账户
     * @param enterpriseAccount
     * @return
     */
    public DataMigrationResponseDto migrateAccount(DataMigrationRequestDto migrationData,
        EnterpriseAccount enterpriseAccount) {
        
        String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(), enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(),  decodedKey);

        TextResponse response = null;
        try {
            response = ufmClientService.performJsonPostTextResponse(DATA_MIGRATION + "/migrationAccount", headers, migrationData);
            if (response.getStatusCode() != 200) {
                logger.error("Migrate departure user account fail code:" + response.getStatusCode());
                return null;
            }
        } catch (ServiceException e) {
            logger.error("Migrate departure user account fail", e);
            return null;
        }
        
        DataMigrationResponseDto result = JsonUtils.stringToObject(response.getResponseBody(),
            DataMigrationResponseDto.class);
        return result;
    }
    
    /**
     * 迁移数据
     * @param cloudUserId
     * @param enterpriseAccount
     * @return
     */
    public DataMigrationResponseDto migrateData(DataMigrationRequestDto migrationData,
        EnterpriseAccount enterpriseAccount) {
        
        String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(), enterpriseAccount.getSecretKeyEncodeKey());
        Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(),  decodedKey);

        TextResponse response = null;
        try {
            response = ufmClientService.performJsonPostTextResponse(DATA_MIGRATION + "/migrationData", headers, migrationData);
            if (response.getStatusCode() != 200) {
                logger.error("Migrate departure user data fail code:" + response.getStatusCode());
                return null;
            }
        } catch (ServiceException e) {
            logger.error("Migrate departure user data fail", e);
            return null;
        }
        
        DataMigrationResponseDto result = JsonUtils.stringToObject(response.getResponseBody(),
            DataMigrationResponseDto.class);
        return result;
    }
    
    /**
     * 清理数据数据
     * @param cloudUserId
     * @param enterpriseAccount
     * @return
     */
	public boolean cleanDepartureUserInfo(MigrationRecord migrationData, EnterpriseAccount enterpriseAccount) {
		String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(),
				enterpriseAccount.getSecretKeyEncodeKey());
		Map<String, String> headers = getAppAuthHeader("account", enterpriseAccount.getAccessKeyId(), decodedKey);

		TextResponse response = null;
		try {
			response = ufmClientService.performJsonPostTextResponse(DATA_MIGRATION + "/cleanUserInfo", headers,
					migrationData);
			if (response.getStatusCode() != 200) {
				logger.error("Clean departure user data fail code:" + response.getStatusCode());
				return false;
			}
		} catch (ServiceException e) {
			logger.error("Clean departure user data fail", e);
			return false;
		}

		boolean result = Boolean.valueOf(response.getResponseBody());

		return result;
	}
}
