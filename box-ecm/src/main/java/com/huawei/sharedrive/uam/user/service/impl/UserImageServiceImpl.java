package com.huawei.sharedrive.uam.user.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.util.HttpClientUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.session.UnknownSessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.huawei.sharedrive.uam.user.dao.UserImageDao;
import com.huawei.sharedrive.uam.user.domain.UserImage;
import com.huawei.sharedrive.uam.user.service.UserImageService;

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
    public void updateUserImage(UserImage userImage) {
        UserImage userImages = userImageDao.get(userImage);
        if (null == userImages) {
            userImageDao.insert(userImage);
        } else {
            userImageDao.update(userImage);
        }
    }

    @Override
    public void updateUserImage(long userId, long accountId, String url) {
        if(StringUtils.isNotBlank(url)) {
            byte[] image = HttpClientUtils.downloadAsStream(url);
            if(image != null) {
                UserImage userImage = new UserImage();
                userImage.setUserId(userId);
                userImage.setAccountId(accountId);
                userImage.setUserImage(image);
                userImage.setImageFormatName("JPEG");//头像固定格式
                updateUserImage(userImage);
            }
        }

    }

    @Override
    public void updateUserImage(String sessionId, UserImage userImage) {
        updateUserImage(userImage);

        String sessionKey = getSessionKey(sessionId);
        Date expireTime = new Date(System.currentTimeMillis() + imageExpireTime);
        boolean success = cacheClient.setCache(sessionKey, userImage, expireTime);
        if (!success) {
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
        String sessionKey = getSessionKey(req.getSession().getId());
        UserImage newNserImage = (UserImage) cacheClient.getCache(sessionKey);
        if (newNserImage == null)
        {
            newNserImage = userImageDao.get(userImage);
        }
        return newNserImage;
    }
    
    @Override
    public void saveTempImageFile(String sessionId, MultipartFile file)
    {
        
        String sessionKey = "temp_file_" + sessionId;
        Date expireTime = new Date(System.currentTimeMillis() + 60000);
        cacheClient.deleteCache(sessionKey);
        boolean success = cacheClient.setCache(sessionKey, file, expireTime);
        if (!success)
        {
            throw new UnknownSessionException("Store session to memcache failed.");
        }
    }
    
    @Override
    public MultipartFile getTempImageFile(HttpServletRequest req)
    {
        String sessionKey = "temp_file_" + req.getSession().getId();
        return (MultipartFile) cacheClient.getCache(sessionKey);
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
