package com.huawei.sharedrive.app.group.dao;

import java.util.List;

import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

public interface GroupMembershipsDAO
{
    
    /**
     * 创建群组关系
     * 
     * @param groupMemberships
     */
    void create(GroupMemberships groupMemberships);
    
    /**
     * 创建群组用户关系
     * 
     * @param groupMemberships
     */
    void createUser(GroupMemberships groupMemberships);
    
    /**
     * 通过群组ID删除群组关系
     * 
     * @param groupId
     * @return
     */
    int delete(Long groupId);
    
    /**
     * 通过群组ID 成员ID 用户类型删除群组关系
     * 
     * @param userId
     * @param groupId
     * @param userType
     * @return
     */
    int deleteOneShips(Long userId, Long groupId, byte userType);
    
    /**
     * 通过群组ID 成员ID 用户类中删除成员关系
     * 
     * @param userId
     * @param groupId
     * @param userType
     * @return
     */
    int deleteOneUser(Long userId, Long groupId, byte userType);
    
    /**
     * 通过群组ID 用户ID 删除群组用户关系
     * 
     * @param groupId
     * @param userId
     * @return
     */
    int deleteUser(Long groupId, Long userId);
    
    long getMaxMembershipsId(long groupId);
    
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
     * @param groupMemberships
     * @param groupRole
     * @param keyword
     * @return
     */
    long getMemberListCount(GroupMemberships groupMemberships, Long groupRole, String keyword);
    
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
     * 通过用户ID 群组ID 用户类型 获取群组成员关系
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
     * @param groupMemberships
     * @return
     */
    List<GroupMemberships> getUserList(List<GroupOrder> order, Limit limit, GroupMemberships groupMemberships);
    
    /**
     * 获取用户关系总数 支持模糊查询
     * 
     * @param groupMemberships
     * @return
     */
    long getUserListCount(GroupMemberships groupMemberships);
    
    /**
     * 通过用户ID获取用户关系
     */
    List<GroupMemberships> getUserListByUserId(Long userId);
    
    /**
     * 通过群组ID获取该群组的成员
     */
    List<GroupMemberships> getMemberListByGroupId(Long groupId);
    
    /**
     * 更新群组关系信息
     * 
     * @param groupMemberships
     * @return
     */
    int update(GroupMemberships groupMemberships);
    
    /**
     * 更新用户关系信息
     * 
     * @param groupMemberships
     * @return
     */
    int updateUser(GroupMemberships groupMemberships);
    
    int updateMembershipUsername(GroupMemberships groupMemberships);
    
    int updateUserUsername(GroupMemberships groupMemberships);
    
    int updateShipsGroupName(String groupName, long groupId, long userId);
    
    int updateUserShipsGroupName(String groupName, long groupId, long userId);
    
}
