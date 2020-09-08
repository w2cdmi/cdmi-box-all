/**
 * 
 */
package pw.cdmi.box.uam.user.service;

import java.util.List;

import pw.cdmi.box.uam.user.domain.UserTag;
import pw.cdmi.box.uam.user.domain.UserTagExtend;

public interface UserTagService
{
    void update(UserTag userTag);
    
    void insert(UserTag userTag);
    
    void delete(String tagId);
    
    UserTag selectByTagId(String tagId, Long userId);
    
    List<UserTagExtend> selectUserTagByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
