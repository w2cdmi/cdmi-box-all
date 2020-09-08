package com.huawei.sharedrive.app.user.domain;

import java.util.Date;

import org.junit.Test;

public class UserTest
{
    @Test
    public void userTest()
    {
        User user = new User();
        user.equals(null);
        user.getAccountId();
        user.getAppId();
        user.getClass();
        user.getCreatedAt();
//        user.getDepartment();
        user.getDomain();
        user.getEmail();
        user.getFileCount();
        user.getId();
        user.getLabel();
        user.getLastLoginAt();
        user.getLastStatisticsTime();
        user.getLoginName();
        user.getMaxVersions();
        user.getModifiedAt();
        user.getType();
        user.getTableSuffix();
        user.getStatus();
        user.getSpaceUsed();
        user.getSpaceQuota();
        user.getSecurityId();
        user.getRegionId();
        user.getRecycleDays();
        user.getPassword();
        user.getObjectSid();
        user.getName();
    }
    
    @Test
    public void userTest2()
    {
        User user = new User();
        user.setAccountId(1l);
        user.setAppId("1");
        user.setCreatedAt(new Date());
//        user.setDepartment("t");
        user.setDomain("test");
        user.setEmail("a@huawei.com");
        user.setFileCount(0);
        user.setId(0);
        user.setLabel("lab");
        user.setLastLoginAt(new Date());
        user.setLastStatisticsTime(new Date());
        user.setLoginName("test");
        user.setMaxVersions(0);
        user.setModifiedAt(new Date());
        user.setName("a");
        user.setObjectSid("aaa");
    }
}
