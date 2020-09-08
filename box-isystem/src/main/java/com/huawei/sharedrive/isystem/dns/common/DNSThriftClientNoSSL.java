package com.huawei.sharedrive.isystem.dns.common;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.thrift.dns.DNSService;
import com.huawei.sharedrive.thrift.dns.Node;
import com.huawei.sharedrive.thrift.dns.Subnet;

public class DNSThriftClientNoSSL
{
    public static final Logger LOGGER = LoggerFactory.getLogger(DNSThriftClientNoSSL.class);
    
    
    public void deleteDSSDomain(String serverIp, int serverPort, String domain) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            client.deleteDSSDomain(domain);
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public List<Subnet> getAllIntranet(String serverIp, int serverPort) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            return client.getAllIntranet();
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public List<String> getDSSDomains(String serverIp, int serverPort) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            return client.getDSSDomains();
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public List<Node> getNodesForDomain(String serverIp, int serverPort, String domain)
        throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            return client.getNodesForDomain(domain);
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
    }
    
    
    public List<Node> getUASNodes(String serverIp, int serverPort) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            return client.getUASNodes();
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
    }
    
    
    public void setIntranet(String serverIp, int serverPort, List<Subnet> subnets) throws TException
    {
        for(Subnet net:subnets)
        {
            LOGGER.info("netAddreess:["+net.getNetAddress()+"]mask["+net.getNetMask()+']');
            
            LOGGER.debug("netAddreess:["+net.getNetAddress()+"]mask["+net.getNetMask()+']');
        }
        
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            client.setIntranet(subnets);
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public void setNodesForDomain(String serverIp, int serverPort, String domain, List<Node> nodes)
        throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            client.setNodesForDomain(domain, nodes);
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public void setUASNodes(String serverIp, int serverPort, List<Node> nodes) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            client.setUASNodes(nodes);
        }
        finally
        {
            if (transport != null)
            {
                transport.close();
            }
        }
        
    }
    
    
    public boolean validDomain(String serverIp, int serverPort, String domain) throws TException
    {
        TTransport transport = null;
        try
        {
            transport = new TFramedTransport(new TSocket(serverIp, serverPort,20000));
            TProtocol protocol = new TBinaryProtocol(transport);
            DNSService.Client client = new DNSService.Client(protocol);
            transport.open();
            return client.validDomain(domain);
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
