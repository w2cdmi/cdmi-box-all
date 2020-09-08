package com.huawei.sharedrive.app.openapi.restv2.user;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserConfig;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserConfigList;
import com.huawei.sharedrive.app.openapi.domain.user.SetUserAttributeRequest;
import com.huawei.sharedrive.app.openapi.domain.user.UserAttribute;
import com.huawei.sharedrive.app.user.domain.UserConfig;
import com.huawei.sharedrive.app.user.service.UserConfigService;
import com.huawei.sharedrive.app.utils.BusinessConstants;

@Controller
@RequestMapping(value = "/api/v2/users")
@Api(tags = { "用户属性操作接口"})
public class UserAttributeAPI
{
    @Autowired
    private UserConfigService userConfigService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @RequestMapping(value = "/{userId}/attributes", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户属性列表")
    public ResponseEntity<RestUserConfigList> getAttribute(@PathVariable Long userId,
        @RequestParam(required = false) String name, @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = checkTokenAndUserStatus(userId, authorization, date);
        
        List<RestUserConfig> configs = new ArrayList<RestUserConfig>(BusinessConstants.INITIAL_CAPACITIES);
        RestUserConfig config;
        String[]description={String.valueOf(userId)};
        try
        {
            // 获取单独配置项
            if (StringUtils.isNotBlank(name))
            {
                UserAttribute attribute = UserAttribute.getUserAttribute(name);
                if (attribute == null)
                {
                    throw new InvalidParamException("Invalid attribute " + name);
                }
                UserConfig userConfig = userConfigService.get(userId, name);
                if (userConfig != null)
                {
                    config = new RestUserConfig(userConfig.getName(), userConfig.getValue());
                    configs.add(config);
                }
            }
            else
            {
                // 获取所有配置
                List<UserConfig> list = userConfigService.list(userId);
                for (UserConfig userConfig : list)
                {
                    config = new RestUserConfig(userConfig.getName(), userConfig.getValue());
                    configs.add(config);
                }
            }
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_USER_ATTRIBUTE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_USER_ATTRIBUTE,
            description,
            null);
        RestUserConfigList configList = new RestUserConfigList(configs);
        return new ResponseEntity<RestUserConfigList>(configList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{userId}/attributes", method = RequestMethod.PUT)
    @ApiOperation(value = "更新用户属性列表")
    public ResponseEntity<String> setAttribute(@PathVariable Long userId,
        @RequestBody SetUserAttributeRequest setConfigRequest,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date)
    {
        // 参数校验
        setConfigRequest.checkParameter();
        
        UserToken userToken = checkTokenAndUserStatus(userId, authorization, date);
        String[] description = {String.valueOf(userId)};
        try
        {
            UserConfig userConfig = new UserConfig(userId, setConfigRequest.getName(),
                setConfigRequest.getValue());
            
            UserConfig config = userConfigService.get(userId, setConfigRequest.getName());
            
            // 新增
            if (config == null)
            {
                userConfigService.create(userConfig);
            }
            else
            {
                // 更新
                userConfigService.update(userId, setConfigRequest.getName(), setConfigRequest.getValue());
            }
            
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.SET_USER_ATTRIBUTE_ERR,
                description,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.SET_USER_ATTRIBUTE,
            description,
            null);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private UserToken checkTokenAndUserStatus(Long userId, String authorization, String date)
    {
        UserToken userToken = new UserToken();
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            Account account = userTokenHelper.checkAccountToken(authorization, date);
            userToken.setAppId(account.getAppId());
            userToken.setId(account.getId());
            String[] akArray = authorization.split(",");
            userToken.setLoginName(akArray[1]);
            // 用户状态校验
            userTokenHelper.checkUserStatus(userId);
            userToken.setAccountVistor(account);
        }
        else
        {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
            if (!userToken.getCloudUserId().equals(userId))
            {
                throw new ForbiddenException("The operation is prohibited");
            }
            // 用户状态校验
            userTokenHelper.checkUserStatus(userToken.getAppId(), userToken.getCloudUserId());
        }
        return userToken;
    }
}
