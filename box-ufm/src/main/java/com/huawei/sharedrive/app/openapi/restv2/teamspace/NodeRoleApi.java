package com.huawei.sharedrive.app.openapi.restv2.teamspace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.acl.domain.ResourceRole;
import com.huawei.sharedrive.app.acl.service.ResourceRoleService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestNodeRoleInfo;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.Constants;

/**
 * 系统资源角色对外接口
 * 
 * @author t00159390
 * 
 */
@Controller
@RequestMapping(value = "/api/v2/roles")
public class NodeRoleApi
{
    
    @Autowired
    private ResourceRoleService resourceRoleService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    @Autowired
    private FileBaseService fileBaseService;
    private static final List<String> PLUGIN_APPID_SET = new ArrayList<String>(2);
    static
    {
        PLUGIN_APPID_SET.add(Constants.APPID_PPREVIEW);
        PLUGIN_APPID_SET.add(Constants.APPID_SECURITYSCAN);
    }
    
    /**
     * 列举系统资源角色配置
     * 
     * @param token
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RestNodeRoleInfo>> list(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date) throws BaseRunException
    {
        UserToken userToken = new UserToken();
        
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            userTokenHelper.checkAccountToken(authorization, date);
            String[]akArray=authorization.split(",");
            userToken.setLoginName(akArray[1]);
        }
        else if (authorization.startsWith(UserTokenHelper.APPLICATION_PREFIX))
        {
            String appId = userTokenHelper.checkAppSystemToken(authorization, date);
            String[]akArray=authorization.split(",");
            userToken.setLoginName(akArray[1]);
            userToken.setAppId(appId);
            if (PLUGIN_APPID_SET.contains(appId))
            {
                throw new ForbiddenException("app " + appId + " is not allowed to access this method");
            }
        }
        else
        {
            userToken=userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
        }
        ResponseEntity<List<RestNodeRoleInfo>> responseEntity=null;
        try
        {
            responseEntity =listResourceRole();
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.LIST_ROLE_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_ROLE,
            null,
            null);
        return responseEntity;
    }
    
    private ResponseEntity<List<RestNodeRoleInfo>> listResourceRole()
    {
        List<ResourceRole> resourceRoleList = resourceRoleService.listResourceRoleSetting();
        
        List<RestNodeRoleInfo> restNodeRoleList = new ArrayList<RestNodeRoleInfo>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        RestNodeRoleInfo temp = null;
        for (ResourceRole role : resourceRoleList)
        {
            temp = new RestNodeRoleInfo(role);
            restNodeRoleList.add(temp);
        }
        
        return new ResponseEntity<List<RestNodeRoleInfo>>(restNodeRoleList, HttpStatus.OK);
    }
    
}
