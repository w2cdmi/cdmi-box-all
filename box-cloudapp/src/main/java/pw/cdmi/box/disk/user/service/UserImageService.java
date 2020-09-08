package pw.cdmi.box.disk.user.service;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.disk.user.domain.UserImage;

public interface UserImageService
{
    void updateUserImage(String sessionId, UserImage userImage);
    
    UserImage getUserImage(HttpServletRequest req, UserImage userImage);
    
    UserImage getUserImage(UserImage userImage);
    
    int getUserId(UserImage userImage);
    
    void updateCache(HttpServletRequest req, UserImage userImage);
}
