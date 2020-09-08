package com.huawei.sharedrive.isystem.account.domain.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;

import pw.cdmi.box.domain.PageRequest;

public class AccountPageConditionTest
{
    @Test
    public void startTimeTest()
    {
        AccountPageCondition condition = new AccountPageCondition();
        condition.setAppId("OneBox");
        condition.setEndTime(new Date());
        condition.getEndTime();
        condition.setEndTime(null);
        condition.setName("conditonname");
        condition.setStartTime(new Date());
        condition.getStartTime();
        condition.setStartTime(null);
        condition.setPageRequest(new PageRequest());
        Assert.assertEquals("OneBox", condition.getAppId());
        Assert.assertEquals("conditonname", condition.getName());
        condition.getEndTime();
        condition.getStartTime();
        condition.getPageRequest();
    }
}
