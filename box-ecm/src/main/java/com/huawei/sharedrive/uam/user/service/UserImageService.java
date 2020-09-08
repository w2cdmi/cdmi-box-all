package com.huawei.sharedrive.uam.user.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.huawei.sharedrive.uam.user.domain.UserImage;

public interface UserImageService
{
    void updateUserImage(UserImage userImage);

    //更新用户头像
    void updateUserImage(long userId, long accountId, String url);

    void updateUserImage(String sessionId, UserImage userImage);
    
    UserImage getUserImage(HttpServletRequest req, UserImage userImage);
    
    UserImage getUserImage(UserImage userImage);
    
    int getUserId(UserImage userImage);
    
    void saveTempImageFile(String sessionId, MultipartFile file);
    
    MultipartFile getTempImageFile(HttpServletRequest req);
}
