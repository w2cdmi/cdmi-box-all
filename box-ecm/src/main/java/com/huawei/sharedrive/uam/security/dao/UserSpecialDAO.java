package com.huawei.sharedrive.uam.security.dao;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.UserSpecial;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserSpecialDAO
{
    
    void insert(UserSpecial userSpecial);
    
    void delete(UserSpecial userSpecial);
    
    void update(UserSpecial userSpecial, UserSpecial oldUserSpecial);
    
    List<UserSpecial> getAll(UserSpecial userSpecial, Order order, Limit limit);
    
    Integer getFilterdCount(UserSpecial userSpecial);
    
    Integer isExist(UserSpecial userSpecial);
    
    UserSpecial getUserSpecial(UserSpecial userSpecial);
    
    UserSpecial getUserByNameAndType(String userName, Integer specialType);
}
