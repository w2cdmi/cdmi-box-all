package com.huawei.sharedrive.app.user.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.group.dao.GroupMembershipsDAO;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.user.dao.GroupMemberDao;
import com.huawei.sharedrive.app.user.domain.GroupInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.core.utils.CacheParameterUtils;

@Service
public class GroupMemberDaoImpl extends CacheableSqlMapClientDAO implements GroupMemberDao
{
    @Autowired
    private GroupMembershipsDAO groupMembershipsDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public List<GroupInfo> getUserGroupList(long userId)
    {
        List<GroupInfo> groupCaches = null;
        if (isCacheSupported())
        {
            String key = GroupConstants.CACHE_MEMBER_GROUP + userId;
            groupCaches = (List<GroupInfo>) getCacheClient().getCache(key);
            if (groupCaches != null)
            {
                return groupCaches;
            }
            groupCaches = getDataBaseInfo(userId);
            if (!groupCaches.isEmpty())
            {
                getCacheClient().setCache(key,
                    groupCaches,
                    System.currentTimeMillis() + CacheParameterUtils.AUTHSERVER_CACHE_TIME_OUT);
            }
            return groupCaches;
        }
        
        groupCaches = getDataBaseInfo(userId);
        return groupCaches;
    }
    
    private List<GroupInfo> getDataBaseInfo(long userId)
    {
        List<GroupInfo> groupCaches = null;
        List<GroupMemberships> groupMemberships = groupMembershipsDao.getUserListByUserId(userId);
        groupCaches = new ArrayList<GroupInfo>(groupMemberships.size());
        GroupInfo gc = null;
        for (GroupMemberships gm : groupMemberships)
        {
            gc = new GroupInfo();
            gc.setId(gm.getGroupId());
            gc.setName(gm.getName());
            gc.setGroupRole(gm.getGroupRole());
            groupCaches.add(gc);
        }
        return groupCaches;
    }
    
}
