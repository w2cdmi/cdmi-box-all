package pw.cdmi.box.disk.user.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.session.UnknownSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.user.dao.UserImageDao;
import pw.cdmi.box.disk.user.domain.UserImage;
import pw.cdmi.box.disk.user.service.UserImageService;
import pw.cdmi.common.cache.CacheClient;

@Service
public class UserImageServiceImpl implements UserImageService
{
    public static final String CACHE_KEY_SESSION_PREFIX = "user_image_";
    
    @Autowired
    private UserImageDao userImageDao;
    
    @Resource(name = "cacheClient")
    private CacheClient cacheClient;
    
    @Value("${userimage.expire}")
    private long imageExpireTime;
    
    @Override
    public void updateUserImage(String sessionId, UserImage userImage)
    {
        UserImage userImages = userImageDao.get(userImage);
        if (null == userImages)
        {
            userImageDao.insert(userImage);
        }
        else
        {
            userImageDao.update(userImage);
        }
        
        String sessionKey = getSessionKey(sessionId) + "." + userImage.getUserId();;
        Date expireTime = new Date(System.currentTimeMillis() + imageExpireTime);
        boolean success = cacheClient.setCache(sessionKey, userImage, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store session to memcache failed.");
        }
    }
    
    private String getSessionKey(String sessionId)
    {
        return CACHE_KEY_SESSION_PREFIX + sessionId;
    }
    
    @Override
    public UserImage getUserImage(HttpServletRequest req, UserImage userImage)
    {
        String sessionKey = getSessionKey(req.getSession().getId()) + "." + userImage.getUserId();
        UserImage newNserImage = (UserImage) cacheClient.getCache(sessionKey);
        if (newNserImage == null)
        {
            newNserImage = userImageDao.get(userImage);
            if(newNserImage != null) {
                Date expireTime = new Date(System.currentTimeMillis() + imageExpireTime);
                cacheClient.setCache(sessionKey, newNserImage, expireTime);
            }
        }
        return newNserImage;
    }
    
    @Override
    public void updateCache(HttpServletRequest req, UserImage userImage)
    {
        String sessionKey =  getSessionKey(req.getSession().getId()) + "." + userImage.getUserId();
        Date expireTime = new Date(System.currentTimeMillis() + imageExpireTime);
        boolean success = cacheClient.setCache(sessionKey, userImage, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store userImage to memcache failed.");
        }
    }
    
    @Override
    public UserImage getUserImage(UserImage userImage)
    {
        return userImageDao.get(userImage);
    }
    
    @Override
    public int getUserId(UserImage userImage)
    {
        return userImageDao.getUserId(userImage);
    }
    
}
