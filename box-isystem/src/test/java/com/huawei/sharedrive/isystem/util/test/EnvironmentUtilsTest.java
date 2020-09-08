package com.huawei.sharedrive.isystem.util.test;

import org.junit.Test;

import com.huawei.sharedrive.isystem.exception.BusinessException;

import pw.cdmi.core.utils.EnvironmentUtils;

public class EnvironmentUtilsTest
{
    @Test
    public void getHostNameTest()
    {
        String hostName = EnvironmentUtils.getHostName();
        System.out.println(hostName);
    }
    
    @Test
    public void getDeviceUUIDTest()
    {
        try
        {
            String hostName = EnvironmentUtils.getDeviceUUID();
            System.out.println(hostName);
        }
        catch (BusinessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
