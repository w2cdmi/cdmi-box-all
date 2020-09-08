package com.huawei.sharedrive.uam.cmb.openapi.rest;

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
import com.huawei.sharedrive.uam.cmb.control.manager.Constants;
import com.huawei.sharedrive.uam.cmb.openapi.domain.RequestSsoAPI;
import com.huawei.sharedrive.uam.cmb.sso.util.CMBCertUtils;
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
import com.huawei.sharedrive.uam.user.domain.UserLocked;
import com.huawei.sharedrive.uam.user.service.UserLockService;
import com.huawei.sharedrive.uam.user.service.UserService;
import com.huawei.sharedrive.uam.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.uam.util.RequestUtils;

@Controller
@RequestMapping(value = "/api/v2/cmb/ssoauth")
public class CMBSsoAPIController
{
    private static Logger logger = LoggerFactory.getLogger(CMBSsoAPIController.class);
    
    @Autowired
    private AuthAppNetRegionIpManager authAppNetRegionIpManager;
    
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
    public ResponseEntity<RestLoginResponse> login(@RequestBody RequestSsoAPI requestSsoAPI,
        HttpServletRequest request) throws BaseRunException, IOException
    {
        if (!Constants.isCMB)
        {
            logger.warn("it is not a cmb application");
            throw new InvalidParamterException();
        }
        String appId = requestSsoAPI.getAppId();
        String data = requestSsoAPI.getData();
        String cmbToken = requestSsoAPI.getToken();
        String regionIp = RequestUtils.getLoginRegionIp(request);
        String requestIp = RequestUtils.getRealIP(request);
        logger.info("user sso login appId:" + appId + " data:" + data + " requestIp:" + requestIp);
        requestSsoAPI.checkParameter(request);
        AuthApp authApp = null;
        if (StringUtils.isBlank(appId))
        {
            authApp = getDefaultAuthApp();
            appId = authApp.getAuthAppId();
        }
        
        String userInfo = CMBCertUtils.verifySign(data, cmbToken);
        if (StringUtils.isBlank(userInfo))
        {
            logger.error("can not get userInfo");
            throw new LoginAuthFailedException();
        }
        // 超时也登录失败
        if (CMBCertUtils.isSSOTimeExpired(userInfo))
        {
            throw new LoginAuthFailedException();
        }
        String sapId = CMBCertUtils.phraseSSOResult(userInfo);
        if (StringUtils.isBlank(sapId))
        {
            logger.error("sapId is null");
            throw new LoginAuthFailedException();
        }
        UserLog userLog = UserLogType.getUserLog(request, appId, sapId, false);
        LoginInfo loginInfo = enterpriseAuthControlManager.getLoginInfo(sapId, null, appId);
        UserLocked userLocked = null;
        if (loginInfo.getUserId() != 0)
        {
            userLocked = userLockService.checkUserLocked(userLog, loginInfo.getUserId(), sapId, appId);
            if (userLockService.isLocked(userLocked))
            {
                String msg = "user has been locked from " + userLocked.getLockedAt() + ", userloginname is "
                    + sapId;
                throw new UserLockedException(msg);
            }
        }
        String domainName = loginInfo.getDomainName();
        EnterpriseUserAccount enterpriseUserAccount = null;
        try
        {
            enterpriseUserAccount = checkAndGetUser(sapId, appId, domainName);
        }
        catch (LoginAuthFailedException e)
        {
            userLogService.saveFailLog(sapId, appId, null, UserLogType.KEY_GET_TOKEN_ERR);
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
    
    private AuthApp getDefaultAuthApp()
    {
        AuthApp authApp = authAppService.getDefaultWebApp();
        if (null == authApp)
        {
            logger.error("no DefaultWebApp");
            throw new InvalidParamterException();
        }
        return authApp;
    }
}
