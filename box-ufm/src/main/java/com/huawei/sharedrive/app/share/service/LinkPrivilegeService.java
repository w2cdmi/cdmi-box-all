/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;

/**
 * 外链权限服务接口
 * @author l90003768
 *
 */
public interface LinkPrivilegeService
{
    
    /**
     * 检查删除外链权限
     * @param curUser
     * @param ownerId
     * @param nodeId
     * @throws BaseRunException
     */
    void checkDeletePrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * 检查更新外链权限
     * @param curUser
     * @param ownerId
     * @param nodeId
     * @throws BaseRunException
     */
    void checkUpdatePrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * 检查创建外链权限
     * @param curUser
     * @param ownerId
     * @param nodeId
     * @throws BaseRunException
     */
    void checkCreatePrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException;
    
    /**
     * 检查获取外链信息权限
     * @param curUser
     * @param ownerId
     * @param nodeId
     * @throws BaseRunException
     */
    void checkGetPrivilege(UserToken curUser, long ownerId, long nodeId) throws BaseRunException;
    
}
