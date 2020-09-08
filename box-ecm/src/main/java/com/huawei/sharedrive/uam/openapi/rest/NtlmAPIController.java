package com.huawei.sharedrive.uam.openapi.rest;

import com.huawei.common.ntlmv2.liferay.NtlmUserAccount;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.authapp.manager.AuthAppNetRegionIpManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.enterprisecontrol.EnterpriseAuthControlManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.ldapauth.manager.AuthServiceManager;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginTerminalManager;
import com.huawei.sharedrive.uam.ldapauth.manager.impl.LoginUpdateManagerImpl;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RestLoginResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestNtlmCreateRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestNtlmGetChallengeResponse;
import com.huawei.sharedrive.uam.openapi.domain.RestTerminalRsp;
import com.huawei.sharedrive.uam.openapi.manager.NtlmApiCheckManager;
import com.huawei.sharedrive.uam.openapi.manager.NtlmApiManager;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.uam.util.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.log.UserLog;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/v2/ntlm")
public class NtlmAPIController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NtlmAPIController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private NtlmApiManager ntlmApiManager;
    
    @Autowired
    private NtlmApiCheckManager ntlmApiCheckManager;
    
    @Autowired
    private EnterpriseAuthControlManager enterpriseAuthControlManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @Autowired
    private LoginUpdateManagerImpl loginUpdateManagerImpl;
    
    @Autowired
    private AuthAppNetRegionIpManager authAppNetRegionIpManager;
    
    @Autowired
    private LoginTerminalManager loginTerminalManager;
    
    /**
     * 
     * @param sessionId
     * @param restNtlmCreateRequest
     * @return
     * @throws BaseRunException
     * @throws IOException
     */
    @RequestMapping(value = "/challenge", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestNtlmGetChallengeResponse> createChallenge(HttpServletRequest request,
        @RequestBody RestNtlmCreateRequest restNtlmCreateRequest) throws BaseRunException, IOException
    {
        String usernameHash = restNtlmCreateRequest.getUsernameHash();
        String key = restNtlmCreateRequest.getKey();
        String appId = restNtlmCreateRequest.getAppId();
        String realIp = RequestUtils.getRealIP(request);
        UserLog userLog = new UserLog();
        userLog.setLoginName(key);
        userLog.setClientAddress(realIp);
        RestNtlmGetChallengeResponse restNtlmGetChallengeResponse = null;
        try
        {
            if (StringUtils.isBlank(usernameHash) || StringUtils.isBlank(key)||StringUtils.isBlank(appId)||StringUtils.isBlank(realIp))
            {
                throw new InvalidParamterException();
            }
            AuthServer authServer = enterpriseAuthControlManager.getNtlmAuthServer(appId);
            String challenge = ntlmApiManager.getChallenge(usernameHash, key, authServer.getId(), realIp);
            restNtlmGetChallengeResponse = new RestNtlmGetChallengeResponse();
            restNtlmGetChallengeResponse.setChallenge(challenge);
        }
        catch (RuntimeException e)
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_GET_NTLM_CHALLENGE_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_GET_NTLM_CHALLENGE, null);
        return new ResponseEntity<RestNtlmGetChallengeResponse>(restNtlmGetChallengeResponse, HttpStatus.OK);
    }
    
    /**
     * 
     * @param restNtlmCreateRequest
     * @return
     * @throws BaseRunException
     * @throws IOException
     */
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestLoginResponse> validateChallenge(HttpServletRequest request,
        @RequestBody RestNtlmCreateRequest restNtlmCreateRequest) throws BaseRunException, IOException
    {
        String challenge = restNtlmCreateRequest.getChallenge();
        String key = restNtlmCreateRequest.getKey();
        String appId = restNtlmCreateRequest.getAppId();
        String regionIp = RequestUtils.getLoginRegionIp(request);
        String realIp = RequestUtils.getRealIP(request);
        
        ntlmApiCheckManager.checkNtlmParamter(appId, key, challenge, request);
        AuthServer authServer = enterpriseAuthControlManager.getNtlmAuthServer(appId);
        NtlmUserAccount ntlmUserAccount = ntlmApiManager.validateChallenge(challenge,
            key,
            authServer.getId(),
            realIp);
        String loginName = ntlmUserAccount.getUserName();
        LoginInfo loginInfo = enterpriseAuthControlManager.getLoginInfo(loginName, null, appId);
        AuthServiceManager authServiceManager = authServerManager.getAuthService(authServer.getId());
        EnterpriseUser enterpriseUserByLdap = authServiceManager.getUserByLoginName(authServer.getId(),
            loginName);
        if (null == enterpriseUserByLdap)
        {
            LOGGER.error("auth failed user is null");
            throw new LoginAuthFailedException();
        }
        enterpriseUserByLdap.setUserSource(authServer.getId());
        enterpriseUserByLdap.setEnterpriseId(loginInfo.getEnterpriseId());
        EnterpriseUserAccount enterpriseUserAccount = loginUpdateManagerImpl.save(authServer.getId(),
            enterpriseUserByLdap.getObjectSid(),
            loginInfo.getEnterpriseId(),
            appId,
            enterpriseUserByLdap);
        if (enterpriseUserAccount.getAccountStatus() == UserAccount.INT_STATUS_DISABLE)
        {
            LOGGER.info("account user status disabled accountid:" + enterpriseUserAccount.getAccountId()
                + " id:" + enterpriseUserAccount.getId());
            throw new LoginAuthFailedException();
        }
        UserToken userToken = new UserToken();
        userToken.setAppId(appId);
        UserToken.buildUserToken(userToken, enterpriseUserAccount);

        authAppNetRegionIpManager.setLoginMatchNetRegion(regionIp, appId, userToken);
        RestTerminalRsp lastAccessTerminal = loginTerminalManager.getByUserLastLogin(enterpriseUserAccount.getCloudUserId());
        userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
        RestLoginResponse restLoginResponse = RestLoginResponse.fillRestLoginResponse(userToken);
        Map<String, Long> bandWidthMap = userService.fillBandWidth(enterpriseUserAccount.getDownloadBandWidth(),
            enterpriseUserAccount.getUploadBandWidth(),
            appId);
        restLoginResponse.setUploadQos(bandWidthMap.get(UserServiceImpl.UPLOAD_BANDWIDTH));
        restLoginResponse.setDownloadQos(bandWidthMap.get(UserServiceImpl.DOWNLOAD_BANDWIDTH));
        userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
        UserLog userLog = UserLogType.getUserLog(request, appId, loginName, false);
        userLogService.saveUserLog(userLog, UserLogType.KEY_GET_TOKEN, null);
        restLoginResponse.setLastAccessTerminal(lastAccessTerminal);
        return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
    }
}
