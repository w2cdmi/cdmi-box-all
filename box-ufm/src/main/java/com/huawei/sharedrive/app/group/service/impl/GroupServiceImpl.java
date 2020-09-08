package com.huawei.sharedrive.app.group.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.group.dao.GroupDAO;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.service.GroupService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.group.GroupOrder;

import pw.cdmi.box.domain.Limit;

@Component
public class GroupServiceImpl implements GroupService
{
    @Autowired
    private GroupDAO groupDao;
    
    @Override
    public Group createGroup(UserToken userToken, Group group)
    {
        groupDao.create(group);
        return group;
    }
    
    @Override
    public int delete(Long id)
    {
        return groupDao.delete(id);
    }
    
    @Override
    public Group get(Long id)
    {
        Group group = groupDao.get(id);
        return group;
    }
    
    @Override
    public Integer getCount(Group group)
    {
        return groupDao.getCount(group);
    }
    
    @Override
    public List<Group> getGroupsList(List<GroupOrder> orders, Limit limit, Group group)
    {
        return groupDao.getGroupsList(orders, limit, group);
    }
    
    @Override
    public void modifyGroup(UserToken userToken, Group group)
    {
        groupDao.update(group);
    }
	
	@Override
	public Group get(Long id, Long accountId) {
		return groupDao.get(id, accountId);
	}

	@Override
	public Group getByName(String name,long accountId) {
		return groupDao.getByName(name,accountId);
	}
}
