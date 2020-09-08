package com.huawei.sharedrive.uam.user.service;

import com.huawei.sharedrive.uam.user.domain.UserCustom;

public interface UserCustomService
{
    
    /**
     * 
     * @param id
     * @return
     */
    UserCustom getUserCustom(long id);
    
    /**
     * 
     * @param userCustom
     */
    void saveUserCustom(UserCustom userCustom);
}
