package com.huawei.sharedrive.uam.user.dao;

import java.util.List;

import com.huawei.sharedrive.uam.openapi.domain.UserOrder;
import com.huawei.sharedrive.uam.user.domain.User;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.encrypt.HashPassword;

public interface UserDAO extends BaseDAO<User, Long>
{
    
    List<User> getFilterdOrderList(User filter, List<UserOrder> orderList, Limit limit);
    
    List<User> getUsersByAppId(String appId);
    
    long getMaxUserId();
    
    long getNextAvailableUserId();
    
    /**
     * 
     * @param loginName
     * @param appId
     * @return
     */
    User getUserByLoginNameAppId(String loginName, String appId);
    
    /**
     * 
     * @param objectSid
     * @return
     */
    User getUserByObjectSidAppId(String objectSid, String appId);
    
    List<User> getFilterd(User filter, Order order, Limit limit);
    
    int getFilterdCount(User filter);
    
    Long findUserById();
    
    void updateStatus(long id, String status);
    
    void updatePassword(long id, HashPassword hashPassword);
    
    void updateRegionID(long id, int regionID);
    
    /**
     * 
     * @param id
     * @param validateKey
     */
    void updateValidateKey(long id, String validateKey);
    
    /**
     * @param teamSpaceFlag
     * @param teamSpaceMaxNum
     */
    void updateTeamSpace(long id, byte teamSpaceFlag, int teamSpaceMaxNum);
    
    void sacleUser(long id, long spaceQuota);
    
    List<User> getAll();
    
    /**
     * 
     * @param cloudUserId
     * @return
     */
    User getUserByCloudUserId(Long cloudUserId);
    
    /**
     * 
     * @param userlist
     */
    void addUserList(List<User> userlist);
    
    /**
     * 
     * @param userlist
     */
    void updateuserlist(List<User> userlist);
    
    /**
     * 
     * @param id
     * @param loginName
     * @param userName
     * @return
     */
    
    /**
     * 
     * @param id
     * @param loginName
     * @param userName
     * @return
     */
    void create(User user);
    
    /**
     * 
     * 
     * @param filter
     * @param order
     * @param limit
     * @return
     */
    List<User> listUser(User filter, Order order, Limit limit);
    
    /**
     * 
     * @param id
     */
    void deleteUserByid(Long id);
    
    /**
     * 
     * @param user
     */
    void updateUserByid(User user);
    
    List<User> listUserByAppid(String appId, int offset, int length);
    
    List<String> getFilterdId(User filter);
    
}