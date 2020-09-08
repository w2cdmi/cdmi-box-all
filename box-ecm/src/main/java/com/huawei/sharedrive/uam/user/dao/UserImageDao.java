package com.huawei.sharedrive.uam.user.dao;

import com.huawei.sharedrive.uam.user.domain.UserImage;

public interface UserImageDao
{
    void insert(UserImage userImage);
    
    void update(UserImage userImage);
    
    UserImage get(UserImage userImage);
    
    int getUserId(UserImage userImage);
}
