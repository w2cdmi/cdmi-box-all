package com.huawei.sharedrive.isystem.util.test;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.RandomKeyGUID;

public class RandomKeyGUIDTest
{
    @Test
    public void getSecureRandomGUIDTest()
    {
        String string = RandomKeyGUID.getSecureRandomGUID();
        System.out.println(string);
    }
}
