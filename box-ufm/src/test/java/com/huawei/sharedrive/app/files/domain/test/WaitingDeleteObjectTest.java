package com.huawei.sharedrive.app.files.domain.test;

import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.WaitingDeleteObject;

public class WaitingDeleteObjectTest
{
    @Test
    public void getCreatedAtTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        Date createdAt = wait.getCreatedAt();
        System.out.println(createdAt);
    }
    
    @Test
    public void getObjectIdTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        String createdAt = wait.getObjectId();
        System.out.println(createdAt);
    }
    
    @Test
    public void getResourceGroupIdTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        int createdAt = wait.getResourceGroupId();
        System.out.println(createdAt);
    }
    
    @Test
    public void setCreatedAtTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        wait.setCreatedAt(null);
    }
    
    @Test
    public void setCreatedAtTest2() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        wait.setCreatedAt(new Date());
    }
    
    @Test
    public void setObjectIdTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        wait.setObjectId("avb");
    }
    
    @Test
    public void setResourceGroupIdTest() {
        WaitingDeleteObject wait = new WaitingDeleteObject();
        wait.setResourceGroupId(0);
    }
}
