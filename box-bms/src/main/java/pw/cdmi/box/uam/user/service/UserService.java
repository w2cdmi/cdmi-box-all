package pw.cdmi.box.uam.user.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.httpclient.domain.RestAppStatisticsRequest;
import pw.cdmi.box.uam.httpclient.domain.RestRegionInfo;
import pw.cdmi.box.uam.user.domain.User;
import pw.cdmi.box.uam.user.domain.UserExtend;
import pw.cdmi.box.uam.user.domain.UserQos;

public interface UserService
{
    Map<String, Long> fillBandWidth(Long donwloadBandWidthPare, Long uploadBandWidthPare, String appId);
    
    void fillStatistics(User user, RestAppStatisticsRequest request, String appId);// TODO
    
    List<User> listStatisticsByAppId(String appId);
    
    List<User> getUsersByAppId(String appId);
    
    void update(User user);
    
    /**
     * 
     * @param username
     * @param appId
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
     * @param user
     */
    // void enableUser(long id, String appId);
    
    /**
     * 
     * @param user
     */
    // void disableUser(long id, String appId);
    
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
     * @param userID
     * @param uploadTraffic
     * @param downloadTraffic
     * @param concurrent
     */
    void setUserQos(long userID, long uploadTraffic, long downloadTraffic, int concurrent);
    
    /**
     * 
     * @param id
     * @return
     */
    UserQos getUserQos(long id);
    
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
    
    /**
     * 
     * @param user
     * @throws InternalServerErrorException
     */
    // public boolean updateUser(User user);
    
    void resetPassword(long id, String newPsw);
    
    /**
     * 
     * @param id
     * @param validateKey
     */
    void updateValidateKey(long id, String validateKey);
    
    void addUserList(List<User> userlist);
    
    /**
     * 
     * @param id
     */
    
    // void deleteUserByid(User user, AuthApp authApp);
    
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
