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
package com.huawei.sharedrive.thrift.app2isystem;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;




/**
 * 
 * @author s90006125
 *
 */
public class NetConfigThriftServiceClientTest {
	private static final String serverName = "netConfigService";
	@Test
	public void addNetSegment()
	{
		TMultiplexedProtocol protocal = null;
		try {
			protocal = getProtocol(serverName);
			
//			NetConfigThriftService.Client client  = new NetConfigThriftService.Client(protocal);
//			
//			List<NetSegment> netSegments = new ArrayList<>();
//	        
//	        for(int i=0;i<3;i++)
//	        {
//	            NetSegment net = new NetSegment();
//	            net.setStartSegment("10.10.10." + i);
//	            net.setEndSegment("10.10.100." + i);
//	            net.setRegionID(0);
//	            netSegments.add(net);
//	        }
//	        
//	        client.addNetSegment(netSegments);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != protocal) {
				protocal.getTransport().close();
			}
		}
	}
	
	@Test
    public void changeNetSegment()
    {
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            NetConfigThriftService.Client client  = new NetConfigThriftService.Client(protocal);
//            
//            List<NetSegment> netSegments = new ArrayList<>();
//            
//            for(int i=0;i<1;i++)
//            {
//                NetSegment net = new NetSegment();
//                net.setId(i);
//                net.setStartSegment("10.10.10." + i);
//                net.setEndSegment("10.10.100." + i);
//                net.setRegionID(0);
//                netSegments.add(net);
//            }
//            
//            client.changeNetSegment(netSegments);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != protocal) {
                protocal.getTransport().close();
            }
        }
    }
	
	@Test
    public void deleteNetSegment()
    {
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            NetConfigThriftService.Client client  = new NetConfigThriftService.Client(protocal);
//            
//            List<NetSegment> netSegments = new ArrayList<>();
//            
//            for(int i=0;i<2;i++)
//            {
//                NetSegment net = new NetSegment();
//                net.setId(i);
//                netSegments.add(net);
//            }
//            
//            client.deleteNetSegment(netSegments);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != protocal) {
                protocal.getTransport().close();
            }
        }
    }
	
	@Test
    public void getAllNetSegment()
    {
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            NetConfigThriftService.Client client  = new NetConfigThriftService.Client(protocal);
//            
//            List<NetSegment> netSegments = client.getAllNetSegment();
//            for(NetSegment n : netSegments)
//            {
//                System.out.println("Start: " + n.getStartSegment() + "; End: " + n.getEndSegment());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != protocal) {
                protocal.getTransport().close();
            }
        }
    }
	
	@Test
    public void getAllNetSegmentByRegion()
    {
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            NetConfigThriftService.Client client  = new NetConfigThriftService.Client(protocal);
//            
//            List<NetSegment> netSegments = client.getAllNetSegmentByRegion(0);
//            for(NetSegment n : netSegments)
//            {
//                System.out.println("Start: " + n.getStartSegment() + "; End: " + n.getEndSegment());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != protocal) {
                protocal.getTransport().close();
            }
        }
    }
	
	private TMultiplexedProtocol getProtocol(String serverName) throws TTransportException
	{
		TSocket transport = new TSocket("127.0.0.1",15555);
        TFramedTransport transport2 = new TFramedTransport(transport);
        
        TBinaryProtocol protocol = new TBinaryProtocol(transport2);
        TMultiplexedProtocol mp1 = new TMultiplexedProtocol(protocol, serverName);
        transport2.open();
        
        return mp1;
	}
}
