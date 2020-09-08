package com.huawei.sharedrive.uam.openapi.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.declare.manager.ConcealDeclareManager;
import com.huawei.sharedrive.uam.declare.manager.UserSignDeclareManager;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.ExistDeclarationException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;

import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.domain.UserSignDeclare;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/declaration")
public class DeclarationAPIController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationAPIController.class);
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private ConcealDeclareManager concealDeclareManager;
    
    @Autowired
    private UserSignDeclareManager userSignDeclareManager;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/{clientType}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConcealDeclare> getdeclaration(@RequestHeader("Authorization") String token,
        @PathVariable("clientType") String clientType) throws BaseRunException
    {
        ConcealDeclare cdeclare = null;
        UserLog userLog = null;
        String[] params = {clientType};
        try
        {
            UserToken userToken = userTokenHelper.unSafeCheckTokenAndGetUser(token);
            userLog = UserLogType.getUserLog(userToken);
            String appId = userToken.getAppId();
            if (StringUtils.isEmpty(clientType))
            {
                throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
            }
            ConcealDeclare declare = new ConcealDeclare();
            declare.setAppId(appId);
            declare.setClientType(clientType);
            cdeclare = concealDeclareManager.getDeclaration(declare);
            if (cdeclare == null || StringUtils.isEmpty(cdeclare.getDeclaration()))
            {
                LOGGER.error("No Such Exist Declaration");
                throw new ExistDeclarationException();
            }
        }
        catch (RuntimeException e)
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_DECLARATION_GET_ERR, params);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_DECLARATION_GET, params);
        
        return new ResponseEntity<ConcealDeclare>(cdeclare, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/sign", method = RequestMethod.PUT)
    public ResponseEntity<UserSignDeclare> sign(@RequestHeader("Authorization") String token,
        @RequestBody ConcealDeclare concealDeclare, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = userTokenHelper.unSafeCheckTokenAndGetUser(token);
        if (StringUtils.isEmpty(concealDeclare.getId()))
        {
            UserLog userLog = UserLogType.getUserLog(userToken);
            userLogService.saveFailLog(userLog,
                userToken.getLoginName(),
                userToken.getAppId(),
                new String[]{getDeviceType(userToken.getType())},
                UserLogType.KEY_USER_SIGN_ERROR);
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        ConcealDeclare declare = concealDeclareManager.getDeclarationById(concealDeclare.getId());
        if (null == declare)
        {
            UserLog userLog = UserLogType.getUserLog(userToken);
            userLogService.saveFailLog(userLog,
                userToken.getLoginName(),
                userToken.getAppId(),
                new String[]{getDeviceType(userToken.getType())},
                UserLogType.KEY_USER_SIGN_ERROR);
            throw new InvalidParamterException(ErrorCode.INVALID_PARAMTER.getMessage());
        }
        UserSignDeclare userDeclare = buildDeclare(userToken);
        userDeclare.setDeclareId(declare.getId());
        userDeclare.setClientType(declare.getClientType());
        userSignDeclareManager.create(userDeclare);
        userTokenHelper.updateTokenCache(token, declare.getClientType());
        userDeclare.setIsSigned(true);
        
        UserLog userLog = UserLogType.getUserLog(userToken);
        userLogService.saveUserLog(userLog, UserLogType.KEY_USER_SIGN, new String[]{declare.getClientType()});
        return new ResponseEntity<UserSignDeclare>(userDeclare, HttpStatus.OK);
    }
    
    private UserSignDeclare buildDeclare(UserToken userToken)
    {
        UserSignDeclare declare = new UserSignDeclare();
        declare.setAccountId(userToken.getAccountId());
        declare.setUserId(userToken.getId());
        declare.setCloudUserId(userToken.getCloudUserId());
        declare.setIsSigned(true);
        return declare;
    }
    
    private static String getDeviceType(byte deviceType)
    {
        if (deviceType == Terminal.CLIENT_TYPE_ANDROID)
        {
            return Terminal.CLIENT_TYPE_ANDROID_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_IOS)
        {
            return Terminal.CLIENT_TYPE_IOS_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_PC)
        {
            return Terminal.CLIENT_TYPE_PC_STR;
        }
        if (deviceType == Terminal.CLIENT_TYPE_WEB)
        {
            return Terminal.CLIENT_TYPE_WEB_STR;
        }
        return Terminal.CLIENT_TYPE_WEB_STR;
    }
}
