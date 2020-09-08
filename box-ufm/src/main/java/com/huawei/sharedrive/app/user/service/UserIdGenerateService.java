/**
 * 
 */
package com.huawei.sharedrive.app.user.service;

/**
 * @author q90003805
 * 
 */
public interface UserIdGenerateService
{
    /**
     * 获取下一个用户ID
     * 
     * @return
     */
    long getNextUserId();
    
}