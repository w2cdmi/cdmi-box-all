/**
 * 
 */
package pw.cdmi.box.uam.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.user.dao.UserTagDAO;
import pw.cdmi.box.uam.user.domain.UserTag;
import pw.cdmi.box.uam.user.domain.UserTagExtend;
import pw.cdmi.box.uam.user.service.UserTagService;

@Component
public class UserTagServiceImpl implements UserTagService
{
    
    @Autowired
    private UserTagDAO userTagDAO;
    
    @Override
    public void update(UserTag userTag)
    {
        userTagDAO.update(userTag);
    }
    
    @Override
    public void insert(UserTag userTag)
    {
        userTagDAO.insert(userTag);
    }
    
    @Override
    public void delete(String tagId)
    {
        userTagDAO.delete(tagId);
    }
    
    @Override
    public void deleteByUserId(Long userId)
    {
        userTagDAO.deleteByUserId(userId);
    }
    
    @Override
    public UserTag selectByTagId(String tagId, Long userId)
    {
        UserTag userTag = new UserTag();
        userTag.setTagId(tagId);
        userTag.setUserId(userId);
        return userTagDAO.selectByUserTag(userTag);
    }
    
    @Override
    public List<UserTagExtend> selectUserTagByUserId(Long userId)
    {
        List<UserTagExtend> list = userTagDAO.selectUserTagByUserId(userId);
        return list;
    }
}
