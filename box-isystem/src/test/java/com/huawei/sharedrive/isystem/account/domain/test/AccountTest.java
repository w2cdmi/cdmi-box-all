package com.huawei.sharedrive.isystem.account.domain.test;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.account.domain.Account;

public class AccountTest
{
    @Test
    public void accountTest()
    {
        Account a = new Account();
        a.setAppId("OneBox");
        a.setCreateAt(new Date());
        a.getCreateAt();
        a.setCreateAt(null);
        a.getCreateAt();
        a.setCurrentMember(10);
        a.setCurrentTeamspace(10);
        a.setEnterpriseId(12L);
        a.setFilePreviewable(true);
        a.setFileScanable(true);
        a.setId(123L);
        a.setMaxFiles(123L);
        a.setMaxMember(234);
        a.setMaxSpace(345L);
        a.setMaxTeamspaces(213);
        a.setModifiedAt(new Date());
        a.getModifiedAt();
        a.setModifiedAt(null);
        a.getModifiedAt();
        a.setName("test");
        a.setStatus((byte) 2);
        Assert.assertEquals("OneBox", a.getAppId());
        Assert.assertEquals(new Integer(10), a.getCurrentMember());
        Assert.assertEquals(new Integer(10), a.getCurrentTeamspace());
        Assert.assertEquals((Long) 12L, a.getEnterpriseId());
        Assert.assertEquals((Long) 123L, a.getId());
        Assert.assertEquals((Long) 123L, a.getMaxFiles());
        Assert.assertEquals((Long) 345L, a.getMaxSpace());
        Assert.assertEquals(new Integer(234), a.getMaxMember());
        Assert.assertEquals(new Integer(213), a.getMaxTeamspaces());
        Assert.assertEquals("test", a.getName());
        a.getStatus();
        a.getFilePreviewable();
        a.getFileScanable();
    }
}
