/**
 * 
 */
package com.huawei.sharedrive.app.user.service;

import java.io.IOException;

import com.huawei.sharedrive.app.openapi.rest.AsyncContextWrapper;

/**
 * 集群数据管理器
 * 
 * @author q90003805
 * 
 */
public interface UserSyncVersionService
{
    /**
     * 关闭用户请求异步上下文
     * 
     * @param userId
     * @param clientId
     */
    void completeContext(long userId, String clientId);
    
    /**
     * 获取用户同步版本号监听器
     * 
     * @param userId
     * @return
     */
    long getUserCurrentSyncVersion(long userId);
    
    /**
     * 获取用户的下一个同步版本号
     * 
     * @param userId
     * @return
     */
    long getNextUserSyncVersion(long userId);
    
    /**
     * 删除用户对应的节点关系
     * 
     * @param userId
     */
    void delete(long userId);
    
    /**
     * 通知集群管理器，用户同步版本号发生变化
     * 
     * @param userId
     * @param newVersion
     * @throws Exception
     */
    void notifyUserCurrentSyncVersionChanged(long userId, long newVersion) throws IOException;
    
    /**
     * 注册用户请求异步上下文
     * 
     * @param context
     */
    void registContext(AsyncContextWrapper context);
}