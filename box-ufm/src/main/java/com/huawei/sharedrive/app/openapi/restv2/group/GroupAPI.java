package com.huawei.sharedrive.app.openapi.restv2.group;

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
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.manager.GroupManager;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.TokenChecker;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.group.GroupList;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrderRequest;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrderUserRequest;
import com.huawei.sharedrive.app.openapi.domain.group.GroupUserList;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupModifyRequest;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupRequest;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

@Controller
@RequestMapping(value = "/api/v2/groups")
public class GroupAPI
{
    
    @Autowired
    private GroupManager groupManager;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    private static final long APP_USER_ID = 1;
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createGroup(@RequestBody RestGroupRequest restGroupRequest,
        @RequestHeader("Authorization") String authToken,
        @RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = null;
        try
        {
            userToken = checkUserTokenAndAppToken(restGroupRequest, authToken, date);
            userTokenHelper.checkUserStatusAndSpace(restGroupRequest.getOwnedBy());
            restGroupRequest.checkCreateParameter();
            if (StringUtils.isEmpty(restGroupRequest.getName()))
            {
                throw new InvalidParamException("null name");
            }
            if (StringUtils.trimToEmpty(restGroupRequest.getName()).length() > 255)
            {
                throw new InvalidParamException("name lenth is more than 255");
            }
            if (StringUtils.trimToEmpty(restGroupRequest.getDescription()).length() > 1023)
            {
                throw new InvalidParamException("getDescription lenth is more than 1023");
            }
            Group group = transGroup(restGroupRequest, userToken);
            group = groupManager.createGroup(userToken, group, restGroupRequest.getOwnedBy());
            return new ResponseEntity<RestGroup>(new RestGroup(group), HttpStatus.CREATED);
        }
        catch (Exception t)
        {
            String keyword = StringUtils.trimToEmpty(restGroupRequest != null ? restGroupRequest.getName()
                : null);
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_GROUP_ERR,
                null,
                keyword,
                null);
            throw t;
        }
    }
    
    @RequestMapping(value = "{groupId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGroup(@RequestHeader("Authorization") String authToken,
        @RequestHeader(value = "Date", required = false) String date, @PathVariable Long groupId)
    {
        UserToken userToken = null;
        try
        {
            userToken = checkUserTokenAndAppToken(authToken, date);
            FilesCommonUtils.checkNonNegativeIntegers(groupId);
            groupManager.deleteGroup(userToken, groupId);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (Exception t)
        {
            String[] logParams = new String[]{String.valueOf(groupId)};
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_GROUP_ERR,
                logParams,
                null,
                groupId);
            throw t;
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getGroupInfo(@RequestHeader("Authorization") String authToken,
        @PathVariable(value = "id") Long id, @RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = null;
        try
        {
            userToken = checkUserTokenAndAppToken(authToken, date);
            FilesCommonUtils.checkNonNegativeIntegers(id);
            Group group = groupManager.getGroupInfo(userToken, id);
            return new ResponseEntity<RestGroup>(new RestGroup(group), HttpStatus.OK);
        }
        catch (Exception t)
        {
            String[] logParams = new String[]{String.valueOf(id)};
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_GROUP_ERR,
                logParams,
                null,
                null);
            throw t;
        }
    }
    
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listAllGroupsInfo(@RequestHeader("Authorization") String authToken,
        @RequestHeader(value = "Date", required = false) String date,
        @RequestBody GroupOrderRequest orderRequest)
    {
        UserToken userToken = checkUserTokenAndAppToken(authToken, date);
        orderRequest.checkParameter();
        orderRequest.checkOrder();
        GroupList groupList;
        try
        {
            groupList = groupManager.getGroupList(orderRequest.getOrder(),
                orderRequest.getLimit(),
                orderRequest.getOffset(),
                orderRequest.getKeyword(),
                orderRequest.getType(),
                userToken);
        }
        catch (RuntimeException e)
        {
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_GROUP_INFO_ERR,
                null,
                "all",
                null);
            throw e;
        }
        groupManager.sendEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_GROUP_INFO,
            null,
            "all",
            null);
        return new ResponseEntity<GroupList>(groupList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/items", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listUserGroups(@RequestHeader("Authorization") String authToken,
        @RequestBody GroupOrderUserRequest orderRequest,
        @RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = null;
        
        userToken = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
        userTokenHelper.checkUserStatus(null, userToken.getCloudUserId());
        orderRequest.checkParameter();
        orderRequest.checkOrder();
        GroupUserList groupUserList;
        try
        {
            groupUserList = groupManager.getUserGroupList(orderRequest.getOrder(),
                orderRequest.getLimit(),
                orderRequest.getOffset(),
                userToken,
                orderRequest.getKeyword(),
                orderRequest.getType(),
                orderRequest.getListRole());
        }
        catch (RuntimeException e)
        {
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_GROUP_INFO_ERR,
                null,
                userToken.getLoginName(),
                null);
            throw e;
        }
        groupManager.sendEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.GET_GROUP_INFO,
            null,
            userToken.getLoginName(),
            null);
        return new ResponseEntity<GroupUserList>(groupUserList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> modifyGroup(@RequestBody RestGroupModifyRequest restGroupRequest,
        @RequestHeader("Authorization") String authToken, @PathVariable(value = "id") Long id,
        @RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = null;
        Group group = null;
        try
        {
            userToken = checkUserTokenAndAppToken(authToken, date);
            restGroupRequest.checkModifyParameter();
            group = transModifyGroup(restGroupRequest);
            group.setId(id);
            group = groupManager.modifyGroup(userToken, group, restGroupRequest);
            
            return new ResponseEntity<RestGroup>(new RestGroup(group), HttpStatus.OK);
        }
        catch (Exception t)
        {
            String keyword = StringUtils.trimToEmpty(group != null ? group.getName() : null);
            String[] logParams = new String[]{String.valueOf(id)};
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MODIFY_GROUP_ERR,
                logParams,
                keyword,
                null);
            throw t;
        }
    }
    
    @RequestMapping(value = "/checkname", method = RequestMethod.POST)
    public ResponseEntity<?> getGroupByName(@RequestBody RestGroupRequest restGroupRequest, 
    		@RequestHeader("Authorization") String authToken, 
    		@RequestHeader(value = "Date", required = false) String date)
    {
        UserToken userToken = null;
        try
        {
            userToken = checkUserTokenAndAppToken(authToken, date);
            Group group = groupManager.getGroupByName(userToken, restGroupRequest.getName());
            return new ResponseEntity<RestGroup>(new RestGroup(group), HttpStatus.OK);
        }
        catch (Exception t)
        {
            String[] logParams = new String[]{String.valueOf(restGroupRequest.getName())};
            groupManager.sendEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.CREATE_GROUP_ERR,
                logParams,
                null,
                null);
            throw t;
        }
    }
    
    private UserToken checkUserTokenAndAppToken(String authToken, String date)
    {
        UserToken userToken = null;
        if (authToken.startsWith(UserTokenHelper.APP_PREFIX)
            || authToken.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            Account account = userTokenHelper.checkAccountToken(authToken, date);
            userToken = new UserToken();
            userToken.setAccountVistor(account);
            userToken.setId(APP_USER_ID);
            userToken.setAccountId(account.getId());
            userToken.setAppId(account.getAppId());
            userToken.setLoginName(TokenChecker.getAk(authToken));
            userToken.setAccountVistor(account);
        }
        else
        {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
            userTokenHelper.checkUserStatus(null, userToken.getCloudUserId());
        }
        return userToken;
    }
    
    private UserToken checkUserTokenAndAppToken(RestGroupRequest request, String authToken, String date)
    {
        UserToken userToken;
        if (authToken.startsWith(UserTokenHelper.APP_PREFIX)
            || authToken.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            userToken = new UserToken();
            Account account = userTokenHelper.checkAccountToken(authToken, date);
            if (request.getOwnedBy() == null)
            {
                throw new InvalidParamException();
            }
            userToken.setAppId(account.getAppId());
            userTokenHelper.isAppUser(account.getAppId(), request.getOwnedBy());
            userToken.setCloudUserId(request.getOwnedBy());
            userToken.setAccountId(account.getId());
            userToken.setLoginName(TokenChecker.getAk(authToken));
            userToken.setAccountVistor(account);
        }
        else
        {
            userToken = userTokenHelper.checkTokenAndGetUserForV2(authToken, null);
            request.setOwnedBy(userToken.getCloudUserId());
            userTokenHelper.checkUserStatus(null, userToken.getCloudUserId());
        }
        return userToken;
    }
    
    private Group transModifyGroup(RestGroupModifyRequest restGroupRequest)
    {
        Group group = new Group();
        if (restGroupRequest.getDescription() != null)
        {
            group.setDescription(restGroupRequest.getDescription());
        }
        if (restGroupRequest.getMaxMembers() == null
            || restGroupRequest.getMaxMembers() == GroupConstants.REQUEST_MAXMEMBERS)
        {
            group.setMaxMembers(GroupConstants.MAXMEMBERS_DEFAULT);
        }
        else
        {
            group.setMaxMembers(restGroupRequest.getMaxMembers());
        }
        if (restGroupRequest.getName() != null)
        {
            group.setName(restGroupRequest.getName());
        }
        if (restGroupRequest.getType() != null)
        {
            transType(restGroupRequest.getType(), group);
        }
        
        return group;
    }
    
    private Group transGroup(RestGroupRequest restGroupRequest, UserToken userToken)
    {
        Group group = new Group();
        
        group.setAppId(userToken.getAppId());
        group.setModifiedBy(userToken.getCloudUserId());
        group.setDescription(restGroupRequest.getDescription());
        if (restGroupRequest.getMaxMembers() == null
            || restGroupRequest.getMaxMembers() == GroupConstants.REQUEST_MAXMEMBERS)
        {
            group.setMaxMembers(GroupConstants.MAXMEMBERS_DEFAULT);
        }
        else
        {
            group.setMaxMembers(restGroupRequest.getMaxMembers());
        }
        transType(restGroupRequest.getType(), group);
        transStatus(restGroupRequest.getStatus(), group);
        group.setParent(GroupConstants.GROUP_PARENT_DEFAULT);
        group.setName(restGroupRequest.getName());
        group.setOwnedBy(userToken.getCloudUserId());
        return group;
    }
    
    private void transStatus(String status, Group group)
    {
        if (status != null && StringUtils.equals(GroupConstants.STATUS_ENABLE, status))
        {
            group.setStatus(GroupConstants.GROUP_STATUS_DEFAULT);
        }
        else
        {
            group.setStatus(GroupConstants.GROUP_STATUS_DISABLE);
        }
    }
    
    private void transType(String type, Group group)
    {
        if (type != null && StringUtils.equals(GroupConstants.TYPE_PUBLIC, type))
        {
            group.setType(GroupConstants.GROUP_TYPE_PUBLIC);
        }
        else
        {
            group.setType(GroupConstants.GROUP_TYPE_DEFAULT);
        }
    }
}
