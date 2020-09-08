package com.huawei.sharedrive.app.user.dao;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.GroupInfo;


public interface GroupMemberDao
{
    List<GroupInfo> getUserGroupList(long userId);
}
