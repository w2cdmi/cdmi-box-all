package com.huawei.sharedrive.uam.user.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.user.domain.UserImage;
import com.huawei.sharedrive.uam.user.manager.UserImageManager;
import com.huawei.sharedrive.uam.user.service.UserImageService;

@Component
public class UserImageManagerImpl implements UserImageManager
{
    @Autowired
    private UserImageService userImageService;
    
    @Override
    public void create(String sessionId, UserImage userImage)
    {
        userImageService.updateUserImage(sessionId, userImage);
    }
    
    @Override
    public UserImage getUserImage(UserImage userImage)
    {
        return userImageService.getUserImage(userImage);
    }
}
