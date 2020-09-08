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
public class DCThriftServiceClientTest {
	private static final String serverName = "dcService";
	@Test
	public void addResourceGroup()
	{
		TMultiplexedProtocol protocal = null;
		try {
			protocal = getProtocol(serverName);
			
			DCThriftService.Client client  = new DCThriftService.Client(protocal);
			
			String name = "testDC_001";
			String managerip = "127.0.0.1";
			int managerport = 12346;
			int regionid = 1;
			String domainName = "127.0.0.1";
			//client.addResourceGroup("", name, managerip, managerport, regionid, domainName, "http", "http");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != protocal) {
				protocal.getTransport().close();
			}
		}
	}
	
	@Test
	public void deleteResourceGroup()
	{
		TMultiplexedProtocol protocal = null;
		try {
			protocal = getProtocol(serverName);
			
			DCThriftService.Client client  = new DCThriftService.Client(protocal);
			
			int dcid = 11;
			//client.deleteResourceGroup("", dcid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != protocal) {
				protocal.getTransport().close();
			}
		}
	}
	
	@Test
	public void updateRegion()
	{
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            DCThriftService.Client client  = new DCThriftService.Client(protocal);
//            
//            int dcid = 11;
//            int regionid = 1;
            //client.updateRegion("", dcid, regionid);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != protocal) {
                protocal.getTransport().close();
            }
        }

	}
	
	@Test
	public void activeResourceGroup()
	{
		TMultiplexedProtocol protocal = null;
		try {
			protocal = getProtocol(serverName);
			
			DCThriftService.Client client  = new DCThriftService.Client(protocal);
			
			int dcid = 13;
			//client.activeResourceGroup("", dcid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != protocal) {
				protocal.getTransport().close();
			}
		}
	}
	
	@Test
	public void list()
	{
        TMultiplexedProtocol protocal = null;
        try {
            protocal = getProtocol(serverName);
            
//            DCThriftService.Client client  = new DCThriftService.Client(protocal);
            
//          List<ResourceGroup> groups = client.listResourceGroup();
//          for(ResourceGroup group : groups) {
//              System.out.println(group.getRegion().getName());
//              System.out.println(group.getDomainName());
//              System.out.println(group.getName());
//          }
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
