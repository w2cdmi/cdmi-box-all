package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import pw.cdmi.core.utils.RandomGUID;


public class RandomGUIDTest
{
    @Test
    public void getRandomGUIDTest()
    {
        RandomGUID guid = new RandomGUID();
        String afterMD5 = guid.getValueAfterMD5();
        System.out.println(afterMD5);
        Assert.assertEquals(false, "0e04e81588c6dd72830173a2c4ad5dc".equals(afterMD5));
        RandomGUID guid1 = new RandomGUID(true);
        String afterMD51 = guid1.getValueAfterMD5();
        System.out.println(afterMD51);
        Assert.assertEquals(false, "0e04e81588c6dd72830173a2c4ad5dc".equals(afterMD51));
        String beforeMD5 = guid1.getValueBeforeMD5();
        System.out.println(beforeMD5);
        Assert.assertEquals(false, "0e04e81588c6dd72830173a2c4ad5dc".equals(beforeMD5));
    }
    
    @Test
    public void toStringTest()
    {
        RandomGUID guid = new RandomGUID();
        guid.setValueAfterMD5("0e04e81588c6dd72830173a2c4ad5dc");
        Assert.assertEquals("0e04e81588c6dd72830173a2c4ad5dc", guid.getValueAfterMD5());
        System.out.println(guid.toString());
        Assert.assertEquals("0E04E815-88C6-DD72-8301-73A2C4AD5DC", guid.toString());
    }
    
    @Test
    public void getGUID4udsTest()
    {
        RandomGUID guid = new RandomGUID();
        guid.setValueAfterMD5("0e04e81588c6dd72830173a2c4ad5dc");
        guid.setValueBeforeMD5("0e04e81588c6dd72830173a2c4ad5dc");
        System.out.println(guid.getValueBeforeMD5());
        String guid4uds = guid.getGUID4uds();
        System.out.println(guid4uds);
        Assert.assertEquals(false, "csecloudc345aada588c6dd72830173a".equals(guid4uds));
    }
    
    @Test
    public void getRandomGUIDTest1()
    {
        RandomGUID guid = new RandomGUID();
        String afterMD5 = guid.getValueAfterMD5();
        System.out.println(afterMD5);
        String beforeMD5 = guid.getValueBeforeMD5();
        System.out.println(beforeMD5);
        guid.setValueAfterMD5("valueAfterMD5");
        guid.setValueBeforeMD5("valueBeforeMD5");
        RandomGUID guid1 = new RandomGUID(true);
        String afterMD51 = guid1.getValueAfterMD5();
        System.out.println(afterMD51);
        String beforeMD51 = guid1.getValueBeforeMD5();
        System.out.println(beforeMD51);
        guid1.setValueAfterMD5("valueAfterMD5");
        guid1.setValueBeforeMD5("valueBeforeMD5");
    }
}
