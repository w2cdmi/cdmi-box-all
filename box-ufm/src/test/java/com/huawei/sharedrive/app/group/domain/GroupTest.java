package com.huawei.sharedrive.app.group.domain;

import java.util.Date;

import org.junit.Test;

public class GroupTest
{
    @Test
    public void groupTest()
    {
        Group group = new Group();
        group.equals(null);
    }
    
    @Test
    public void groupTest2()
    {
        Group group = new Group();
        group.getAccountId();
    }
    
    @Test
    public void groupTest3()
    {
        Group group = new Group();
        group.getAppId();
    }
    
    @Test
    public void groupTest4()
    {
        Group group = new Group();
        group.getClass();
    }
    
    @Test
    public void groupTest5()
    {
        Group group = new Group();
        Date createdAt = group.getCreatedAt();
        System.out.println(createdAt);
    }
    
    @Test
    public void groupTest6()
    {
        Group group = new Group();
        group.getDescription();
    }
    
    @Test
    public void groupTest7()
    {
        Group group = new Group();
        group.getId();
        group.getMaxMembers();
        group.getModifiedAt();
        group.getModifiedBy();
        group.getName();
        group.getType();
        group.getStatus();
        group.getParent();
        group.getOwnedBy();
    }
    
    @Test
    public void groupTest8()
    {
        Group group = new Group();
        group.setAccountId(1l);
        group.setAppId("1");
        group.setCreatedAt(new Date());
        group.setCreatedBy(1234556l);
        group.setDescription("test");
        group.setId(1l);
        group.setMaxMembers(0);
        group.setModifiedAt(new Date());
        group.setModifiedBy(0);
        group.setName("test");
        group.setParent(0);
        group.setType(null);
        group.setOwnedBy(1l);
    }
}
