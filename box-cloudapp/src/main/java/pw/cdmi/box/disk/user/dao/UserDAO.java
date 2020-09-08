package pw.cdmi.box.disk.user.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserDAO extends BaseDAO<User, Long>
{
    
    List<User> getFilterd(User filter, Order order, Limit limit);
    
    int getFilterdCount(User filter);
    
    long getMaxUserId();
    
    User getUserByLoginNameAppId(String loginName, String appId);
    
    User getUserByObjectSid(String objectSid);
    
    void sacleUser(long id, long spaceQuota);
    
    void updateLastLoginTime(long id, Date lastLoginAt);
    
    void updatePassword(long id, String newPsw);
    
    void updateRegionID(long id, int regionID);
    
    void updateStatus(long id, String status);
    
    User getUserByCloudUserId(long cloudUserId);
}