package pw.cdmi.box.uam.user.dao;

import java.util.List;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.user.domain.UserExtend;
import pw.cdmi.box.uam.user.domain.UserTag;
import pw.cdmi.box.uam.user.domain.UserTagExtend;

public interface UserTagDAO
{
    
    void update(UserTag userTag);
    
    void insert(UserTag userTag);
    
    void delete(String tagId);
    
    void deleteByUserId(Long userId);
    
    UserTag selectByUserTag(UserTag userTag);
    
    List<UserTagExtend> selectUserTagByUserId(Long userId);
    
    int getFilterdCount(UserExtend filter);
    
    List<UserExtend> getFilterdOrderList(UserExtend filter, Order order, Limit limit);
    
}