package com.huawei.sharedrive.isystem.test;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;

import com.huawei.sharedrive.thrift.dns.DNSService;

public class DNSLocalThriftServiceClientTest extends AbstractSpringTest
{
    @Test
    public void getAllIntranet() throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket("10.169.52.72", 13020, 0));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            System.out.println(client.getAllIntranet().size());
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    @Test
    public void getDSSDomains() throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket("10.169.52.72", 13020, 0));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            System.out.println(client.getDSSDomains().size());
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
    }
    
    @Test
    public void getNodesForDomain() throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket("10.169.52.72", 13020, 0));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            System.out.println(client.getNodesForDomain("test").size());
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
    }
    
    @Test
    public void getUASNodes() throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket("10.169.52.72", 13020, 0));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            System.out.println(client.getUASNodes().size());
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
    }
    
}
