package com.huawei.sharedrive.app.group.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.group.dao.GroupMembershipsDAO;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.HashTool;

@Service
public class GroupMembershipsDAOImpl extends AbstractDAOImpl implements GroupMembershipsDAO
{
    private static final long BASE_MEMBERSHIPS_ID = 0;
    
    private static final long TABLE_COUNT = 100;
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        sqlMapClientTemplate.insert("GroupMemberships.insert", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void createUser(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        sqlMapClientTemplate.insert("GroupMemberships.insertUser", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int delete(Long groupId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.delete("GroupMemberships.delete", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteOneShips(Long groupId, Long userId, byte userType)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        groupMemberships.setUserId(userId);
        groupMemberships.setUserType(userType);
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.delete("GroupMemberships.deleteOneShips", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteOneUser(Long groupId, Long userId, byte userType)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        groupMemberships.setUserId(userId);
        groupMemberships.setUserType(userType);
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.delete("GroupMemberships.deleteOneUser", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int deleteUser(Long groupId, Long userId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        groupMemberships.setUserId(userId);
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.delete("GroupMemberships.deleteUser", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxMembershipsId(long groupId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        Object membershipsId = sqlMapClientTemplate.queryForObject("GroupMemberships.getMaxMembershipsId",
            groupMemberships);
        if (membershipsId == null)
        {
            return BASE_MEMBERSHIPS_ID;
        }
        return (Long) membershipsId;
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<GroupMemberships> getMemberList(List<GroupOrder> order, Limit limit,
        GroupMemberships groupMemberships, Long groupRole, String keyword)
    {
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        groupMemberships.setName(keyword);
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("filter", groupMemberships);
        map.put("limit", limit);
        map.put("order", getOrderByStr(order));
        map.put("groupRole", groupRole);
        return sqlMapClientTemplate.queryForList("GroupMemberships.getMemberList", map);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMemberListCount(GroupMemberships groupMemberships, Long groupRole, String keyword)
    {
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        groupMemberships.setName(keyword);
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("filter", groupMemberships);
        map.put("groupRole", groupRole);
        return (long) sqlMapClientTemplate.queryForObject("GroupMemberships.getMemberListCount", map);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public GroupMemberships getMemberships(Long userId, Long groupId, byte userType)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        groupMemberships.setUserId(userId);
        groupMemberships.setUserType(userType);
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return (GroupMemberships) sqlMapClientTemplate.queryForObject("GroupMemberships.getMembership",
            groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public GroupMemberships getUser(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return (GroupMemberships) sqlMapClientTemplate.queryForObject("GroupMemberships.getUser",
            groupMemberships);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<GroupMemberships> getUserList(List<GroupOrder> order, Limit limit,
        GroupMemberships groupMemberships)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        map.put("limit", limit);
        map.put("order", getOrderByStr(order));
        map.put("filter", groupMemberships);
        return sqlMapClientTemplate.queryForList("GroupMemberships.getUserMembershipsList", map);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<GroupMemberships> getUserListByUserId(Long userId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setUserId(userId);
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.queryForList("GroupMemberships.getUserListByUserId", groupMemberships);
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    @Override
    public List<GroupMemberships> getMemberListByGroupId(Long groupId)
    {
        GroupMemberships groupMemberships = new GroupMemberships();
        groupMemberships.setGroupId(groupId);
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.queryForList("GroupMemberships.getMemberListByGroupId", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getUserListCount(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return (Long) sqlMapClientTemplate.queryForObject("GroupMemberships.getUserMembershipsListCount",
            groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int update(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateMemberships", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateMembershipUsername(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffix(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateMembershipsForUsername", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateShipsGroupName(String groupName, long groupId, long userId)
    {
        GroupMemberships memberships = new GroupMemberships();
        memberships.setName(groupName);
        memberships.setGroupId(groupId);
        memberships.setUserId(userId);
        int tableSuffix = getTableSuffix(memberships);
        memberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateShipsOfGroupName", memberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateUser(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateUser", groupMemberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateUserShipsGroupName(String groupName, long groupId, long userId)
    {
        GroupMemberships memberships = new GroupMemberships();
        memberships.setName(groupName);
        memberships.setGroupId(groupId);
        memberships.setUserId(userId);
        int tableSuffix = getTableSuffixUser(memberships);
        memberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateUserShipsOfGroupName", memberships);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int updateUserUsername(GroupMemberships groupMemberships)
    {
        int tableSuffix = getTableSuffixUser(groupMemberships);
        groupMemberships.setTableSuffix(tableSuffix);
        return sqlMapClientTemplate.update("GroupMemberships.updateUserForUsername", groupMemberships);
    }
    
    private String getOrderByStr(List<GroupOrder> orderList)
    {
        if (null == orderList || orderList.isEmpty())
        {
            return null;
        }
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (GroupOrder order : orderList)
        {
            field = order.getField();
            
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            if ("username".equalsIgnoreCase(field))
            {
                field = "convert(username using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
    
    private int getTableSuffix(GroupMemberships groupMemberships)
    {
        long groupId = groupMemberships.getGroupId();
        if (groupId <= 0)
        {
            throw new IllegalArgumentException("illegal owner id " + groupId);
        }
        return getTableSuffix(groupId);
    }
    
    private int getTableSuffix(long groupId)
    {
        int table = (int) (HashTool.apply(String.valueOf(groupId)) % TABLE_COUNT);
        return table;
    }
    
    private int getTableSuffixUser(GroupMemberships groupMemberships)
    {
        long groupId = groupMemberships.getUserId();
        if (groupId <= 0)
        {
            throw new IllegalArgumentException("illegal owner id " + groupId);
        }
        return getTableSuffix(groupId);
    }
    
}
