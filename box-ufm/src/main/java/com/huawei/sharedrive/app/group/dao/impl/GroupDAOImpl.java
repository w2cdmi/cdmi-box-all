package com.huawei.sharedrive.app.group.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.group.dao.GroupDAO;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.SqlUtils;

@Service
public class GroupDAOImpl extends AbstractDAOImpl implements GroupDAO
{
    private static final long BASE_GROUP_ID = 0;
    
    @SuppressWarnings("deprecation")
    @Override
    public void create(Group group)
    {
        sqlMapClientTemplate.insert("Group.insert", group);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int delete(long id)
    {
        int result = sqlMapClientTemplate.delete("Group.delete", id);
        return result;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public Group get(long id)
    {
        return (Group) sqlMapClientTemplate.queryForObject("Group.get", id);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int getCount(Group groupFilter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        if (groupFilter != null && StringUtils.isNotBlank(groupFilter.getName()))
        {
            groupFilter.setName(SqlUtils.stringToSqlLikeFields(groupFilter.getName()));
        }
        map.put("filter", groupFilter);
        return (int) sqlMapClientTemplate.queryForObject("Group.getGroupsCount", map);
    }
    
    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public List<Group> getGroupsList(List<GroupOrder> orders, Limit limit, Group group)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("order", getOrderByStr(orders));
        map.put("limit", limit);
        map.put("filter", group);
        return sqlMapClientTemplate.queryForList("Group.getGroupsList", map);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public long getMaxGroupId()
    {
        Object groupId = sqlMapClientTemplate.queryForObject("Group.getMaxGroupId");
        if (groupId == null)
        {
            return BASE_GROUP_ID;
        }
        return (Long) groupId;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public int update(Group group)
    {
        return sqlMapClientTemplate.update("Group.update", group);
    }
    
    private String getOrderByStr(List<GroupOrder> orderList)
    {
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (GroupOrder order : orderList)
        {
            field = order.getField();
            
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            orderBy.append(field).append(' ').append(order.getDirection()).append(',');
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
	
	@Override
	public Group get(long id, long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("id", id);
        map.put("accountId", accountId);
		return (Group) sqlMapClientTemplate.queryForObject("Group.getGroupByIdAndAccountId", map);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Group getByName(String name,long accountId) {
		Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("name", name);
        map.put("accountId", accountId);
		return (Group) sqlMapClientTemplate.queryForObject("Group.getByName", map);
	}
    
}
