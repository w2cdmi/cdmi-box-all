/**
 * 
 */
package com.huawei.sharedrive.uam.user.service;

import java.util.List;

import com.huawei.sharedrive.uam.user.domain.UserTag;
import com.huawei.sharedrive.uam.user.domain.UserTagExtend;

public interface UserTagService
{
    void update(UserTag userTag);
    
    void insert(UserTag userTag);
    
    void delete(String tagId);
    
    UserTag selectByTagId(String tagId, Long userId);
    
    List<UserTagExtend> selectUserTagByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
