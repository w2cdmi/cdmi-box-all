/**
 * 
 */
package com.huawei.sharedrive.isystem.test;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.thrift.app2isystem.TBusinessException;
import com.huawei.sharedrive.thrift.app2isystem.UserThriftService;
import com.huawei.sharedrive.thrift.app2isystem.UserThriftService.Iface;

/**
 * @author q90003805
 * 
 */
public class ThriftClientTest implements Iface
{
    protected TTransport transport;
    
    protected TProtocolDecorator protocol;
    
    private UserThriftService.Client client;
    
    public ThriftClientTest(String serviceName, String ip, int port)
    {
        this.transport = new TFramedTransport(new TSocket(ip, port));
        this.protocol = new TMultiplexedProtocol(new TBinaryProtocol(transport), serviceName);
        this.client = new UserThriftService.Client(this.protocol);
    }
    
    public void open() throws TTransportException
    {
        this.transport.open();
    }
    
    public void close()
    {
        this.transport.close();
    }
    
    public boolean validate()
    {
        return this.transport.isOpen();
    }
    
    @Override
    public long getUsedSpace(long userId) throws TBusinessException, TException
    {
        // TODO Auto-generated method stub
        return client.getUsedSpace(userId);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        ThriftClientTest test = new ThriftClientTest("userManageService", "10.169.20.82", 13000);
        test.open();
        System.out.println(test.getUsedSpace(3));
        test.close();
    }
    
}
