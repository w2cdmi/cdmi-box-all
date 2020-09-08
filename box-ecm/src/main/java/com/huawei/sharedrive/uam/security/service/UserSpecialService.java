package com.huawei.sharedrive.uam.security.service;

import com.huawei.sharedrive.uam.security.domain.UserSpecial;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface UserSpecialService
{
    
    void insert(UserSpecial userSpecial);
    
    void delete(UserSpecial userSpecial);
    
    void update(UserSpecial userSpecial, UserSpecial oldUserSpecial);
    
    Page<UserSpecial> queryPage(UserSpecial userSpecial, PageRequest pageRequest);
    
    Integer isExist(UserSpecial userSpecial);
    
    UserSpecial getUserSpecial(UserSpecial userSpecial);
    
    UserSpecial getUserByNameAndType(String userName, Integer specialType);
}
