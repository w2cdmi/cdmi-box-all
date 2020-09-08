package com.huawei.sharedrive.cloudapp.cmb.openapi.rest;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.cloudapp.authapp.service.AuthAppService;
import com.huawei.sharedrive.cloudapp.client.domain.user.RestUserV2loginRsp;
import com.huawei.sharedrive.cloudapp.cmb.openapi.rest.domain.CMBSsoAPIRequest;
import com.huawei.sharedrive.cloudapp.event.domain.EventType;
import com.huawei.sharedrive.cloudapp.exception.BaseRunException;
import com.huawei.sharedrive.cloudapp.httpclient.rest.request.RestLoginResponse;
import com.huawei.sharedrive.cloudapp.oauth2.domain.UserToken;
import com.huawei.sharedrive.cloudapp.user.service.UserLoginService;
import com.huawei.sharedrive.cloudapp.user.service.UserService;
import com.huawei.sharedrive.cloudapp.user.service.impl.UserServiceImpl;
import com.huawei.sharedrive.cloudapp.utils.RequestUtils;

@Controller
@RequestMapping(value = "/api/v2/cmb/sso")
public class CMBUserAPIController
{
    
    @Autowired
    private UserLoginService userLoginService;
    
    @Autowired
    private UserCMBLoginService userCMBLoginService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthAppService authAppService;
    
    /**
     * login
     * 
     * @param userlogin
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<RestUserV2loginRsp> userLoginByPost(@RequestBody CMBSsoAPIRequest userlogin,
        HttpServletRequest request) throws BaseRunException
    {
        userlogin.checkParameter(request);
        String data = userlogin.getData();
        String token = userlogin.getToken();
        String appId = userlogin.getAppId();
        String deviceTypeStr = request.getHeader("x-device-type");
        String deviceSN = request.getHeader("x-device-sn");
        String deviceOS = request.getHeader("x-device-os");
        String deviceName = request.getHeader("x-device-name");
        String deviceAgent = request.getHeader("x-client-version");
        if (StringUtils.isBlank(appId))
        {
            appId = authAppService.getCurrentAppId();
        }
        String remoteAddr = RequestUtils.getRealIP(request);
        int deviceType = UserServiceImpl.transDeviceType(deviceTypeStr);
        
        UserToken userToken = new UserToken();
        userToken.setDeviceSn(deviceSN);
        userToken.setDeviceName(deviceName);
        userToken.setDeviceType(deviceType);
        userToken.setDeviceOS(deviceOS);
        userToken.setDeviceAgent(deviceAgent);
        userToken.setDeviceAddress(remoteAddr);
        userToken.setProxyAddress(RequestUtils.getProxyIP(request));
        RestLoginResponse restLoginResponse = userCMBLoginService.authCmbUser(data, token, appId, userToken);
        String authToken = restLoginResponse.getToken();
        String refreshToken = restLoginResponse.getRefreshToken();
        transUser(userToken, restLoginResponse);
        userToken.setToken(authToken);
        userToken.setRefreshToken(refreshToken);
        Date expire = new Date(System.currentTimeMillis() + restLoginResponse.getTimeout() * 1000L);
        userToken.setExpiredAt(expire);
        RestUserV2loginRsp userRsp = new RestUserV2loginRsp();
        userRsp.setToken(authToken);
        userRsp.setRefreshToken(refreshToken);
        userRsp.setTimeout(restLoginResponse.getTimeout());
        userRsp.setLoginName(userToken.getLoginName());
        userRsp.setCloudUserId(userToken.getCloudUserId());
        userRsp.setUserId(userToken.getId());
        userRsp.setUploadQos(restLoginResponse.getUploadQos());
        userRsp.setDownloadQos(restLoginResponse.getDownloadQos());
        userRsp.setAccountId(restLoginResponse.getAccountId());
        userRsp.setEnterpriseId(restLoginResponse.getEnterpriseId());
        userRsp.setDomain(restLoginResponse.getDomain());
        userService.createEvent(userToken, EventType.USER_LOGIN, userToken.getId());
        return new ResponseEntity<RestUserV2loginRsp>(userRsp, HttpStatus.OK);
    }
    
    private void transUser(UserToken userToken, RestLoginResponse restLoginResponse)
    {
        userToken.setId(restLoginResponse.getUserId());
        userToken.setLoginName(restLoginResponse.getLoginName());
        userToken.setDomain(restLoginResponse.getDomain());
        userToken.setCloudUserId(restLoginResponse.getCloudUserId());
        userToken.setEnterpriseId(restLoginResponse.getAccountId());
        userToken.setAccountId(restLoginResponse.getAccountId());
    }
}
