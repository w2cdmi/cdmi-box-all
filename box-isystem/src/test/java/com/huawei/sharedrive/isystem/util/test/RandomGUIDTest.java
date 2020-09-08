package com.huawei.sharedrive.isystem.util.test;

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
