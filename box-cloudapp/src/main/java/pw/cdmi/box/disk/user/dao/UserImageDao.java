package pw.cdmi.box.disk.user.dao;

import pw.cdmi.box.disk.user.domain.UserImage;

public interface UserImageDao
{
    void insert(UserImage userImage);
    
    void update(UserImage userImage);
    
    UserImage get(UserImage userImage);
    
    int getUserId(UserImage userImage);
}
