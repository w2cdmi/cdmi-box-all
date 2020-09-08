/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.test.dataserver;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.dataserver.domain.NetSegment;
import com.huawei.sharedrive.app.dataserver.service.NetConfigService;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

/**
 * 
 * @author s90006125
 *
 */
public class NetConfigServiceTest extends AbstractSpringTest
{
    @Autowired
    private NetConfigService netConfigService;
    
    @Test
    public void addNetSegment()
    {
        List<NetSegment> netSegments = new ArrayList<>();
        
        for(int i=0;i<3;i++)
        {
            NetSegment net = new NetSegment();
            net.setStartIp("10.10.10." + i);
            net.setEndIp("10.10.100." + i);
            //net.setRegionID(0);
            netSegments.add(net);
        }
        
        netConfigService.addNetSegment(netSegments);
    }

    @Test
    public void changeNetSegment()
    {
        List<NetSegment> netSegments = new ArrayList<>();
        
        for(int i=0;i<3;i++)
        {
            NetSegment net = new NetSegment();
            net.setId(i);
            net.setStartIp("10.10.10." + i);
            net.setEndIp("10.10.100." + i);
            //net.setRegionID(0);
            netSegments.add(net);
        }
        
        netConfigService.changeNetSegment(netSegments);
    }

    @Test
    public void deleteNetSegment()
    {
        List<NetSegment> netSegments = new ArrayList<>();
        
        for(int i=0;i<2;i++)
        {
            NetSegment net = new NetSegment();
            net.setId(i);
            netSegments.add(net);
        }
        
        netConfigService.changeNetSegment(netSegments);
    }

    @Test
    public void getAllNetSegment()
    {
        List<NetSegment> netSegments = netConfigService.getAllNetSegment();
        for(NetSegment n : netSegments)
        {
            System.out.println("Start: " + n.getStartIp() + "; End: " + n.getEnd());
        }
    }

    @Test
    public void getAllNetSegmentByRegion()
    {
        List<NetSegment> netSegments = netConfigService.getAllNetSegmentByRegion(0);
        for(NetSegment n : netSegments)
        {
            System.out.println("Start: " + n.getStartIp() + "; End: " + n.getEnd());
        }
    }
}
