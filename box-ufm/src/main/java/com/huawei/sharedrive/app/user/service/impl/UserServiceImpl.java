package com.huawei.sharedrive.app.user.service.impl;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.dataserver.domain.Region;
import com.huawei.sharedrive.app.dataserver.service.RegionService;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ConflictUserException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchReginException;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.INodeIdGenerateService;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.openapi.restv2.user.DeleteUserThread;
import com.huawei.sharedrive.app.share.service.LinkService;
import com.huawei.sharedrive.app.share.service.ShareService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.domain.UserList;
import com.huawei.sharedrive.app.user.domain.UserQos;
import com.huawei.sharedrive.app.user.service.UserIdGenerateService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.common.domain.SystemConfig;

import java.util.*;

@Component
public class UserServiceImpl implements UserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private static final byte DEFAULT_REGION = -1;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeIdGenerateService iNodeIdGenerateService;
    
    @Autowired
    private LinkService linkService;
    
    @Autowired
    private INodeACLService nodeACLService;
    
    @Autowired
    private NodeService nodeService;
    
    @Autowired
    private RegionService regionService;
    
    @Autowired
    private ShareService shareService;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Autowired
    private UserIdGenerateService userIdGenerateService;
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    @Autowired
    private GroupMembershipsService groupMembershipsService;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeRegion(long accountId, long userId, int regionId)
    {
        if (null == get(userId))
        {
            String message = "User Not Found.";
            LOGGER.warn(message);
            throw new NoSuchElementException(message);
        }
        
        if (null == regionService.getRegion(regionId))
        {
            String message = "Region Not Found";
            LOGGER.warn(message);
            throw new NoSuchElementException(message);
        }
        userDAO.updateRegionID(accountId, userId, regionId);
        userReverseDAO.updateRegionID(accountId, userId, regionId);
    }
    
    @Override
    public long countSpaceTotal(Map<String, Object> map)
    {
        return userReverseDAO.countSpaceTotal(map);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void create(User user)
    {
        long id = userIdGenerateService.getNextUserId();
        user.setId(id);
        Date now = new Date();
        user.setCreatedAt(now);
        user.setModifiedAt(now);
        userDAO.create(user);
        userReverseDAO.create(user);
    }
    
    @Override
    public void createEvent(UserToken userToken, EventType type, long createdBy)
    {
        try
        {
            Event event = new Event(userToken);
            event.setType(type);
            event.setCreatedAt(new Date());
            event.setCreatedBy(createdBy);
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString(), e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void delete(long accountId, long id)
    {
        // TODO 跨库事务，后续需要单独处理
        userDAO.delete(accountId, id);
        userReverseDAO.delete(accountId, id);
        iNodeIdGenerateService.delete(id);
        userSyncVersionService.delete(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void deleteUser(long accountId, long userId)
    {
        linkService.deleteByOwner(userId);
        shareService.deleteUserFromSystem(userId);
        nodeACLService.deleteSpaceAllACLs(userId);
        groupMembershipsService.deleteMembershipsForUser(userId);
        teamSpaceService.updateTeamSpacesForUserDelete(accountId, userId);
        nodeService.deleteUserAllNodes(userId);
        userDAO.delete(accountId, userId);
        userReverseDAO.delete(accountId, userId);
        iNodeIdGenerateService.delete(userId);
        userSyncVersionService.delete(userId);
    }
    
    @Override
    public void disableUser(long accountId, long id)
    {
        userDAO.updateStatus(accountId, id, "disable");
        userReverseDAO.updateStatus(accountId, id, "disable");
    }
    
    @Override
    public void enableUser(long accountId, long id)
    {
        userDAO.updateStatus(accountId, id, "enable");
        userReverseDAO.updateStatus(accountId, id, "enable");
    }
    
    @Override
    public User get(Long id)
    {
        User user = userDAO.get(id);
        if (user != null)
        {
            UserToken userToken = new UserToken();
            userToken.setAppId(user.getAppId());
            userToken.setId(id);
            String[] logParams = new String[]{String.valueOf(user.getId()),
                StringUtils.trimToEmpty(user.getAppId())};
            String keyword = StringUtils.trimToEmpty(user.getLoginName());
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_USER,
                logParams,
                keyword);
        }
        return user;
    }
    
    @Override
    public User deleteUser(User user, String[] akArr, UserService userService)
    {
        user.setStatus(User.USER_DELETING);
        if (null != akArr && akArr.length >= 2)
        {
            user.setLabel(akArr[1]);
        }
        userService.update(user);
        Thread sut = new Thread(new DeleteUserThread(user.getAccountId(), user.getId(), userService));
        sut.start();
        return user;
    }
    
    @Override
    public User get(Long accLongId, Long id)
    {
        return get(id);
    }
    
    public long getAllUsers(User user)
    {
        long total = userReverseDAO.getFilterdCount(user);
        return total;
    }
    
    @Override
    public List<User> getFilterd(User filter, OrderV1 order, Limit limit)
    {
        return userReverseDAO.getFilterd(filter, order, limit);
    }
    
    @Override
    public int getMaxVersions(Long userId)
    {
        return userDAO.getMaxVersions(userId);
    }
    
    @Override
    public User getOneUserOrderByACS(long accountId, long id)
    {
        return userReverseDAO.getOneUserOrderByACS(accountId, id);
    }
    
    @Override
    public UserList getOrderedUserList(User filter, List<Order> orderList, long offset, int limit)
    {
        orderList = orderList == null ? new ArrayList<Order>(BusinessConstants.INITIAL_CAPACITIES)
            : orderList;
        
        // 默认按照user id排序
        Order orderById = new Order("ID", "ASC");
        if (!orderList.contains(orderById))
        {
            orderList.add(orderById);
        }
        
        List<User> list = userReverseDAO.getOrderedUserList(filter, orderList, offset, limit);
        int userCount = userReverseDAO.getUserCountByAccountId(filter.getAccountId());
        List<RestUserCreateRequest> restUserList = transToRestUserList(list);
        UserList userList = new UserList(limit, offset, userCount, restUserList);
        return userList;
    }
    
    @Override
    public List<User> getUsedCapacity(Map<String, Object> map)
    {
        return userReverseDAO.getUsedCapacity(map);
    }
    
    @Override
    public User getUserByLoginNameAccountId(String loginName, long accountId)
    {
        User user = userReverseDAO.getUserByLoginNameAccountId(loginName, accountId);
        if (user != null)
        {
            UserToken userToken = new UserToken();
            userToken.setAppId(user.getAppId());
            userToken.setId(user.getId());
            String[] logMsgs = new String[]{String.valueOf(user.getId()),
                StringUtils.trimToEmpty(user.getAppId())};
            fileBaseService.sendINodeEvent(userToken,
                EventType.OTHERS,
                null,
                null,
                UserLogType.GET_USER_POST,
                logMsgs,
                null);
        }
        return user;
    }
    
    @Override
    public User getUserByObjectSid(String objectSid, long accountId)
    {
        return userDAO.getUserByObjectSid(objectSid, accountId);
    }
    
    @Override
    public UserQos getUserQos(long id)
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, UserQos.USER_QOS_CONFIG_PREFIX);
        return UserQos.buildUserQos(itemList);
    }
    
    /**
     * 用户开户
     * 
     * @param user
     * @throws BaseRunException
     */
    @Override
    public void saveUser(User user) throws BaseRunException
    {
    	/** 修改时间2017-04-04 伍伟  开始 **/
//        long spaceCount = teamSpaceDAO.getTeamSpaceCount(null);
//        long userCount = getAllUsers(null);
//        licenseChecker.checkTotalUsers(userCount - spaceCount);
//        修改时间2017-04-04 伍伟  结束 **/
        User userByDao = getUserByLoginNameAccountId(user.getLoginName(), user.getAccountId());
        
        if (userByDao != null)
        {
            throw new ConflictUserException();
        }
        create(user);
        String[] logParams = new String[]{String.valueOf(user.getId()),
            StringUtils.trimToEmpty(user.getAppId())};
        String keyword = StringUtils.trimToEmpty(user.getLoginName());
        UserToken userToken = new UserToken();
        userToken.setAppId(user.getAppId());
        userToken.setId(user.getId());
        if (StringUtils.isNotBlank(user.getLabel()))
        {
            userToken.setLoginName(user.getLabel());
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            UserLogType.CREATE_USER,
            logParams,
            keyword);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setUserQos(long userId, long uploadTraffic, long downloadTraffic, int concurrent)
    {
    }
    
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void update(User user)
    {
        userDAO.update(user);
        userReverseDAO.update(user);
        UserToken userToken = new UserToken();
        userToken.setAppId(user.getAppId());
        userToken.setId(user.getId());
        if (StringUtils.isNotBlank(user.getLabel()))
        {
            userToken.setLoginName(user.getLabel());
        }
        String[] logParams = new String[]{String.valueOf(user.getId()),
            StringUtils.trimToEmpty(user.getAppId())};
        String keyword = StringUtils.trimToEmpty(user.getLoginName());
        UserLogType userLogType = UserLogType.UPDATE_USER;
        if (user.getStatus() != null && user.getStatus().equals(User.USER_DELETING))
        {
            userLogType = UserLogType.DELETE_USER;
        }
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            null,
            null,
            userLogType,
            logParams,
            keyword);
    }
    
    @Override
    public void updateLastLoginTime(long accountId, long id, Date lastLoginAt)
    {
        userDAO.updateLastLoginTime(accountId, id, lastLoginAt);
        userReverseDAO.updateLastLoginTime(accountId, id, lastLoginAt);
    }
    
    @Override
    public void updateSecurityId(long accountId, long id, Integer securityLabel)
    {
        if (securityLabel == null)
        {
            throw new InvalidParamException("securityLabel is null");
        }
        userDAO.updateSecurityId(accountId, id, securityLabel);
        userReverseDAO.updateSecurityId(accountId, id, securityLabel);
    }
    
    private List<RestUserCreateRequest> transToRestUserList(List<User> userList)
    {
        List<RestUserCreateRequest> restUserList = new ArrayList<RestUserCreateRequest>(
            BusinessConstants.INITIAL_CAPACITIES);
        RestUserCreateRequest restUser = null;
        for (User user : userList)
        {
            restUser = new RestUserCreateRequest();
            restUser.copyFrom(user);
            restUserList.add(restUser);
        }
        return restUserList;
    }
    
    @Override
    public void setDefaultValue(RestUserCreateRequest user)
    {
        user.setDefaultValue();
    }
    
    /**
     * 开户接口参数校验
     * 
     * @param user
     * @throws InvalidParamException
     */
    
    @Override
    public void checkUserAddParamter(RestUserCreateRequest user) throws InvalidParamException
    {
        if (user == null)
        {
            throw new InvalidParamException();
        }
        user.checkUserAddParamter();
    }
    
    /**
     * 检查新用户的存储区域参数
     * @throws BaseRunException
     */
    @Override
    public void checkNewUserRegion(RestUserCreateRequest user) throws BaseRunException
    {
        if (null == user.getRegionId())
        {
            user.setRegionId(DEFAULT_REGION);
        }
        else if (user.getRegionId() == DEFAULT_REGION)
        {
            return;
        }
        Region region = regionService.getRegion(user.getRegionId());
        if (null == region)
        {
            throw new NoSuchReginException();
        }
    }
    
    @Override
    public void transUser(RestUserCreateRequest rUser, User user) throws BaseRunException
    {
        rUser.transUserFromCreate(user);
    }
    
    @Override
    public User initUser(Account account, RestUserCreateRequest requestUser, String[] akArr)
    {
        String lable = null != akArr && akArr.length >= 2 ? akArr[1] : null;
        User user = new User(account.getId(), account.getAppId(), lable);
        requestUser.transUserFromCreate(user);
        return user;
    }
    
    @Override
    public RestUserCreateRequest update(RestUserCreateRequest requestUser, User user)
    {
        requestUser.setId(user.getId());
        if (null == requestUser.getFileCount())
        {
            requestUser.setFileCount(0L);
        }
        
        return requestUser;
    }

    public long getSpaceQuota(long userId) {
        User user = userDAO.get(userId);
        if(user == null) {
            LOGGER.error("No user found: userId=", userId);
            return AccountConstants.UNLIMIT_NUM;
        }

        return  user.getSpaceQuota();
    }

    @Override
    @Transactional
    public void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue) {
        List<User> userList = userReverseDAO.getByAccountIdAndSpaceQuota(accountId, oldValue);

        //批量更新反表中的空间配额
        userReverseDAO.compareAndSwapSpaceQuotaByAccountId(accountId, oldValue, newValue);

        //逐条修改用户的空间配额
        for(User user : userList) {
            userDAO.updateSpaceQuota(user.getId(), newValue);
        }
    }

    @Override
    public void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long spaceQuota) {
        userReverseDAO.updateSpaceQuotaByAccountIdAndUserIds(accountId, userIds, spaceQuota);

        //逐条修改用户的空间配额
        for(Long id : userIds) {
            userDAO.updateSpaceQuota(id, spaceQuota);
        }
    }
}
