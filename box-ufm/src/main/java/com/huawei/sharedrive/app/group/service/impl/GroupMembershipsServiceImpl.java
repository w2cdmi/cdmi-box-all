package com.huawei.sharedrive.app.group.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.group.dao.GroupCachedDAO;
import com.huawei.sharedrive.app.group.dao.GroupDAO;
import com.huawei.sharedrive.app.group.dao.GroupMembershipsDAO;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupMembershipsService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

@Component
public class GroupMembershipsServiceImpl implements GroupMembershipsService
{
    
    @Autowired
    private GroupMembershipsDAO groupMembershipsDao;
    
    @Autowired
    private GroupCachedDAO groupCachedDao;
    
    @Autowired
    private GroupDAO groupDao;
    
    @Override
    public void createMemberships(UserToken userToken, GroupMemberships groupMemberships)
    {
        /* 正表 */
        groupMembershipsDao.create(groupMemberships);
        
        /* 反表 */
        groupMembershipsDao.createUser(groupMemberships);
        
    }
    
    @Override
    public int delete(Long groupId)
    {
        return groupMembershipsDao.delete(groupId);
    }
    
    @Override
    public void deleteOne(Long groupId, Long userId, byte userType)
    {
        groupMembershipsDao.deleteOneShips(groupId, userId, userType);
        
        groupMembershipsDao.deleteOneUser(groupId, userId, userType);
    }
    
    @Override
    public int deleteUser(Long groupId, Long userId)
    {
        return groupMembershipsDao.deleteUser(groupId, userId);
    }
    
    @Override
    public List<GroupMemberships> getMemberList(List<GroupOrder> order, Limit limit,
        GroupMemberships groupMemberships, Long groupRole, String keyword)
    {
        return groupMembershipsDao.getMemberList(order, limit, groupMemberships, groupRole, keyword);
    }
    
    @Override
    public long getMemberListCount(UserToken userToken, GroupMemberships groupMemberships, Long groupRole,
        String keyword)
    {
        return groupMembershipsDao.getMemberListCount(groupMemberships, groupRole, keyword);
    }
    
    @Override
    public GroupMemberships getMemberships(Long userId, Long groupId, byte userType)
    {
        return groupMembershipsDao.getMemberships(userId, groupId, userType);
    }
    
    @Override
    public GroupMemberships getUser(GroupMemberships groupMemberships)
    {
        return groupMembershipsDao.getUser(groupMemberships);
    }
    
    @Override
    public List<GroupMemberships> getUserList(List<GroupOrder> order, Limit limit, Long userId,
        byte userType, String keyword)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setUserId(userId);
        groupMemberships.setUserType(userType);
        
        groupMemberships.setName(keyword);
        return groupMembershipsDao.getUserList(order, limit, groupMemberships);
    }
    
    @Override
    public long getUserListCount(Long userId, byte userType, String keyword)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setUserId(userId);
        groupMemberships.setUserType(userType);
        groupMemberships.setName(keyword);
        return groupMembershipsDao.getUserListCount(groupMemberships);
    }
    
    @Override
    public GroupMemberships update(GroupMemberships groupMemberships)
    {
        groupMembershipsDao.update(groupMemberships);
        
        groupMembershipsDao.updateUser(groupMemberships);
        return groupMemberships;
    }
    
    /**
     * this method to change the owner role to any other member of the group
     * 
     * @param membersOfGroup
     * @param groupMember
     * @return
     */
    private GroupMemberships getUpdateRole(List<GroupMemberships> membersOfGroup, GroupMemberships groupMember)
    {
        GroupMemberships memberShip = null;
        int size = membersOfGroup.size();
        boolean isManager = false;
        for (int i = 0; i < size; i++)
        {
            if (GroupConstants.GROUP_ROLE_MANAGER == membersOfGroup.get(i).getGroupRole())
            {
                memberShip = membersOfGroup.get(i);
                memberShip.setGroupRole(groupMember.getGroupRole());
                isManager = true;
                break;
            }
        }
        if (!isManager)
        {
            for (int i = 0; i < size; i++)
            {
                if (GroupConstants.GROUP_ROLE_MEMBER == membersOfGroup.get(i).getGroupRole())
                {
                    memberShip = membersOfGroup.get(i);
                    memberShip.setGroupRole(groupMember.getGroupRole());
                    break;
                }
            }
        }
        return memberShip;
    }
    
    @Override
    public void deleteMembershipsForUser(long userId)
    {
        List<GroupMemberships> groupMembershipses = groupMembershipsDao.getUserListByUserId(userId);
        List<GroupMemberships> membersOfGroup = null;
        for (GroupMemberships gm : groupMembershipses)
        {
            membersOfGroup = groupMembershipsDao.getMemberListByGroupId(gm.getGroupId());
            if (GroupConstants.GROUP_ROLE_ADMIN == gm.getGroupRole())
            {
                deleteGroupByAdmin(userId, membersOfGroup, gm);
            }
            else
            {
                /**
                 * if the user is a member of the group and is not the owner, the user
                 * owns a memberships,then only delete the user of the group and delete
                 * the memberships
                 */
                deleteOne(gm.getGroupId(), userId, GroupConstants.GROUP_USERTYPE_USER);
            }
        }
    }

    private void deleteGroupByAdmin(long userId, List<GroupMemberships> membersOfGroup, GroupMemberships gm)
    {
        if (membersOfGroup.size() == 1)
        {
            /**
             * if the user is the only member of the group, so he is the owner.
             * then delete the memberships and delete the group
             */
            groupDao.delete(gm.getGroupId());
            deleteOne(gm.getGroupId(), userId, GroupConstants.GROUP_USERTYPE_USER);
            groupCachedDao.deleteCached(userId);
        }
        else
        {
            /**
             * if the user is the owner of the group and there are at least one
             * member except the owner belonging to the group, then delete the
             * owner and update any other member to be the owner.
             */
            GroupMemberships memberShip = getUpdateRole(membersOfGroup, gm);
            if (null != memberShip)
            {
                groupMembershipsDao.update(memberShip);
                groupMembershipsDao.updateUser(memberShip);
                Group group = groupDao.get(gm.getGroupId());
                if (null != group)
                {
                    group.setOwnedBy(memberShip.getUserId());
                    group.setModifiedAt(new Date());
                    group.setModifiedBy(memberShip.getUserId());
                    groupDao.update(group);
                }
            }
            
            deleteOne(gm.getGroupId(), userId, GroupConstants.GROUP_USERTYPE_USER);
        }
    }
    
    @Override
    public void updateNameForUser(GroupMemberships groupMemberships)
    {
        groupMembershipsDao.updateUserUsername(groupMemberships);
        groupMembershipsDao.updateMembershipUsername(groupMemberships);
    }
    
    @Override
    public void updateNameForGroup(Long groupId, Long userId, String groupName)
    {
        groupMembershipsDao.updateShipsGroupName(groupName, groupId, userId);
        groupMembershipsDao.updateUserShipsGroupName(groupName, groupId, userId);
    }
}
