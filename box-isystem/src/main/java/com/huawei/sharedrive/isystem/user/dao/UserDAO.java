package com.huawei.sharedrive.isystem.user.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.user.domain.User;

import pw.cdmi.box.dao.BaseDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

public interface UserDAO extends BaseDAO<User, Long>
{
    
    /**
     * 用户id生成器，获取一个可用的用户id
     * 
     * @return
     */
    long getNextAvailableUserId();
    
    /**
     * 根据登录名获取用户对象
     * 
     * @param loginName
     * @return
     */
    User getUserByLoginName(String loginName);
    
    /**
     * 根据objectSid获取用户对象
     * 
     * @param objectSid
     * @return
     */
    User getUserByObjectSid(String objectSid);
    
    /**
     * 获取对象列表
     * 
     * @param filter 过滤参数，设置相应属性值，支持name，loginname，email模糊查找。为空表示不做过滤
     * @param order 排序参数。为空表示不做排序
     * @param limit 分页参数。为空表示不做分页
     * @return
     */
    List<User> getFilterd(User filter, Order order, Limit limit);
    
    /**
     * 获取用户总数
     * 
     * @param filter 过滤参数，设置相应属性值，支持name，loginname，email模糊查找。为空表示不做过滤
     * @return
     */
    int getFilterdCount(User filter);
    
    /**
     * 修改用户状态为enable/disable
     * 
     * @param id 要更新的用户ID
     * @param status 新状态
     */
    void updateStatus(long id, String status);
    
    /**
     * 修改用户密码
     * 
     * @param id 要更新的用户ID
     * @param newPsw 新密码
     */
    void updatePassword(long id, String newPsw);
    
    /**
     * 修改用户区域
     * 
     * @param id 要更新的用户ID
     * @param regionID 区域ID
     */
    void updateRegionID(long id, int regionID);
    
    /**
     * 对用户进行扩容操作
     * 
     * @param id 要扩容的用户ID
     * @param spaceQuota 扩容后大小
     */
    void sacleUser(long id, long spaceQuota);
}