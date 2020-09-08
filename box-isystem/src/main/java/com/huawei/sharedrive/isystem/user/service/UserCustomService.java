/**
 * 
 */
package com.huawei.sharedrive.isystem.user.service;

import com.huawei.sharedrive.isystem.user.domain.UserCustom;

/**
 * @author s00108907
 * 
 */
public interface UserCustomService
{
    
    /**
     * 获取用户的个性化配置
     * 
     * @param id
     * @return
     */
    UserCustom getUserCustom(long id);
    
    /**
     * 保存用户个性化配置
     * 
     * @param userCustom
     */
    void saveUserCustom(UserCustom userCustom);
}
