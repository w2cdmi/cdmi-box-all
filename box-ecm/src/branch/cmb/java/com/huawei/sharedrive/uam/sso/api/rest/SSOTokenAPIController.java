package com.huawei.sharedrive.uam.sso.api.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import com.huawei.sharedrive.common.log.UserLog;
import com.huawei.sharedrive.portal.domain.AuthApp;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.authapp.manager.AuthAppNetRegionIpManager;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.enterprisecontrol.EnterpriseAuthControlManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.exception.UserLockedException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.logininfo.domain.LoginInfo;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RestLoginResponse;
import com.huawei.sharedrive.uam.sso.api.domain.RestSSOLoginRequest;
import com.huawei.sharedrive.uam.sso.manager.SSOTmpTokenCheckManager;
import com.huawei.sharedrive.uam.sso.manager.SSOTmpTokenManager;
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.uam.util.RequestUtils;

@Controller
@RequestMapping(value = "/api/v2/ssotoken")
public class SSOTokenAPIController
{
    private static Logger logger = LoggerFactory.getLogger(SSOTokenAPIController.class);
    
    @Autowired
    private AuthAppNetRegionIpManager authAppNetRegionIpManager;
    
    @Autowired
    private SSOTmpTokenCheckManager sSOTmpTokenCheckManager;
    
    @Autowired
    private SSOTmpTokenManager sSOTmpTokenManager;
    
    @Autowired
    private UserLockService userLockService;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private EnterpriseAuthControlManager enterpriseAuthControlManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    /**
     * 
     * @param requestIp
     * @param restUserLoginCreateRequest
     * @return
     * @throws BaseRunException
     * @throws IOException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestLoginResponse> login(@RequestBody RestSSOLoginRequest restSSOLoginRequest,
        HttpServletRequest request) throws BaseRunException, IOException
    {
        String appId = restSSOLoginRequest.getAppId();
        String ssoToken = restSSOLoginRequest.getSsoToken();
        String domainName = restSSOLoginRequest.getDomain();
        String regionIp = RequestUtils.getLoginRegionIp(request);
        String requestIp = RequestUtils.getRealIP(request);
        logger.info("user sso login appId:" + appId + " domainName:" + domainName + " requestIp:" + requestIp);
        sSOTmpTokenCheckManager.checkSsoParamter(appId, ssoToken, domainName, request);
        String loginName = sSOTmpTokenManager.getSSOLoginName(ssoToken);
        if (StringUtils.isBlank(loginName))
        {
            logger.error("can not get objectSid by ssoCache ssoToken:" + ssoToken);
            userLogService.saveFailLog(loginName, appId, null, UserLogType.KEY_GET_TOKEN_ERR);
            logger.error("no such appId:" + appId);
            throw new LoginAuthFailedException();
        }
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (null == authApp)
        {
            userLogService.saveFailLog(loginName, appId, null, UserLogType.KEY_GET_TOKEN_ERR);
            logger.error("no such appId:" + appId);
            throw new InvalidParamterException();
        }
        UserLog userLog = UserLogType.getUserLog(request, appId, loginName, false);
        
        LoginInfo loginInfo = enterpriseAuthControlManager.getLoginInfo(loginName, domainName, appId);
        UserLocked userLocked = null;
        if (loginInfo.getUserId() != 0)
        {
            userLocked = userLockService.checkUserLocked(userLog, loginInfo.getUserId(), loginName, appId);
            if (userLockService.isLocked(userLocked))
            {
                String msg = "user has been locked from " + userLocked.getLockedAt() + ", userloginname is "
                    + loginName;
                throw new UserLockedException(msg);
            }
        }
        domainName = loginInfo.getDomainName();
        EnterpriseUserAccount enterpriseUserAccount;
        try
        {
            enterpriseUserAccount = checkAndGetUser(loginName, appId, domainName);
        }
        catch (LoginAuthFailedException e)
        {
            userLogService.saveFailLog(loginName, appId, null, UserLogType.KEY_GET_TOKEN_ERR);
            throw new LoginAuthFailedException();
        }
        if (enterpriseUserAccount.getAccountStatus() == UserAccount.INT_STATUS_DISABLE)
        {
            logger.info("account user status disabled accountid:" + enterpriseUserAccount.getAccountId()
                + " id:" + enterpriseUserAccount.getId());
            throw new LoginAuthFailedException();
        }
        UserToken userToken = new UserToken();
        UserToken.buildUserToken(userToken, enterpriseUserAccount);
        userToken.setAppId(appId);
        authAppNetRegionIpManager.setLoginMatchNetRegion(regionIp, appId, userToken);
        userTokenHelper.createAndFillToken(userToken, enterpriseUserAccount, request);
        RestLoginResponse restLoginResponse = RestLoginResponse.fillRestLoginResponse(userToken);
        Map<String, Long> bandWidthMap = userService.fillBandWidth(enterpriseUserAccount.getDownloadBandWidth(),
            enterpriseUserAccount.getUploadBandWidth(),
            appId);
        restLoginResponse.setUploadQos(bandWidthMap.get(UserServiceImpl.UPLOAD_BANDWIDTH));
        restLoginResponse.setDownloadQos(bandWidthMap.get(UserServiceImpl.DOWNLOAD_BANDWIDTH));
        restLoginResponse.setDomain(domainName);
        userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
        userLogService.saveUserLog(userLog, UserLogType.KEY_GET_TOKEN, null);
        if (null != userLocked)
        {
            userLockService.deleteUserLocked(userLocked);
        }
        return new ResponseEntity<RestLoginResponse>(restLoginResponse, HttpStatus.OK);
    }
    
    private EnterpriseUserAccount checkAndGetUser(String loginName, String appId, String domainName)
    {
        EnterpriseUser enterpriseUser = enterpriseUserManager.getEUserByLoginName(loginName, domainName);
        if (null == enterpriseUser)
        {
            logger.error("enterpriseUser is null selName:" + loginName + " domain:" + domainName);
            throw new LoginAuthFailedException();
        }
        UserAccount userAccount = userAccountManager.getUserAccountByApp(enterpriseUser.getId(),
            enterpriseUser.getEnterpriseId(),
            appId);
        if (null == userAccount)
        {
            logger.error("userAccount is null userId:" + enterpriseUser.getId() + " enterpriseId:"
                + enterpriseUser.getEnterpriseId() + " appId:" + appId);
            throw new LoginAuthFailedException();
        }
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        return enterpriseUserAccount;
    }
}
