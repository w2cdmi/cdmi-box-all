package com.huawei.sharedrive.app.dataserver.domain;

import org.junit.Test;

public class NetSegmentTest
{
    @Test
    public void netSegmentTest()
    {
        NetSegment net = new NetSegment();
        net.equals(null);
        net.getClass();
        net.getEnd();
        net.getEndIp();
        net.getId();
        net.getRegionId();
        net.getStart();
        net.getStartIp();
    }
    
    @Test
    public void netSegmentTest2()
    {
        NetSegment net = new NetSegment();
        net.setEndIp("127.255.255.255");
        net.setId(1l);
        net.setRegionId(1);
        net.setStartIp("127.0.0.1");
    }
}
