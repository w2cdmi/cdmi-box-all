package com.huawei.sharedrive.uam.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.event.domain.EventType;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.openapi.domain.RestAppStatisticsRequest;
import com.huawei.sharedrive.uam.openapi.domain.UserOrder;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;
import com.huawei.sharedrive.uam.user.domain.User;
import com.huawei.sharedrive.uam.user.domain.UserExtend;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.domain.Pager;
import pw.cdmi.box.http.request.RestRegionInfo;

public interface UserService
{
    Map<String, Long> fillBandWidth(Long donwloadBandWidthPare, Long uploadBandWidthPare, String appId);
    
    void fillStatistics(User user, RestAppStatisticsRequest request, String appId);// TODO
    
    List<User> listStatisticsByAppId(String appId);
    
    Pager<User> getRankedUser(Pager<User> pager, User user, RankRequest rankRequest);// TODO
    
    ResponseSearchUser listUser(User filter, Limit limit, List<UserOrder> orderList);
    
    List<User> getUsersByAppId(String appId);
    
    void update(User user);
    
    /**
     * 
     * @param username
     * @param AppId
     * @return
     */
    User getUserByLoginNameAppId(String username, String appId);
    
    /**
     * 
     * @param user
     * @param pageRequest
     * @return
     */
    Page<User> getPagedUser(User filter, PageRequest pageRequest);
    
    /**
     * 
     * @param filter
     * @param pageRequest
     * @return
     */
    List<User> getExportUser(String appId, int offset, int length);
    
    User get(Long id);
    
    Long findUserById();
    
    void delete(long id);
    
    /**
     * 
     * @param id
     * @param spaceQuota
     */
    void sacleUser(long id, long spaceQuota);
    
    /**
     * 
     * @param objectSid
     * @return
     */
    User getUserByObjectSidAppId(String objectSid, String appId);
    
    /**
     * 
     * @param onwerId
     * @return
     */
    User getUserByCloudUserId(long cloudUserId);
    
    List<User> getAll();
    
    /**
     * 
     * @param appId
     * @return
     */
    List<RestRegionInfo> getRegionInfo(String appId);
    
    void resetPassword(long id, String newPsw);
    
    /**
     * 
     * @param id
     * @param validateKey
     */
    void updateValidateKey(long id, String validateKey);
    
    /**
     * 
     * @param userToken
     * @param type
     * @param createdBy
     */
    void createEvent(UserToken userToken, EventType type, long createdBy);
    
    void addUserList(List<User> userlist);
    
    /**
     * 
     * @param appId
     * @return
     */
    long getAppUsedSpace(String appId);
    
    /**
     * 
     * @param appId
     * @return
     */
    long getAppUserTotal(String appId);
    
    /**
     * 
     * @param appId
     * @param beginDate
     * @param endDate
     * @return
     */
    long getAppUserLoginTotal(String appId, Date beginDate, Date endDate);
    
    Page<UserExtend> getPagedUserExtend(User filter, PageRequest pageRequest);
    
    List<String> getFilterdId(User filter);
    
}
