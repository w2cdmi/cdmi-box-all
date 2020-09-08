package com.huawei.sharedrive.app.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.user.dao.GroupMemberDao;
import com.huawei.sharedrive.app.user.domain.GroupInfo;
import com.huawei.sharedrive.app.user.service.GroupMemberService;

@Component
@Service("groupMemberService")
public class GroupMemberServiceImpl implements GroupMemberService
{
    
    @Autowired
    private GroupMemberDao groupMemberDao;
    
    @Override
    public List<GroupInfo> getUserGroupList(long userId)
    {
        return groupMemberDao.getUserGroupList(userId);
    }
    
}
