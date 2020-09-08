package com.huawei.sharedrive.uam.user.dao;

import java.util.List;

import com.huawei.sharedrive.uam.user.domain.UserExtend;
import com.huawei.sharedrive.uam.user.domain.UserTag;
import com.huawei.sharedrive.uam.user.domain.UserTagExtend;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserTagDAO
{
    
    void update(UserTag userTag);
    
    void insert(UserTag userTag);
    
    void delete(String tagId);
    
    void deleteByUserId(Long userId);
    
    UserTag selectByUserTag(UserTag userTag);
    
    List<UserTagExtend> selectUserTagByUserId(Long userId);
    
    int getFilterdCount(UserExtend filter);
    
    List<UserExtend> getFilterdOrderList(UserExtend filter, Order order, Limit limit);
    
}