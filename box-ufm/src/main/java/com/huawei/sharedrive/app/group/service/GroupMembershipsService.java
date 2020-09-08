package com.huawei.sharedrive.app.group.service;

import java.util.List;

import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

public interface GroupMembershipsService
{
    
    /**
     * 创建群组关系 成员关系
     * 
     * @param userToken
     * @param groupMemberships
     */
    void createMemberships(UserToken userToken, GroupMemberships groupMemberships);
    
    /**
     * 通过群组ID 删除群组关系
     * 
     * @param groupId
     * @return
     */
    int delete(Long groupId);
    
    /**
     * 通过用户ID 群组ID 用户类型 删除群组、成员关系
     * 
     * @param groupId
     * @param userId
     * @param userType
     */
    void deleteOne(Long groupId, Long userId, byte userType);
    
    /**
     * 通过群组ID 删除用户成员关系
     * 
     * @param groupId
     * @param userId
     * @return
     */
    int deleteUser(Long groupId, Long userId);
    
    /**
     * 列举群组关系 支持模糊查询
     * 
     * @param order
     * @param limit
     * @param groupMemberships
     * @param groupRole
     * @param keyword
     * @return
     */
    List<GroupMemberships> getMemberList(List<GroupOrder> order, Limit limit,
        GroupMemberships groupMemberships, Long groupRole, String keyword);
    
    /**
     * 获取群组关系总数 支持模糊查询
     * 
     * @param userToken
     * @param groupMemberships
     * @param groupRole
     * @param keyword
     * @return
     */
    long getMemberListCount(UserToken userToken, GroupMemberships groupMemberships, Long groupRole,
        String keyword);
    
    /**
     * 通过用户ID 群组ID 用户类型 获取群组关系
     * 
     * @param userId
     * @param groupId
     * @param userType
     * @return
     */
    GroupMemberships getMemberships(Long userId, Long groupId, byte userType);
    
    /**
     * 通过用户ID 群组ID 用户类型 获取成员关系
     * 
     * @param groupMemberships
     * @return
     */
    GroupMemberships getUser(GroupMemberships groupMemberships);
    
    /**
     * 列举用户关系 支持模糊查询
     * 
     * @param order
     * @param limit
     * @param userId
     * @param userType
     * @param keyword
     * @return
     */
    List<GroupMemberships> getUserList(List<GroupOrder> order, Limit limit, Long userId, byte userType,
        String keyword);
    
    /**
     * 获取用户关系总数 支持模糊查询
     * 
     * @param userId
     * @param userType
     * @param keyword
     * @return
     */
    long getUserListCount(Long userId, byte userType, String keyword);
    
    /**
     * 更新用户关系信息
     * 
     * @param groupMemberships
     * @return
     */
    GroupMemberships update(GroupMemberships groupMemberships);

    void deleteMembershipsForUser(long userId);

    void updateNameForUser(GroupMemberships groupMemberships);
    
    void updateNameForGroup(Long groupId,Long userId,String groupName);
}
