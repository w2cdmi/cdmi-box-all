package com.huawei.sharedrive.isystem.dns.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupDao;
import com.huawei.sharedrive.isystem.cluster.dao.ResourceGroupNodeDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RuntimeStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroupNode;
import com.huawei.sharedrive.isystem.dns.dao.DnsDomainDao;
import com.huawei.sharedrive.isystem.dns.dao.DnsServerDao;
import com.huawei.sharedrive.isystem.dns.dao.IntranetDao;
import com.huawei.sharedrive.isystem.dns.dao.UasNodeDao;
import com.huawei.sharedrive.isystem.dns.domain.DnsDomain;
import com.huawei.sharedrive.isystem.dns.domain.DnsServer;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;
import com.huawei.sharedrive.isystem.dns.domain.Intranet;
import com.huawei.sharedrive.isystem.dns.domain.UasNode;
import com.huawei.sharedrive.thrift.dns.Node;
import com.huawei.sharedrive.thrift.dns.Node_Manage_Status;
import com.huawei.sharedrive.thrift.dns.Subnet;

@Service("dnsThriftCommon")
public class DnsThriftCommon
{
    private static Logger logger = LoggerFactory.getLogger(DnsThriftCommon.class);
    
    public static final String HTTP = "http";
    
    public static final String HTTPS = "https";
    
    @Autowired
    private DNSThriftClientNoSSL dnsThriftClientNoSSL;
    
    @Autowired
    private DnsServerDao dnsServerDao;
    
    @Autowired
    private DnsDomainDao dnsDomainDao;
    
    @Autowired
    private ResourceGroupDao resourceGroupDao;
    
    @Autowired
    private UasNodeDao uasNodeDao;
    
    @Autowired
    private IntranetDao intranetDao;
    
    @Autowired
    private ResourceGroupNodeDao resourceGroupNodeDao;
    
    @Value("${dss.heartbeat.url.suffix}")
    private String dssURLSuffix;
    
    /**
     * setUASNodes
     * 
     * @param dnsServer
     * @throws TException
     */
    public void setUASNodes(DnsServer dnsServer) throws TException
    {
        if (null != dnsServer)
        {
            dnsServer = dnsServerDao.get(dnsServer.getId());
            dnsThriftClientNoSSL.setUASNodes(dnsServer.getManageIp(), dnsServer.getManagePort(), getUasNode());
        }
    }
    
    public void setIntranet() throws TException
    {
        List<DnsServer> servers = dnsServerDao.getAllDnsServer();
        List<Intranet> innets = intranetDao.getAllList();
        List<Subnet> subnets = new ArrayList<Subnet>(10);
        Subnet subnet = null;
        for (Intranet in : innets)
        {
            subnet = new Subnet(in.getNetAddress(), in.getNetMask());
            subnets.add(subnet);
            logger.info(" sub net IP :" + subnet.getNetAddress() + "---mask:" + subnet.getNetMask());
        }
        for (DnsServer dnsServer : servers)
        {
            sendInranet(dnsServer, subnets);
        }
    }
    
    public void sendInranet(DnsServer dnsServer, List<Subnet> subnets) throws TException
    {
        dnsThriftClientNoSSL.setIntranet(dnsServer.getManageIp(), dnsServer.getManagePort(), subnets);
    }
    
    public List<Node> getUasNode()
    {
        List<Node> nodes = new ArrayList<Node>(10);
        Node node = null;
        List<UasNode> uasNodes = uasNodeDao.getUasNodeList();
        for (UasNode uasNode : uasNodes)
        {
            node = new Node();
            if (null != uasNode.getServiceAddr())
            {
                node.setServiceAddress(uasNode.getServiceAddr());
            }
            if (null != uasNode.getNatAddr())
            {
                node.setNatAddress(uasNode.getNatAddr());
            }
            node.setHeartbeatURL("");
            node.setManageStatus(Node_Manage_Status.Enable);
            nodes.add(node);
        }
        return nodes;
    }
    
    /**
     * setNodesForDomain
     * 
     * @param dnsDomain
     * @throws TException
     */
    public void setNodesForDomain(DnsDomain dnsDomain) throws TException
    {
        if (null != dnsDomain)
        {
            dnsDomain = dnsDomainDao.get(dnsDomain.getDomainName());
            DnsServer dnsServer = dnsServerDao.get(dnsDomain.getDnsServerId());
            DssDomain dss = dnsDomainDao.getDssDomainbyID(dnsDomain.getDomainName());
            if (null != dss)
            {
                ResourceGroup resourceGroup = resourceGroupDao.get(dss.getDataCenter().getId());
                List<Node> nodes = setDomainUseResourceGroup(resourceGroup);
                dnsThriftClientNoSSL.setNodesForDomain(dnsServer.getManageIp(),
                    dnsServer.getManagePort(),
                    dnsDomain.getDomainName(),
                    nodes);
            }
        }
    }
    
    public void setResourceGroupNodes(ResourceGroupNode resourceGroupNode) throws TException
    {
        if (null != resourceGroupNode)
        {
            List<DssDomain> dssDomain = dnsDomainDao.getAllByDataCenterID(resourceGroupNode.getDcId());
            for (DssDomain domain : dssDomain)
            {
                setNodesForDomain(domain.getDnsDomain());
            }
        }
    }
    
    public List<Node> setDomainUseResourceGroup(ResourceGroup resourceGroup)
    {
        List<Node> nodes = new ArrayList<Node>(10);
        if (null != resourceGroup)
        {
            List<ResourceGroupNode> groupNodes = resourceGroupNodeDao.getResourceGroupNodeByDcID(resourceGroup.getDcId());
            
            Node node = null;
            for (ResourceGroupNode groupNode : groupNodes)
            {
                node = new Node();
                node.setHeartbeatURL(getHeartbeatURL(resourceGroup.getDcId(), groupNode.getServiceAddr()));
                node.setServiceAddress(groupNode.getServiceAddr());
                node.setManageStatus(Node_Manage_Status.Enable);
                node.setNatAddress(groupNode.getNatAddr());
                nodes.add(node);
            }
            
        }
        return nodes;
    }
    
    public String getHeartbeatURL(int dcID, String serviceaddress)
    {
        // String url = "http" + "://" + serviceaddress + ":" + "port" + "/aaaaaa";
        StringBuffer heartbeatURL = new StringBuffer();
        ResourceGroup resourceGroup = resourceGroupDao.get(dcID);
        heartbeatURL.append(resourceGroup.getProtocol()).append("://");
        heartbeatURL.append(serviceaddress);
        
        int port = 0;
        if (resourceGroup.getProtocol().equals(HTTP))
        {
            port = resourceGroup.getServiceHttpPort();
        }
        else
        {
            port = resourceGroup.getServiceHttpsPort();
        }
        if (!(port == 80) && !(port == 443))
        {
            heartbeatURL.append(':').append(port);
        }
        heartbeatURL.append(dssURLSuffix);
        return heartbeatURL.toString();
    }
    
    public List<Node> getNodesForDataCenter(DataCenter dataCenter) throws TException
    {
        List<DssDomain> dss = dnsDomainDao.getAllByDataCenterID(dataCenter.getId());
        DnsDomain dnsDomain = null;
        for (DssDomain domain : dss)
        {
            if (null != domain.getDnsDomain())
            {
                dnsDomain = domain.getDnsDomain();
            }
        }
        return getNodesForDomain(dnsDomain);
        
    }
    
    public RuntimeStatus getDataCenterStatus(DataCenter dataCenter)
    {
        try
        {
            List<Node> nodes = getNodesForDataCenter(dataCenter);
            boolean available = false;
            for (Node node : nodes)
            {
                available = (available || node.isAvailable());
            }
            logger.info(" dc RuntimeStatus" + available);
            if (available)
            {
                return RuntimeStatus.Normal;
            }
            return RuntimeStatus.Offline;
        }
        catch (TException e)
        {
            logger.info("getDataCenterStatus fail ", e);
            return RuntimeStatus.Abnormal;
        }
    }
    
    public List<Node> getNodesForDomain(DnsDomain dnsDomain) throws TException
    {
        List<Node> nodes = new ArrayList<Node>(0);
        if (null != dnsDomain)
        {
            dnsDomain = dnsDomainDao.get(dnsDomain.getDomainName());
            DnsServer dnsServer = dnsServerDao.get(dnsDomain.getDnsServerId());
            nodes = dnsThriftClientNoSSL.getNodesForDomain(dnsServer.getManageIp(),
                dnsServer.getManagePort(),
                dnsDomain.getDomainName());
        }
        return nodes;
    }
    
    public boolean validDomain(DnsDomain dnsDomain)
    {
        if (null != dnsDomain)
        {
            DnsServer dnsServer = dnsServerDao.get(dnsDomain.getDnsServerId());
            try
            {
                return dnsThriftClientNoSSL.validDomain(dnsServer.getManageIp(),
                    dnsServer.getManagePort(),
                    dnsDomain.getDomainName());
            }
            catch (Exception e)
            {
                logger.error(" validDomain  TException", e);
                return false;
            }
            
        }
        return false;
        
    }
    
    public boolean getDSSDomains(DnsServer dnsServer)
    {
        try
        {
            List<String> list = dnsThriftClientNoSSL.getDSSDomains(dnsServer.getManageIp(),
                dnsServer.getManagePort());
            if (null != list)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            logger.error("Server Status fail  getDSSDomains    ", e);
            
            return false;
        }
        return false;
    }
    
    public void deleteDSSDomains(String domainName) throws TException
    {
        DnsDomain dnsDomain = dnsDomainDao.get(domainName);
        DnsServer dnsServer = dnsServerDao.get(dnsDomain.getDnsServerId());
        dnsThriftClientNoSSL.deleteDSSDomain(dnsServer.getManageIp(),
            dnsServer.getManagePort(),
            dnsDomain.getDomainName());
    }
}
