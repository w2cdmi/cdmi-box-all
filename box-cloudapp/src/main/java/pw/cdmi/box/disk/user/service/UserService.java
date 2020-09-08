package pw.cdmi.box.disk.user.service;

import pw.cdmi.box.disk.event.domain.EventType;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;

public interface UserService
{
    
    /**
     * 
     * @param userToken
     * @param type
     * @param createdBy
     */
    void createEvent(UserToken userToken, EventType type, long createdBy);
    
    
    /**
     * 
     * @param onwerId
     * @return
     */
    User getUserByCloudUserId(long cloudUserId);
    
    EnterpriseUser getEnterpriseUserByUserId(long accountId, long id);
    
    EnterpriseAccount getEnterpriseAccountByCloudUserId(long cloudUserId);
    
    void getUserTokenBydb(UserToken userToken, String loginName, long enterpriseId, long accountId);

    EnterpriseUser getEnterpriseUserByWeixinUserId(String corpId, String userId);


	User getUserByCloudUserId(long accountId, long cloudUserId);
}
