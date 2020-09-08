/**
 * 
 */
package com.huawei.sharedrive.isystem.test;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.huawei.sharedrive.thrift.app2isystem.UserThriftService;

/**
 * @author q90003805
 * 
 */
public class SSLThriftClientTest
{
    protected TTransport transport;
    
    private TProtocolDecorator protocol;
    
    private UserThriftService.Client client;
    
    public SSLThriftClientTest(String serviceName, String ip, int port) throws TTransportException
    {
        TSSLTransportParameters params = new TSSLTransportParameters();
        params.setTrustStore("d:/isystem_thrift_server.jks", "isystem@thrift.cse");
        params.setKeyStore("d:/isystem_thrift_server.jks", "isystem@thrift.cse");
        transport = TSSLTransportFactory.getClientSocket(ip, port, 0, params);
        protocol = new TMultiplexedProtocol(new TBinaryProtocol(transport), serviceName);
        client = new UserThriftService.Client(protocol);
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
    
    public long getUsedSpace(long userId) throws TException
    {
        return client.getUsedSpace(userId);
    }
    
    /**
     * @param args
     * @throws TException
     */
    public static void main(String[] args) throws TException
    {
        SSLThriftClientTest test = new SSLThriftClientTest("userManageService", "10.169.20.82", 13000);
        // test.open();
        for (int i = 0; i < 1000; i++)
        {
            try
            {
                System.out.println(test.getUsedSpace(2));
                try
                {
                    Thread.sleep(60000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(test.getUsedSpace(1));
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        test.close();
    }
    
}
