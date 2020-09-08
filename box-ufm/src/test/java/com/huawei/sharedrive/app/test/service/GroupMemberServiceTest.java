package com.huawei.sharedrive.app.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.test.other.AbstractSpringTest;
import com.huawei.sharedrive.app.user.service.GroupMemberService;

public class GroupMemberServiceTest extends AbstractSpringTest
{
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Test
    public void testCacheNormal()
    {
        //获取数据库中信息
        groupMemberService.getUserGroupList(2L);
        
        //获取cache中信息
        groupMemberService.getUserGroupList(2L);
    }
}
