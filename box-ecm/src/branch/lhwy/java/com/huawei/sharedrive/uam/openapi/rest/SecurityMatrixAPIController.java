package com.huawei.sharedrive.uam.openapi.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.exception.BaseRunException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.RestSceurityAccreditRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestSecurityAccreditResponse;
import com.huawei.sharedrive.uam.security.service.SecurityMatrixControl;

@Controller
@RequestMapping(value = "/api/v2/security")
public class SecurityMatrixAPIController
{
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private SecurityMatrixControl securityMatrixControl;
    
    /**
     * 
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "accredit", method = RequestMethod.POST)
    public ResponseEntity<RestSecurityAccreditResponse> securityAccredit(
        @RequestBody RestSceurityAccreditRequest securityAccreditRequest,
        @RequestHeader("Authorization") String token, HttpServletRequest request) throws BaseRunException
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(token);
        if (securityAccreditRequest.getVisitCloudUserId() != userToken.getCloudUserId())
        {
            throw new NoSuchUserException();
        }
        userToken.setDeviceType(securityAccreditRequest.getDeviceType());
        userToken.setDeviceAddress(securityAccreditRequest.getClientAddress());
        userToken.setProxyAddress(securityAccreditRequest.getProxyAddress());
        
        RestSecurityAccreditResponse response = new RestSecurityAccreditResponse(securityAccreditRequest);
        long nodeId = securityAccreditRequest.getiNodeId();
        long cloudUserId = securityAccreditRequest.getOnwerCloudUserId();
        String[] permissions = securityAccreditRequest.getPermissions();
        if (securityAccreditRequest.getType() == 0)
        {
            response.setCanAccess(securityMatrixControl.canAccessFolder(nodeId,
                cloudUserId,
                userToken,
                permissions));
        }
        else
        {
            response.setCanAccess(securityMatrixControl.canAccessFile(nodeId,
                cloudUserId,
                userToken,
                permissions));
        }
        return new ResponseEntity<RestSecurityAccreditResponse>(response, HttpStatus.OK);
    }
}
