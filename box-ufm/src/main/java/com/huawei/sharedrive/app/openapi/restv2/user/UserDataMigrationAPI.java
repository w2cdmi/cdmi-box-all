/**
 * 
 */
package com.huawei.sharedrive.app.openapi.restv2.user;

import io.swagger.annotations.Api;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.InvalidSpaceStatusException;
import com.huawei.sharedrive.app.exception.NoSuchReginException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.manager.UserDataMigrationTaskManager;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.user.MigrationProccessResponse;
import com.huawei.sharedrive.app.openapi.domain.user.MigrationRequest;
import com.huawei.sharedrive.app.openapi.domain.user.MigrationResponse;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;

import pw.cdmi.core.utils.MethodLogAble;

@Controller
@RequestMapping(value = "/api/v2/migration")
@Api(hidden = true)
public class UserDataMigrationAPI
{
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private UserDataMigrationTaskManager userDataMigrationTaskManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    /**
     * 检查健全
     * @param authorization
     * @param date
     * @return
     */
    private UserToken checkAuth(String authorization, String date)
    {
        
        UserToken userToken = new UserToken();
        
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            Account account = userTokenHelper.checkAccountToken(authorization, date);
            String[] akArray = authorization.split(",");
            userToken.setLoginName(akArray[1]);
            userToken.setAppId(account.getAppId());
            userToken.setId(account.getId());
            userToken.setAuth(authorization);
            userToken.setAccountVistor(account);
        }
        else
        {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
            userToken.setAuth(authorization);
        }
        
        return userToken;
    }
    
    /**
     * 检查数据迁移请求参数
     * 
     * @param request
     * @return
     */
    private User checkMigrationRequestParameter(long cloudUserId, int regionId)
    {
        if (cloudUserId < 0 || regionId < 0)
        {
            throw new InvalidParamException("cloudUserId"+cloudUserId +"regionId"+regionId);
        }
        
        User user = checkUser(cloudUserId);
        
        Region region = regionService.getRegion(regionId);
        if (null == region)
        {
            throw new NoSuchReginException();
        }
        
        return user;
    }

    private User checkUser(long cloudUserId)
    {
        User user = userService.get(cloudUserId);
        if (null == user || user.getStatus().equals(User.USER_DELETING))
        {
            throw new NoSuchUserException("No such user: " + cloudUserId);
        }
        if (user.getStatus().equals(User.STATUS_DISABLE_INTEGER))
        {
            throw new InvalidSpaceStatusException("User space status is abnormal:" + user.getStatus());
        }
        return user;
    }
    
    /**
     * 创建用户数据迁移任务
     * 
     * @param authorization
     * @param date
     * @param request
     * @return
     * @throws BaseRunException
     */
    @MethodLogAble
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<MigrationResponse> createUserDataMigration(@RequestHeader("Authorization")
    String authorization, @RequestHeader("Date")
    String date, @RequestBody
    MigrationRequest request) throws BaseRunException
    {
        
        boolean bCreate = false;
        UserToken userToken = checkAuth(authorization, date);
        
        try
        {
            User user = checkMigrationRequestParameter(request.getOwnerId(), request.getDestRegionId());
            userToken.setCloudUserId(user.getId());
            
            UserDataMigrationTask task = userDataMigrationTaskManager.createDataMigrationTask(userToken,
                user,
                request.getDestRegionId());
            
            bCreate = true;
            return new ResponseEntity<MigrationResponse>(new MigrationResponse(task), HttpStatus.CREATED);
            
        }
        finally
        {
            String[] logMsgs = new String[]{String.valueOf(userToken.getId()),
                StringUtils.trimToEmpty(userToken.getAppId()), String.valueOf(request.getOwnerId())};
            if (bCreate)
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.CREATE_USER_MIGRATION_SUCCESS,
                    logMsgs,
                    null);
            }
            else
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.CREATE_USER_MIGRATION_ERR,
                    logMsgs,
                    null);
            }
        }
        
    }
    
    /**
     * 删除用户迁移任务
     * 
     * @param userId
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
    @MethodLogAble
    @RequestMapping(value = "/{ownerId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteUserDataMigration(@PathVariable
    long ownerId, @RequestHeader("Authorization")
    String authorization, @RequestHeader("Date")
    String date) throws BaseRunException
    {
        boolean bOperSucceed = false;
        UserToken userToken = new UserToken();
        try
        {
            
            userToken = checkAuth(authorization, date);
            
            User user = checkUser(ownerId);
            
            userDataMigrationTaskManager.deleteDataMigrationTask(userToken, user);
            
            return new ResponseEntity<String>(HttpStatus.OK);
            
        }
        finally
        {
            String[] logMsgs = new String[]{String.valueOf(userToken.getId()),
                StringUtils.trimToEmpty(userToken.getAppId()), String.valueOf(ownerId)};
            
            if (bOperSucceed)
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.DELETE_USER_MIGRATION_SUCCESS,
                    logMsgs,
                    null);
            }
            else
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.DELETE_USER_MIGRATION_ERR,
                    logMsgs,
                    null);
            }
            
        }
        
    }
    
    /**
     * 获取数据迁移任务进度
     * 
     * @param userId
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
    @MethodLogAble
    @RequestMapping(value = "/{ownerId}/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<MigrationProccessResponse> getUserDataMigration(@PathVariable
    long ownerId, @RequestHeader("Authorization")
    String authorization, @RequestHeader("Date")
    String date) throws BaseRunException
    {
        
        boolean bOperSucceed = false;
        UserToken userToken = new UserToken();
        try
        {
            
            userToken = checkAuth(authorization, date);
            
            User user = checkUser(ownerId);
            
            UserDataMigrationTask task = userDataMigrationTaskManager.getDataMigrationTask(userToken, user);
            
            bOperSucceed = true;
            return new ResponseEntity<MigrationProccessResponse>(new MigrationProccessResponse(task),
                HttpStatus.OK);
            
        }
        finally
        {
            
            String[] logMsgs = new String[]{String.valueOf(userToken.getId()),
                StringUtils.trimToEmpty(userToken.getAppId()), String.valueOf(ownerId)};
            
            if (bOperSucceed)
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.GET_USER_MIGRATION_SUCCESS,
                    logMsgs,
                    null);
            }
            else
            {
                fileBaseService.sendINodeEvent(userToken,
                    EventType.USER_DATA_MIGRATION,
                    null,
                    null,
                    UserLogType.GET_USER_MIGRATION_ERR,
                    logMsgs,
                    null);
            }
            
        }
        
    }
    
}
