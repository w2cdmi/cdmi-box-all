package com.huawei.sharedrive.app.group.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.group.dao.GroupCachedDAO;
import com.huawei.sharedrive.app.group.dao.GroupDAO;
import com.huawei.sharedrive.app.group.dao.GroupMembershipsDAO;
import com.huawei.sharedrive.app.group.domain.Group;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.group.service.GroupCachedService;
import com.huawei.sharedrive.app.openapi.domain.group.MemcachedGroup;

@Component
public class GroupCachedServiceImpl implements GroupCachedService
{
    @Autowired
    private GroupMembershipsDAO groupMembershipsDao;
    
    @Autowired
    private GroupCachedDAO groupCachedDao;
    
    @Autowired
    private GroupDAO groupDao;
    
    @Override
    public List<MemcachedGroup> setAndGetUserListToCached(Long userId)
    {
        List<MemcachedGroup> memcachedGroups = groupCachedDao.getGroupMemcached(userId);
        if (CollectionUtils.isNotEmpty(memcachedGroups))
        {
            return memcachedGroups;
        }
        List<GroupMemberships> membershipses = groupMembershipsDao.getUserListByUserId(userId);
        memcachedGroups = new ArrayList<MemcachedGroup>(membershipses.size());
        MemcachedGroup memcachedGroup = new MemcachedGroup();
        
        Group group = null;
        for (GroupMemberships gm : membershipses)
        {
            group = groupDao.get(gm.getGroupId());
            if (group != null)
            {
                memcachedGroup.setId(group.getId());
                memcachedGroup.setName(group.getName());
                memcachedGroups.add(memcachedGroup);
            }
        }
        groupCachedDao.setGroupMemcached(userId, memcachedGroups);
        return memcachedGroups;
    }
    
    @Override
    public void deleteCached(long userId)
    {
        groupCachedDao.deleteCached(userId);
    }
}
