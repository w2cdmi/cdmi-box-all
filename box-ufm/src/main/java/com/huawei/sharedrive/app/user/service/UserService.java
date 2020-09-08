/**
 * 
 */
package com.huawei.sharedrive.app.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
//import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.domain.UserList;
import com.huawei.sharedrive.app.user.domain.UserQos;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserService
{
    /**
     * 修改用户所属区域
     * 
     * @param accountId
     * @param userId
     * @param regionId
     */
    void changeRegion(long accountId, long userId, int regionId);
    
    /**
     * 创建用户对象
     * 
     * @param user
     */
    void create(User user);
    
    /**
     * 创建事件
     * 
     * @param userToken
     * @param type
     * @param createdBy
     */
    void createEvent(UserToken userToken, EventType type, long createdBy);
    
    /**
     * 根据ID删除用户对象
     * 
     * @param id 用户ID
     */
    void delete(long accountId, long id);
    
    void deleteUser(long accountId, long userId);
    
    /**
     * 根据用户ID停用用户
     * 
     * @param UserTest
     */
    void disableUser(long accountId, long id);
    
    /**
     * 根据用户ID启用用户
     * 
     * @param UserTest
     */
    void enableUser(long accountId, long id);
    
    /**
     * 根据ID获取用户对象
     * 
     * @param appId 应用ID 日志
     * @param id 用户ID
     * @return 用户对象
     */
    User get(Long id);
    
    /**
     * 根据ID获取用户对象
     * 
     * @param appId 应用ID 日志
     * @param id 用户ID
     * @return 用户对象
     */
    User get(Long accountId, Long id);
    
    /**
     * 分页获取用户数据
     * 
     * @param filter 过了条件，为空表示所有用户
     * @param order 排序，为空表示不排序
     * @param limit 分页offset和length，为空表示所有
     * @return
     */
    List<User> getFilterd(User filter, OrderV1 order, Limit limit);
    
    /**
     * 获取用户的文件总数和空间使用数 用于统计
     * 
     * @param user
     * @return
     */
    List<User> getUsedCapacity(Map<String, Object> map);
    
    /**
     * 统计 空间总数
     * 
     * @param user
     * @param type
     * @return
     */
    long countSpaceTotal(Map<String, Object> map);
    
    /**
     * 获取用户最大版本数
     * 
     * @param userId
     * @return
     */
    int getMaxVersions(Long userId);
    
    /**
     * 获取某应用排序后的用户列表(默认根据id排序)
     * 
     * @param filter
     * @param orderList
     * @param offset
     * @param limit
     * @return
     */
    UserList getOrderedUserList(User filter, List<Order> orderList, long offset, int limit);
    
    /**
     * 通过sid查询用户信息
     * 
     * @param objectSid
     * @return
     */
    User getUserByObjectSid(String objectSid, long accountId);
    
    /**
     * 获取指定用户的QOS
     * 
     * @param id
     * @return
     */
    UserQos getUserQos(long id);
    
    
    /**
     * 用户开户
     * 
     * @param user
     * @throws BaseRunException
     */
    void saveUser(User user) throws BaseRunException;
    
    /**
     * 设置用户QOS
     * 
     * @param userId
     * @param uploadTraffic
     * @param downloadTraffic
     * @param concurrent
     */
    void setUserQos(long userId, long uploadTraffic, long downloadTraffic, int concurrent);
    
    /**
     * 更新用户信息
     * 
     * @param user
     */
    void update(User user);
    
    /**
     * 更新最新登录时间
     * 
     * @param lastLoginAt
     */
    void updateLastLoginTime(long accountId, long id, Date lastLoginAt);
    
    User getUserByLoginNameAccountId(String loginName, long accountId);
    
    /**
     * 获取一个user
     * 
     * @param accountId
     * @param id
     * @return
     */
    User getOneUserOrderByACS(long accountId, long id);
    
    void updateSecurityId(long accountId, long id, Integer securityLabel);
    
    User deleteUser(User user, String[] akArr, UserService userService);
    
    void setDefaultValue(RestUserCreateRequest user);
    
    void checkUserAddParamter(RestUserCreateRequest user) throws BaseRunException;
    
    void checkNewUserRegion(RestUserCreateRequest user) throws BaseRunException;
    
    void transUser(RestUserCreateRequest rUser, User user) throws BaseRunException;
    
    User initUser(Account account, RestUserCreateRequest requestUser, String[] akArr);
    
    RestUserCreateRequest update(RestUserCreateRequest requestUser, User user);

    long getSpaceQuota(long userId);

    //将accountId下所有配额为oldValue的调整为newValue
    void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue);

    void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long spaceQuota);
}
