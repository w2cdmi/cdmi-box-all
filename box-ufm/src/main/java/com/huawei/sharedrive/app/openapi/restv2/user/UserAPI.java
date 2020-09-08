/**
 * 
 */
package com.huawei.sharedrive.app.openapi.restv2.user;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.authapp.service.impl.AuthAppServiceImpl;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.AuthFailedException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ConflictUserException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchReginException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.user.ListUserRequest;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.domain.UserList;
import com.huawei.sharedrive.app.user.manager.UserManager;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.PatternRegUtil;

import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/api/v2/users")
@Api(tags = {"操作用户接口"})
public class UserAPI
{
    
    /** 邮箱正则表达式 */
    public final static String EMAIL_RULE = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    
    private static final byte DEFAULT_REGION = -1;
    
    private static final long FAIL_CREATE_USER = 0;
    
    private static final int USER_NAME_LENGTH = 127;
    
    private static final int USER_EMAIL_LENGTH = 255;
    
    private static final int LOGIN_NAME_LENGTH = 127;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private ShareService shareService;
    
    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    /**
     * 添加用户
     * 
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> addUser(
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        @RequestBody RestUserCreateRequest ruser) throws BaseRunException
    {
        
        if (StringUtils.isBlank(authorization))
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        String[] akArr = authorization.split(",");
        String appId = null;
        Account account = userTokenHelper.checkAccountToken(authorization, date);
        try
        {
            // 参数校验
            userService.checkUserAddParamter(ruser);
            appId = account.getAppId();
            ruser = userManager.addUser(ruser, akArr, account);
            return new ResponseEntity<RestUserCreateRequest>(ruser, HttpStatus.CREATED);
        }
        catch (RuntimeException t)
        {
            UserToken userToken = new UserToken();
            userToken.setAppId(appId);
            userToken.setId(FAIL_CREATE_USER);
            if (akArr.length >= 2)
            {
                userToken.setLoginName(akArr[1]);
            }
            String[] logMsgs = new String[]{StringUtils.trimToEmpty(appId)};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_USER_ERR,
                logMsgs,
                null);
            throw t;
        }
    }
    
    
    /**
     * 添加用户
     * 
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/addPersonUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> addPersonUser(
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        @RequestBody RestUserCreateRequest ruser) throws BaseRunException
    {
        
        if (StringUtils.isBlank(authorization))
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        String[] akArr = authorization.split(",");
        userTokenHelper.checkAppAndAccountToken(authorization, date);
        try
        {
        	ruser.setRegionId((byte) regionService.getDefaultRegion().getId());
            userService.checkUserAddParamter(ruser);
            ruser = userManager.addWxUser(ruser,ruser.getDescription(), akArr);
            return new ResponseEntity<RestUserCreateRequest>(ruser, HttpStatus.CREATED);
        }
        catch (RuntimeException t)
        {
            UserToken userToken = new UserToken();
            userToken.setAppId(ruser.getDescription());
            userToken.setId(FAIL_CREATE_USER);
            if (akArr.length >= 2)
            {
                userToken.setLoginName(akArr[1]);
            }
            String[] logMsgs = new String[]{StringUtils.trimToEmpty(ruser.getDescription())};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_USER_ERR,
                logMsgs,
                null);
            throw t;
        }
    }
    
    /**
     * 更新用戶信息
     * 
     * @return
     */
    @RequestMapping(value = "/{userId}/update", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> updateWxUser(@PathVariable long userId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        @RequestBody RestUserCreateRequest ruser) throws BaseRunException
    {
        if (StringUtils.isBlank(authorization))
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        String[] akArr = authorization.split(",");
        userTokenHelper.checkAppAndAccountToken(authorization, date);
        
        User user = null;
        try
        {
            // 参数校验
//            checkUpdateUserParamter(ruser);
            
            // 用户校验
            user = userService.get(userId);
            if (null == user)
            {
                throw new NoSuchUserException();
            }
            checkUpdateUserRegion(ruser, user);
            checkLoginName(ruser, user, user.getAccountId());
            String ruserLoginName = ruser.getLoginName();
            String ruserName = ruser.getName();
            
            String userLoginName = user.getLoginName();
            String userName = user.getName();
            userService.transUser(ruser, user);
            userService.update(user);
            if ((ruserLoginName != null && !ruserLoginName.equals(userLoginName))
                || (ruserName != null && !ruserName.equals(userName)))
            {
                shareService.updateUsername(user);
                teamSpaceMembershipService.updateUsername(user, TeamSpaceMemberships.TYPE_USER);
                updateGroupShipInfo(user);
            }
            transRequestUser(ruser, user);
            return new ResponseEntity<RestUserCreateRequest>(ruser, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            if (user != null)
            {
                UserToken userToken = new UserToken();
                userToken.setAppId(user.getAppId());
                userToken.setId(userId);
                if (null != akArr && akArr.length >= 2)
                {
                    userToken.setLoginName(akArr[1]);
                }
                String[] logParams = new String[]{String.valueOf(userId),
                    StringUtils.trimToEmpty(user.getAppId())};
                String keyword = StringUtils.trimToEmpty(user.getLoginName());
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.UPDATE_USER_ERR,
                    logParams,
                    keyword);
            }
            throw t;
        }
    }
    
    /**
     * 删除用户
     * 
     * @param userId
     * @param authorization
     * @param date
     * @return
     * @throws BaseRunException
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@PathVariable long userId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        if (StringUtils.isBlank(authorization))
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        String[] akArr = authorization.split(",");
        Account account = userTokenHelper.checkAccountToken(authorization, date);
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(userId);
            userManager.deleteUserById(userId, akArr);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            UserToken userToken = new UserToken();
            userToken.setAppId(account.getAppId());
            userToken.setId(userId);
            if (akArr.length >= 2)
            {
                userToken.setLoginName(akArr[1]);
            }
            String[] logParams = new String[]{String.valueOf(userId),
                StringUtils.trimToEmpty(account.getAppId())};
            String keyword = String.valueOf(account.getId());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_USER_ERR,
                logParams,
                keyword);
            throw t;
        }
    }
    
    /**
     * 获取用户列表
     * 
     * @return
     */
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ApiOperation(value = "获得用户列表")
    @ResponseBody
    public ResponseEntity<UserList> getOrderedUserList(@RequestHeader("Authorization") String authorization,
        @RequestHeader("Date") String date, @RequestBody(required = false) ListUserRequest request)
        throws BaseRunException
    {
        
        request.checkParameter();
        
        User filter = new User();
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            Account account = userTokenHelper.checkAccountToken(authorization, date);
            filter.setAccountId(account.getId());
            filter.setAppId(account.getAppId());
            filter.setType(User.USER_TYPE_USER);
        }
        else
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        UserToken userToken = new UserToken();
        String[] akArray = authorization.split(",");
        userToken.setLoginName(akArray[1]);
        UserList userList;
        try
        {
            userList = userService.getOrderedUserList(filter,
                request.getOrder(),
                request.getOffset(),
                request.getLimit());
        }
        catch (RuntimeException e)
        {
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_USER_LIST_ERR,
                null,
                null);
            throw e;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_USER_LIST,
            null,
            null);
        return new ResponseEntity<UserList>(userList, HttpStatus.OK);
        
    }
    
    /**
     * 获取用户详情
     * 
     * @return
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取用户详情")
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> getUser(@PathVariable long userId,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date) throws BaseRunException
    {
        User user = null;
        try
        {
            if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
                || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
            {
                userTokenHelper.checkAccountToken(authorization, date);
            }
            else
            {
                UserToken userToken = userTokenHelper.checkTokenAndGetUserForV2(authorization, null);
                if (userToken.getCloudUserId().intValue() != userId)
                {
                    throw new AuthFailedException("Bad app authorization: " + authorization);
                }
            }
            user = userService.get(userId);
            if (null == user)
            {
                throw new NoSuchUserException();
            }
            RestUserCreateRequest restUserCreateRequest = new RestUserCreateRequest();
            transRequestUser(restUserCreateRequest, user);
            
            UserStatisticsInfo userInfo = spaceStatisticsService.updateUserWithoutCacheInfo(userId,
                user.getAccountId());
            restUserCreateRequest.setSpaceUsed(userInfo.getSpaceUsed());
            restUserCreateRequest.setFileCount(userInfo.getFileCount());
            return new ResponseEntity<RestUserCreateRequest>(restUserCreateRequest, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            if (user != null)
            {
                UserToken userToken = new UserToken();
                userToken.setAppId(user.getAppId());
                userToken.setId(userId);
                String[] logMsgs = new String[]{String.valueOf(userId),
                    StringUtils.trimToEmpty(user.getAppId())};
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_USER_ERR,
                    logMsgs,
                    null);
            }
            throw t;
        }
    }
    
    /**
     * 获取用户详情
     * 
     * @return
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户详情")
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> getUser(@RequestBody RestUserCreateRequest ruser,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date)
        throws BaseRunException
    {
        User user = null;
        Account account = null;
        try
        {
            if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
                || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
            {
                account = userTokenHelper.checkAccountToken(authorization, date);
            }else if (authorization.startsWith(UserTokenHelper.APPLICATION_PREFIX)) {

                userTokenHelper.checkAppAndAccountToken(authorization, date);
                account=new Account();
                account.setId(0);
            }else
            {
                throw new AuthFailedException("Bad app authorization: " + authorization);
            }
            user = userService.getUserByLoginNameAccountId(ruser.getLoginName(), account.getId());
            if (null == user)
            {
                throw new NoSuchUserException();
            }
            RestUserCreateRequest restUserCreateRequest = new RestUserCreateRequest();
            transRequestUser(restUserCreateRequest, user);
            UserStatisticsInfo userInfo = spaceStatisticsService.updateUserWithoutCacheInfo(user.getId(),
                user.getAccountId());
            restUserCreateRequest.setSpaceUsed(userInfo.getSpaceUsed());
            restUserCreateRequest.setFileCount(userInfo.getFileCount());
            return new ResponseEntity<RestUserCreateRequest>(restUserCreateRequest, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            if (account != null && ruser != null)
            {
                UserToken userToken = new UserToken();
                userToken.setAppId(account.getAppId());
                userToken.setId(account.getId());
                String[] logMsgs = new String[]{String.valueOf(ruser.getLoginName()),
                    StringUtils.trimToEmpty(account.getAppId())};
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.GET_USER_POST_ERR,
                    logMsgs,
                    null);
            }
            throw t;
        }
    }
    
    /**
     * 更新用戶信息
     * 
     * @return
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新用户信息")
    @ResponseBody
    public ResponseEntity<RestUserCreateRequest> updateUser(@PathVariable long userId,
        @RequestHeader("Authorization") String authorization, @RequestHeader("Date") String date,
        @RequestBody RestUserCreateRequest ruser) throws BaseRunException
    {
        String[] akArr = null;
        if (StringUtils.isNotBlank(authorization))
        {
            akArr = authorization.split(",");
        }
        
        User user = null;
        try
        {
            // 参数校验
            checkUpdateUserParamter(ruser);
            
            checkToken(authorization, date);
            
            // 用户校验
            user = userService.get(userId);
            if (null == user)
            {
                throw new NoSuchUserException();
            }
            checkUpdateUserRegion(ruser, user);
            checkLoginName(ruser, user, user.getAccountId());
            String ruserLoginName = ruser.getLoginName();
            String ruserName = ruser.getName();
            
            String userLoginName = user.getLoginName();
            String userName = user.getName();
            userService.transUser(ruser, user);
            // 更新用戶
            if (null != akArr && akArr.length >= 2)
            {
                user.setLabel(akArr[1]);
            }
            userService.update(user);
            if ((ruserLoginName != null && !ruserLoginName.equals(userLoginName))
                || (ruserName != null && !ruserName.equals(userName)))
            {
                shareService.updateUsername(user);
                teamSpaceMembershipService.updateUsername(user, TeamSpaceMemberships.TYPE_USER);
                updateGroupShipInfo(user);
            }
            transRequestUser(ruser, user);
            return new ResponseEntity<RestUserCreateRequest>(ruser, HttpStatus.OK);
        }
        catch (RuntimeException t)
        {
            if (user != null)
            {
                UserToken userToken = new UserToken();
                userToken.setAppId(user.getAppId());
                userToken.setId(userId);
                if (null != akArr && akArr.length >= 2)
                {
                    userToken.setLoginName(akArr[1]);
                }
                String[] logParams = new String[]{String.valueOf(userId),
                    StringUtils.trimToEmpty(user.getAppId())};
                String keyword = StringUtils.trimToEmpty(user.getLoginName());
                fileBaseService.sendINodeEvent(userToken,
                    EventType.OTHERS,
                    null,
                    null,
                    UserLogType.UPDATE_USER_ERR,
                    logParams,
                    keyword);
            }
            throw t;
        }
    }
    
    private void checkLoginName(RestUserCreateRequest ruser, User curDbUser, long accountId)
        throws ConflictUserException
    {
        if (StringUtils.isEmpty(ruser.getLoginName()))
        {
            return;
        }
        if (StringUtils.equalsIgnoreCase(ruser.getLoginName(), curDbUser.getLoginName()))
        {
            return;
        }
        if (null != userService.getUserByLoginNameAccountId(ruser.getLoginName(), accountId))
        {
            throw new ConflictUserException("Exist same loginName user");
        }
    }
    
    private void checkToken(String authorization, String date)
    {
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            userTokenHelper.checkAccountToken(authorization, date);
        }
        else
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
    }
    
    /**
     * 更新用户
     * 
     * @param user
     * @throws InvalidParamException
     */
    private void checkUpdateUserParamter(RestUserCreateRequest user) throws InvalidParamException
    {
        String loginName = user.getLoginName();
        String name = user.getName();
        
        // 登录名、用户名参数校验
        if (StringUtils.isNotBlank(loginName) && loginName.length() > LOGIN_NAME_LENGTH)
        {
            throw new InvalidParamException();
        }
        if (StringUtils.isNotBlank(name) && name.length() > USER_NAME_LENGTH)
        {
            throw new InvalidParamException();
        }
        // 邮箱参数校验
        if (StringUtils.isNotBlank(user.getEmail()) && user.getEmail().length() > USER_EMAIL_LENGTH)
        {
            throw new InvalidParamException();
        }
        if (StringUtils.isNotBlank(user.getEmail()))
        {
            PatternRegUtil.checkMailLegal(user.getEmail());
        }
        // 状态参数校验
        if (user.getStatus() != null && user.getStatus() != 0 && user.getStatus() != 1)
        {
            throw new InvalidParamException();
        }
        if (user.getSpaceQuota() != null && user.getSpaceQuota() < -1)
        {
            throw new InvalidParamException("error quoto " + user.getSpaceQuota());
        }
        // 文件最大版本数校验
        Integer maxVersions = user.getMaxVersions();
        if (maxVersions != null && (maxVersions <= 0 && maxVersions != User.VERSION_NUM_UNLIMITED))
        {
            throw new InvalidParamException("Invalid max versions: " + maxVersions);
        }
    }
    
    /**
     * 检查更新用户的存储区域参数
     * 
     * @throws BaseRunException
     */
    private void checkUpdateUserRegion(RestUserCreateRequest requestUser, User dbUser)
        throws BaseRunException
    {
        if (null == requestUser.getRegionId())
        {
            return;
        }
        
        if (dbUser.getRegionId() == DEFAULT_REGION)
        {
            if (requestUser.getRegionId() == DEFAULT_REGION)
            {
                return;
            }
            Region region = regionService.getRegion(requestUser.getRegionId());
            if (null == region)
            {
                throw new NoSuchReginException();
            }
            dbUser.setRegionId(requestUser.getRegionId());
        }
    }
    
    private void transRequestUser(RestUserCreateRequest rUser, User user)
    {
        rUser.setEmail(user.getEmail());
        rUser.setLoginName(user.getLoginName());
        rUser.setName(user.getName());
        rUser.setSpaceQuota(user.getSpaceQuota());
        rUser.setStatus(Byte.parseByte(user.getStatus()));
        rUser.setId(user.getId());
        rUser.setRegionId((byte) user.getRegionId());
//        rUser.setDescription(user.getDepartment());
        rUser.setMaxVersions(user.getMaxVersions());
        rUser.setCreatedAt(user.getCreatedAt());
    }
    
    private void updateGroupShipInfo(User user)
    {
        List<GroupMemberships> memberships = groupMembershipsService.getUserList(null,
            null,
            user.getId(),
            GroupConstants.GROUP_USERTYPE_USER,
            null);
        
        for (GroupMemberships gm : memberships)
        {
            gm.setLoginName(user.getLoginName());
            gm.setUsername(user.getName());
            groupMembershipsService.updateNameForUser(gm);
        }
    }
}
