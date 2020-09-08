package com.huawei.sharedrive.uam.oauth2.service.impl;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.declare.manager.UserSignDeclareManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginTerminalManager;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.UserTokenCacheService;
import com.huawei.sharedrive.uam.terminal.service.TerminalService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.domain.UserSignDeclare;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.uam.domain.AuthApp;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class UserTokenHelper
{
    public final static String APP_PREFIX = "app,";
    
    public final static String ACCOUNT_PREFIX = "account,";
    
    public final static String LINK_PREFIX = "link,";
    
    private final static int LENG_ARRAY_APP = 3;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(UserTokenHelper.class);
    
    private static final String APPID_SEP = "/";
    
    public static final int CLIENT_TYPE_ANDROID = 2;
    
    public static final String CLIENT_TYPE_ANDROID_STR = "android";
    
    public static final int CLIENT_TYPE_IOS = 3;
    
    public static final String CLIENT_TYPE_IOS_STR = "ios";
    
    public static final int CLIENT_TYPE_PC = 1;
    
    public static final String CLIENT_TYPE_PC_STR = "pc";
    
    public static final int CLIENT_TYPE_WEB = 0;
    
    public static final String CLIENT_TYPE_WEB_STR = "web";
    
    private static String generateToken(String appId)
    {
        return new StringBuilder().append(appId)
            .append(APPID_SEP)
            .append(RandomKeyGUID.getSecureRandomGUID())
            .toString();
    }

    @Value("${auth2.token.expire}")
    private int tokenExpireTime;
    
    @Value("${auth2.token.retray.time}")
    private long tokenRetryTime;
    
    @Autowired
    private UserTokenCacheService userTokenCacheService;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private AppAccessKeyService appAccessKeyService;
    
    @Autowired
    private EnterpriseAccountService enterpriseAccountService;
    
    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private UserSignDeclareManager userSignDeclareManager;

    @Autowired
    private LoginTerminalManager loginTerminalManager;

    public int getTokenExpireTime()
    {
        return tokenExpireTime;
    }
    
    public long getTokenRetryTime()
    {
        return tokenRetryTime;
    }
    
    /**
     * Auth <br/>
     * 
     * @param auth
     * @return
     * @throws AuthFailedException
     * @throws InternalServerErrorException
     */
    public UserToken unSafeCheckTokenAndGetUser(String authToken) throws AuthFailedException, InternalServerErrorException {
        String token = authToken;
        String[] tmps = authToken.split(" ");

        if (2 == tmps.length) {
            token = tmps[1];
        }

        UserToken userToken = userTokenCacheService.getUserToken(token);

        if (null == userToken) {
            LOGGER.warn("No userToken Found: accessToken={}", authToken);
            String errorMessage = "[tokenLog] userToken is null";
            throw new NoSuchTokenException(errorMessage + ": " + token);
        }
        if (String.valueOf(UserAccount.INT_STATUS_DISABLE).equals(userToken.getStatus())) {
            LOGGER.error("user status is disabled: id={}, cloudUserId={}", userToken.getId(), userToken.getCloudUserId());
            throw new AuthFailedException("user is disabled.");
        }

        if(userToken.getAccountId()!=0){
        	  EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(userToken.getAccountId());
              if (null == enterpriseAccount) {
                  LOGGER.error("enterpriseAccount is null accountId:" + userToken.getAccountId());
                  throw new AuthFailedException("enterpriseAccount is null accountId:" + userToken.getAccountId());
              }
              if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
                  LOGGER.error("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status: " + enterpriseAccount.getStatus());
                  throw new AuthFailedException("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status" + enterpriseAccount.getStatus());
              }
              userToken.setEnterpriseId(enterpriseAccount.getEnterpriseId());

        }else{
        	  userToken.setEnterpriseId(0);
        	  userToken.setAccountId(0);
        }
        
        //access token自动续约
        userToken.setToken(token);
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);
        userTokenCacheService.saveUserToken(userToken);

        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setClientType(getClietType(userToken.getDeviceType()));
        boolean needDeclaration = userSignDeclareManager.isNeedDeclaration(declare, userToken.getAppId());
        userToken.setNeedDeclaration(needDeclaration);
        if(userToken.getAccountId()!=0){
            boolean needChangePassword = userAccountService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId());
            userToken.setNeedChangePassword(needChangePassword && needChangePwdFromConfig());
        }else{
            userToken.setNeedChangePassword(false);
        }

 
        return userToken;
    }
    
    /**
     * Auth <br/>
     * 
     * @param authd
     * @return
     * @throws AuthFailedException
     * @throws InternalServerErrorException
     */
    public UserToken checkTokenAndGetUser(String authToken) throws AuthFailedException, InternalServerErrorException {
        String token = authToken;
        String[] tmps = authToken.split(" ");

        if (2 == tmps.length) {
            token = tmps[1];
        }

        UserToken userToken = userTokenCacheService.getUserToken(token);
        if (null == userToken) {
            LOGGER.warn("No userToken Found: accessToken={}", authToken);
            String errorMessage = "[tokenLog] userToken or terminal or refreshToken is null userToken";
            throw new AuthFailedException(errorMessage);
        }
        if (String.valueOf(UserAccount.INT_STATUS_DISABLE).equals(userToken.getStatus())) {
            LOGGER.error("userstatus or terminalstatus  is unnomral userstatus:" + userToken.getStatus());
            throw new AuthFailedException();
        }

        if (userToken.isNeedChangePassword()) {
            LOGGER.error("user has not inited password, needChangePassword = " + userToken.isNeedChangePassword());
            throw new PasswordInitException("enterprise user has not modify password, needChangePassword = " + userToken.isNeedChangePassword());
        }

        if (userToken.isNeedDeclaration()) {
            LOGGER.error("user need sign declaration, needDeclaration = " + userToken.isNeedDeclaration());
            throw new DeclarationException("user need sign declaration, needDeclaration = " + userToken.isNeedDeclaration());
        }
        
        if(userToken.getAccountId()!=0){

            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(userToken.getAccountId());
            if (null == enterpriseAccount) {
                LOGGER.error("enterpriseAccount is null accountId:" + userToken.getAccountId());
                throw new AuthFailedException("enterpriseAccount is null accountId:" + userToken.getAccountId());
            }
            if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
                LOGGER.error("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status" + enterpriseAccount.getStatus());
                throw new AuthFailedException("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status" + enterpriseAccount.getStatus());
            }
            
            boolean needChangePassword = userAccountService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId());
            userToken.setNeedChangePassword(needChangePassword && needChangePwdFromConfig());
        }


        userToken.setToken(token);
        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setClientType(getClietType(userToken.getDeviceType()));
        boolean needDeclaration = userSignDeclareManager.isNeedDeclaration(declare, userToken.getAppId());
        userToken.setNeedDeclaration(needDeclaration);
     

        //access token自动续约
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);
        userTokenCacheService.saveUserToken(userToken);

        return userToken;
    }
    
    
    
    public UserToken checkTokenAndGetAdminUser(String authToken) throws AuthFailedException, InternalServerErrorException {

        String token = authToken;
        String[] tmps = authToken.split(" ");

        if (2 == tmps.length) {
            token = tmps[1];
        }

        UserToken userToken = userTokenCacheService.getUserToken(token);
        
       
        if (null == userToken) {
            LOGGER.warn("No userToken Found: accessToken={}", authToken);
            String errorMessage = "[tokenLog] userToken or terminal or refreshToken is null userToken";
            throw new AuthFailedException(errorMessage);
        }
        
        if (userToken.getIsAdmin()==0) {
        	String errorMessage = "user is not admin";
            throw new AuthFailedException(errorMessage);
        }
        if (String.valueOf(UserAccount.INT_STATUS_DISABLE).equals(userToken.getStatus())) {
            LOGGER.error("userstatus or terminalstatus  is unnomral userstatus:" + userToken.getStatus());
            throw new AuthFailedException();
        }

        if (userToken.isNeedChangePassword()) {
            LOGGER.error("user has not inited password, needChangePassword = " + userToken.isNeedChangePassword());
            throw new PasswordInitException("enterprise user has not modify password, needChangePassword = " + userToken.isNeedChangePassword());
        }

        if (userToken.isNeedDeclaration()) {
            LOGGER.error("user need sign declaration, needDeclaration = " + userToken.isNeedDeclaration());
            throw new DeclarationException("user need sign declaration, needDeclaration = " + userToken.isNeedDeclaration());
        }
        
        if(userToken.getAccountId()!=0){

            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByAccountId(userToken.getAccountId());
            if (null == enterpriseAccount) {
                LOGGER.error("enterpriseAccount is null accountId:" + userToken.getAccountId());
                throw new AuthFailedException("enterpriseAccount is null accountId:" + userToken.getAccountId());
            }
            if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus()) {
                LOGGER.error("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status" + enterpriseAccount.getStatus());
                throw new AuthFailedException("enterpriseAccount status is disable, accountId:" + userToken.getAccountId() + "status" + enterpriseAccount.getStatus());
            }
            
            boolean needChangePassword = userAccountService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId());
            userToken.setNeedChangePassword(needChangePassword && needChangePwdFromConfig());
        }


        userToken.setToken(token);
        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setClientType(getClietType(userToken.getDeviceType()));
        boolean needDeclaration = userSignDeclareManager.isNeedDeclaration(declare, userToken.getAppId());
        userToken.setNeedDeclaration(needDeclaration);
     

        //access token自动续约
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);
        userTokenCacheService.saveUserToken(userToken);

        return userToken;
    }
    
    /**
     * 
     * @param authToken
     * @return
     * @throws
     */
    public void updateTokenCache(String authToken, String clientType)
    {
        String token = authToken;
        String[] tmps = authToken.split(" ");
        
        if (2 == tmps.length)
        {
            token = tmps[1];
        }
        
        UserToken userToken = userTokenCacheService.getUserToken(token);
        if (userToken == null)
        {
            return;
        }

        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setClientType(clientType);
        boolean needDeclaration = userSignDeclareManager.isNeedDeclaration(declare, userToken.getAppId());
        userToken.setNeedDeclaration(needDeclaration);
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);

        userTokenCacheService.saveUserToken(userToken);
    }
    
    public void updateTokenCache(String authToken)
    {
        String token = authToken;
        String[] tmps = authToken.split(" ");
        
        if (2 == tmps.length)
        {
            token = tmps[1];
        }
        
        UserToken userToken = userTokenCacheService.getUserToken(token);
        if (userToken == null)
        {
            return;
        }

        boolean needChangePassword = userAccountService.isLocalAndFirstLogin(userToken.getAccountId(), userToken.getId());
        userToken.setNeedChangePassword(needChangePassword && needChangePwdFromConfig());
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);

        userTokenCacheService.saveUserToken(userToken);
    }
    
    /**
     * 
     * @return
     * @throws AuthFailedException
     */
    public UserToken getUserTokenByRefreshToken(String refreshToken) throws AuthFailedException {
        UserToken userToken = userTokenCacheService.getUserTokenByRefreshToken(refreshToken);
        if (userToken == null) {
            throw new AuthFailedException();
        }

        return userToken;
    }

    /**
     * 
     * @return
     * @throws InternalServerErrorException
     */
    public void createAndFillToken(UserToken userToken, EnterpriseUserAccount enterpriseUserAccount, HttpServletRequest request) throws InternalServerErrorException {
        String token = generateToken(userToken.getAppId());
        String refreshToken = generateToken(userToken.getAppId());

        Terminal terminal = loginTerminalManager.saveOrUpdateTerminalWhenLogin(enterpriseUserAccount.getCloudUserId(),
                userToken.getAppId(),
                enterpriseUserAccount.getAccountId(),
                request,
                token,
                false);
        userToken.setToken(token);
        userToken.setRefreshToken(refreshToken);
        fillUserToken(userToken, terminal);
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);
        userTokenCacheService.saveUserToken(userToken);
    }
    
    

    /**
     * 
     * @return
     * @throws InternalServerErrorException
     */
    public void createInvitToken(UserToken userToken,int tokenExpireTime, HttpServletRequest request) throws InternalServerErrorException {
        String token = generateToken(userToken.getAppId());
        String refreshToken = generateToken(userToken.getAppId());

        userToken.setToken(token);
        userToken.setRefreshToken(refreshToken);
//        fillUserToken(userToken, terminal);
        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);
        userTokenCacheService.saveUserToken(userToken);
    }

    /**
     *
     * @throws InternalServerErrorException
     */
    public UserToken refreshToken(HttpServletRequest request, String token, String refreshToken, String appId) throws InternalServerErrorException {
        UserToken userToken = userTokenCacheService.getUserTokenByRefreshToken(refreshToken);
        if (userToken == null) {
            throw new InternalServerErrorException("Can not get userToken ");
        }

        //不主动删除原有的token，使用memcache自动过期机制。允许新旧token在一段时间内同时存在，这样客户端在使用的旧的token时可以同时刷新新的token。
        //userTokenCacheService.deleteToken(token);

        //refreshToken有过期时间，不自动续签
        String newToken = generateToken(appId);
        userToken.setToken(newToken);

        Terminal terminal = loginTerminalManager.fillTerminal(request, false);
        userToken.setDeviceAddress(terminal.getLastAccessIP());

        Date expireTime = new Date(System.currentTimeMillis() + tokenExpireTime);
        userToken.setExpiredAt(expireTime);

        //将token相关数据重新缓存
        userTokenCacheService.saveUserToken(userToken);

        //更新记录
        this.terminalService.updateToken(userToken.getCloudUserId(), token, newToken);

        return userToken;
    }

    public void delUserToken(UserToken token) throws NoSuchTokenException
    {
        userTokenCacheService.deleteToken(token.getToken());
        terminalService.updateToken(token.getCloudUserId(), token.getToken(), null);
    }
    
    public void delUserToken(String tokenStr) throws NoSuchTokenException
    {
        userTokenCacheService.deleteToken(tokenStr);
    }
    
    @Autowired
    private TerminalService terminalService;
    
    public void saveUserInfo(UserToken userToken)
    {
        userTokenCacheService.saveUserToken(userToken);
    }

    /**
     * 
     * @param appToken
     */
    public String checkAppToken(String authorization, String date) throws AuthFailedException
    {
        if (!authorization.startsWith(APP_PREFIX) && !authorization.startsWith(ACCOUNT_PREFIX))
        {
            LOGGER.error("Bad app authorization: " + authorization);
            throw new AuthFailedException();
        }
        String[] strArr = authorization.split(",");
        if (strArr.length != LENG_ARRAY_APP)
        {
            LOGGER.error("Bad app authorization: " + authorization);
            throw new AuthFailedException();
        }
        AppAccessKey key = appAccessKeyService.getById(StringUtils.trimToEmpty(strArr[1]));
        if (null == key)
        {
            LOGGER.error("Can not find the key " + authorization);
            throw new AuthFailedException();
        }
        checkSignature(key.getSecretKey(), date, strArr[2]);
        AuthApp app = authAppService.getByAuthAppID(key.getAppId());
        if (null == app)
        {
            LOGGER.error("Can not find the app " + authorization);
            throw new AuthFailedException();
        }
        if (app.getStatus() != 0)
        {
            LOGGER.error("Bad app status");
            throw new AuthFailedException();
        }
        return key.getAppId();
    }
    
    /**
     * 
     * @param appToken
     */
    public String checkDynamicLinkToken(String authorization, String date) throws AuthFailedException
    {
        String[] strArr = authorization.split(",");
        if (strArr.length != LENG_ARRAY_APP)
        {
            LOGGER.error("Bad app authorization: " + authorization);
            throw new AuthFailedException();
        }
        checkSignature(strArr[1], date, strArr[2]);
        return strArr[1];
    }
    
    private void checkSignature(String securityKey, String date, String result) throws AuthFailedException
    {
        String calcuRes = SignatureUtils.getSignature(StringUtils.trimToEmpty(securityKey), date);
        if (!StringUtils.equals(result, calcuRes))
        {
            LOGGER.error("signature result is false. calcuRes is " + calcuRes);
            throw new AuthFailedException();
        }
    }
    
    private void fillUserToken(UserToken userToken, Terminal terminal)
    {
        userToken.setDeviceAddress(terminal.getLastAccessIP());
        userToken.setDeviceAgent(terminal.getDeviceAgent());
        userToken.setDeviceName(terminal.getDeviceName());
        userToken.setDeviceOS(terminal.getDeviceOS());
        userToken.setDeviceSN(terminal.getDeviceSn());
        userToken.setDeviceType(terminal.getDeviceType());
    }
    
    private String getClietType(int index)
    {
        String clientType = "";
        switch (index)
        {
            case CLIENT_TYPE_WEB:
                clientType = CLIENT_TYPE_WEB_STR;
                break;
            case CLIENT_TYPE_ANDROID:
                clientType = CLIENT_TYPE_ANDROID_STR;
                break;
            case CLIENT_TYPE_PC:
                clientType = CLIENT_TYPE_PC_STR;
                break;
            case CLIENT_TYPE_IOS:
                clientType = CLIENT_TYPE_IOS_STR;
                break;
            
            default:
                break;
        }
        return clientType;
    }
    
    public boolean needChangePwdFromConfig()
    {
//        String value = PropertiesUtils.getProperty("user.local.firstlogin.changepwd", "true");
        String value = "false";
        if (null == value)
        {
            return true;
        }
        if ("false".equalsIgnoreCase(value))
        {
            return false;
        }
        return true;
    }
    
}
