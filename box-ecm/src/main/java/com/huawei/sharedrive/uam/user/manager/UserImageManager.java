package com.huawei.sharedrive.uam.user.manager;

import com.huawei.sharedrive.uam.user.domain.UserImage;

public interface UserImageManager
{
    void create(String sessionId, UserImage userImage);
    
    UserImage getUserImage(UserImage userImage);
    
}
