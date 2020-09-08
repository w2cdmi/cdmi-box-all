/**
 * 
 */
package com.huawei.sharedrive.app.test.thrift;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.thrift.dc2app.DCThriftService;
import com.huawei.sharedrive.thrift.dc2app.ResourceGroupNode;
import com.huawei.sharedrive.thrift.dc2app.TBusinessException;
import com.huawei.sharedrive.thrift.echo.ChechService;

import pw.cdmi.common.thrift.client.pool.TTransportManager;

/**
 * @author q90003805
 *         
 */
public class TestThriftClientMain2
{
    private final static String SERVICE_NAME_1 = "ChechService";
    
    private final static String SERVICE_NAME_2 = "DCThriftService";
    
    // private TTransport transport;
    
    public TestThriftClientMain2(String serverIp, int serverPort, int timeout) throws TTransportException
    {
        // transport = new TFramedTransport(new TSocket(serverIp, serverPort, timeout));
        // transport.open();
        
    }
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
//        TTransportManager manager = new TTransportManager();
//        manager.setMaxActive(2);
//        manager.setMaxIdle(2);
//        manager.setMaxWait(20000);
//        manager.setMinEvictableIdleTimeMillis(15000);
//        manager.setMinIdle(2);
//        manager.setNumTestsPerEvictionRun(3);
//        manager.setServiceAddressManager(new TestServiceAddressManager());
//        manager.setTestOnBorrow(true);
//        manager.setTestOnReturn(false);
//        manager.setTimeBetweenEvictionRunsMillis(10000);
//        manager.setTransportTimout(60000);
//        manager.setUseSSL(false);
//        manager.setHeartBeatServiceName("ChechService");
//        manager.initPool();
//        
//        TestThriftClientMain2 m = new TestThriftClientMain2("10.169.101.164", 13012, 60000);
//        // System.out.println(m.echo(manager.getTransport()));
//        // Thread.sleep(10000);
//        
//        TTransport transport = manager.getTransport();
//        System.out.println(transport.isOpen());
//        System.out.println(m.getResourceGroupNodeList(transport).size());
//        Thread.sleep(10000);
//        System.out.println(m.echo(manager.getTransport()));
//        manager.close();
    }
    
    public int echo(TTransport transport) throws TException
    {
        TProtocolDecorator protocol = new TMultiplexedProtocol(new TBinaryProtocol(transport),
            SERVICE_NAME_1);
        ChechService.Client client1 = new ChechService.Client(protocol);
        int n = client1.echo();
        transport.close();
        return n;
    }
    
    public List<ResourceGroupNode> getResourceGroupNodeList(TTransport transport)
        throws TBusinessException, TException
    {
        TProtocolDecorator protocol = new TMultiplexedProtocol(new TBinaryProtocol(transport),
            SERVICE_NAME_2);
        DCThriftService.Client client2 = new DCThriftService.Client(protocol);
        List<ResourceGroupNode> list = client2.getResourceGroupNodeList();
        transport.close();
        return list;
    }
    
    // public void close()
    // {
    // transport.close();
    // }
    
}
