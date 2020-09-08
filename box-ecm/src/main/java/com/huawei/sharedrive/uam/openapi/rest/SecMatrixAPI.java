package com.huawei.sharedrive.uam.openapi.rest;

import java.util.List;

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

import com.huawei.sharedrive.uam.enterprise.domain.SecOperation;
import com.huawei.sharedrive.uam.enterprise.manager.SecMatrixManager;
import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.FileSecResponse;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.GetSecRoleResponse;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.SecJudgeRequest;
import com.huawei.sharedrive.uam.openapi.domain.secmatrix.SecJudgeResponse;
import com.huawei.sharedrive.uam.security.domain.SecretStaff;
import com.huawei.sharedrive.uam.security.service.SecretStaffService;
import com.huawei.sharedrive.uam.util.SecurityConfigConstants;

import pw.cdmi.common.log.UserLog;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/api/v2/security")
public class SecMatrixAPI
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SecMatrixAPI.class);
    
    private final static String X_XEAL_IP = "X-Real-IP";
    
    @Autowired
    private SecMatrixManager secMatrixManager;
    
    @Autowired SecretStaffService  secretStaffService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/roles/{accountId}/{cloudUserId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GetSecRoleResponse> getSecRole(
        @RequestHeader("Authorization") String authorization, @PathVariable("accountId") int accountId,
        @PathVariable("cloudUserId") long cloudUserId) throws BaseRunException
    {
        GetSecRoleResponse roleResponse = null;
        UserLog userLog = null;
        String[] paras = {accountId + "", cloudUserId + ""};
        try
        {
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
            userLog = UserLogType.getUserLog(userToken);
            roleResponse = secMatrixManager.getSecRole(accountId, cloudUserId);
        }
        catch (RuntimeException e)
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_GET_SECURITY_ROLES_ERR, paras);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_GET_SECURITY_ROLES, paras);
        return new ResponseEntity<GetSecRoleResponse>(roleResponse, HttpStatus.OK);
    }
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @RequestMapping(value = "/judge", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SecJudgeResponse> judge(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = X_XEAL_IP, required = false) String realIp,
        @RequestBody SecJudgeRequest judgeRequest) throws BaseRunException
    {
        LOGGER.info("X_XEAL_IP:" + realIp);
        
        SecJudgeResponse secResponse = new SecJudgeResponse();
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserLog userLog = UserLogType.getUserLog(userToken);
        
        // check operation
        SecOperation op = SecOperation.getSecOperation(judgeRequest.getOperation());
        if (op == null && !SecurityConfigConstants.OPERATION_COPY.equals(judgeRequest.getOperation()))
        {
            throw new InvalidParamterException();
        }
        // check realIp
        if (StringUtils.isNotBlank(realIp) && !IpUtils.isIPv4LiteralAddress(realIp))
        {
            throw new InvalidParamterException("invalid ip address");
        }
        
        Integer secRole = userToken.getRoleId();
        // 当角色为‘默认角色’时uam.user_account_xxx表中对应的roleid值为0, 与uam.access_config及uam。access_space_config表中saferoleid值不对应
        // 当角色为‘默认角色’在uam.access_config及uam。access_space_config表中saferoleid一定为-1
        if(0 == Integer.compare(0, secRole)) {
        	secRole = -1;
        } 
        boolean fileVali = secMatrixManager.judgeFileMatrix(userToken.getAccountId(),
            secRole,
            judgeRequest.getSpaceSecRoleId(),
            realIp,
            judgeRequest.getSecurityId(),
            judgeRequest.getOperation(),
            userToken.getDeviceType());
        if (fileVali)
        {
            fileVali = secMatrixManager.judgeSpaceMatrix(userToken.getAccountId(),
                secRole,
                realIp,
                judgeRequest.getSpaceSecRoleId(),
                judgeRequest.getOperation(),
                userToken.getDeviceType());
        }
        if (fileVali)
        {
            fileVali = secMatrixManager.judgeFileCopyMatrix(userToken.getAccountId(),
                judgeRequest.getSpaceSecRoleId(),
                judgeRequest.getTargetSecRoleId(),
                judgeRequest.getOperation());
        }
        if (!fileVali)
        {
            
            LOGGER.error("file validate result is false,accountId=" + userToken.getAccountId() + ",secRole="
                + secRole + ",realIp=" + realIp + ",securityId=" + judgeRequest.getSecurityId()
                + ",operation=" + judgeRequest.getOperation() + ",deviceType=" + userToken.getDeviceType()
                + ",spaceSecRoleId=" + judgeRequest.getSpaceSecRoleId() + ",targetSecRoleID="
                + judgeRequest.getTargetSecRoleId());
            userLogService.saveUserLog(userLog, UserLogType.KEY_SECUTIRY_MATRIX_ERR, null);
            
        }
        else
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_SECUTIRY_MATRIX, null);
            
        }
        secResponse.setAllowed(fileVali);
        return new ResponseEntity<SecJudgeResponse>(secResponse, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/level", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<FileSecResponse> getFileSecLevel(
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = X_XEAL_IP, required = false) String realIp) throws BaseRunException
    {
        // check realIp
        FileSecResponse secResponse;
        UserLog userLog = null;
        try
        {
            if (StringUtils.isNotBlank(realIp) && !IpUtils.isIPv4LiteralAddress(realIp))
            {
                throw new InvalidParamterException("invalid ip address");
            }
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
            userLog = UserLogType.getUserLog(userToken);
            Integer secRole = userToken.getRoleId();
            secResponse = secMatrixManager.getFileSecId(userToken.getAccountId(),
                secRole,
                realIp,
                userToken.getDeviceType());
        }
        catch (RuntimeException e)
        {
            userLogService.saveUserLog(userLog, UserLogType.KEY_GET_FILE_LEVEL_ERR, null);
            throw e;
        }
        userLogService.saveUserLog(userLog, UserLogType.KEY_GET_FILE_LEVEL, null);
        return new ResponseEntity<FileSecResponse>(secResponse, HttpStatus.OK);
    }
    
    
   
    
}
