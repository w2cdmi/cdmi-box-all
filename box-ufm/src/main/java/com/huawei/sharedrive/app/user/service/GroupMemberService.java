package com.huawei.sharedrive.app.user.service;

import java.util.List;

import com.huawei.sharedrive.app.user.domain.GroupInfo;

public interface GroupMemberService
{
    List<GroupInfo>getUserGroupList(long userId);
}
