/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

/**
 * 共享权限服务接口
 * @author l90003768
 *
 */
public interface SharePrivilegeService
{
    
    /**
     * 检查权限
     * @param curUser
     * @param ownerId
     * @param nodeId
     * @throws BaseRunException
     */
    void checkPrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException;
    
}
