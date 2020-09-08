package com.huawei.sharedrive.isystem.dns.service.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.cluster.dao.DCDao;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.dns.common.DnsThriftCommon;
import com.huawei.sharedrive.isystem.dns.dao.DnsDomainDao;
import com.huawei.sharedrive.isystem.dns.dao.DnsServerDao;
import com.huawei.sharedrive.isystem.dns.domain.DnsServer;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;
import com.huawei.sharedrive.isystem.dns.service.DnsServerService;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.thrift.dns.Node;

import pw.cdmi.common.config.service.ConfigManager;

@Service("dnsServerService")
public class DnsServerServiceImpl implements DnsServerService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DnsServerServiceImpl.class);
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private DCDao dcDao;
    
    @Autowired
    private DnsDomainDao dnsDomainDao;
    
    @Autowired
    private DnsServerDao dnsServerDao;
    
    @Autowired
    private DnsThriftCommon dnsThriftCommon;
    
    public boolean addDnsSever(DnsServer dnsServer)
    {
        List<DnsServer> list = dnsServerDao.getDnsServerByIPandPort(dnsServer);
        if (null != list && !list.isEmpty())
        {
            return false;
        }
        dnsServer.setId(dnsServerDao.getNextId());
        dnsServerDao.create(dnsServer);
        return true;
    }
    
    @Override
    public boolean addDomain(DssDomain dssDomain) throws TException
    {
        if (dnsThriftCommon.validDomain(dssDomain.getDnsDomain()))
        {
            addDomainInNewTransaction(dssDomain);
            configManager.setConfig("dssDnsChanged", "changed");
            return true;
        }
        return false;
    }
    
    /**
     * @param dssDomain
     * @throws TException 
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addDomainInNewTransaction(DssDomain dssDomain) throws TException
    {
        dnsDomainDao.create(dssDomain.getDnsDomain());
        DataCenter dc = dcDao.get(dssDomain.getDataCenter().getId());
        if (null == dc)
        {
            throw new InvalidParameterException("dataCenter is null");
        }
        dnsDomainDao.createDssDomain(dssDomain);
        
        dnsThriftCommon.setNodesForDomain(dssDomain.getDnsDomain());
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DnsServer createView(int id)
    {
        
        DnsServer dnsServer = dnsServerDao.get(id);
        List<DataCenter> dcs = dcDao.getAll();
        List<DssDomain> dssdomains = new ArrayList<DssDomain>(10);
        DssDomain domain = null;
        for (DataCenter dc : dcs)
        {
            domain = new DssDomain();
            domain.setDataCenter(dc);
            dssdomains.add(domain);
        }
        dnsServer.setDssdomains(dssdomains);
        return dnsServer;
        
    }
    
    @Override
    public void deleteDnsDomainAndDssDomainByKey(String domainName) throws TException
    {
        delDnsDomainInNewTransaction(domainName);
        configManager.setConfig("dssDnsChanged", "changed");
    }
    
    /**
     * @param domainName
     * @throws TException 
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delDnsDomainInNewTransaction(String domainName) throws TException
    {
        dnsThriftCommon.deleteDSSDomains(domainName);
        dnsDomainDao.deleteDnsDomainAndDssDomainByKey(domainName);
    }
    
    public void deleteDnsServer(int id) throws TException
    {
        List<DssDomain> list = dnsDomainDao.getAllByDnsServerID(id);
        if (CollectionUtils.isNotEmpty(list))
        {
            for (DssDomain dssDomain : list)
            {
                if (null != dssDomain && null != dssDomain.getDnsDomain()
                    && null != dssDomain.getDnsDomain().getDomainName())
                {
                    deleteDnsDomainAndDssDomainByKey(dssDomain.getDnsDomain().getDomainName());
                }
            }
        }
        dnsServerDao.delete(id);
    }
    
    public List<DnsServer> getAllListDnsServer() throws TException
    {
        List<DnsServer> severs = dnsServerDao.getAllDnsServer();
        List<DssDomain> dssdomains = null;
        for (DnsServer dnsServer : severs)
        {
            dssdomains = dnsDomainDao.getAllByDnsServerID(dnsServer.getId());
            
            if (dnsThriftCommon.getDSSDomains(dnsServer))
            {
                dnsServer.setAvailAble(true);
            }
            if (null == dssdomains)
            {
                continue;
            }
            dnsServer.setDssdomains(dssdomains);
            if (!dssdomains.isEmpty() && null != dssdomains.get(0)
                && null != dssdomains.get(0).getDnsDomain())
            {
                setDnsAvailAbleValue(dssdomains, dnsServer);
            }
        }
        return severs;
    }
    
    private void setDnsAvailAbleValue(List<DssDomain> dssdomains, DnsServer dnsServer)
    {
        List<Node> nodes = null;
        try
        {
            nodes = dnsThriftCommon.getNodesForDomain(dssdomains.get(0).getDnsDomain());
            if (null != nodes)
            {
                dnsServer.setAvailAble(true);
            }
        }
        catch (RuntimeException e)
        {
            throw new BusinessException(e);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
    
    public DnsDomainDao getDnsDomainDao()
    {
        return dnsDomainDao;
    }
    
    public DnsServerDao getDnsServerDao()
    {
        return dnsServerDao;
    }
    
    public void setDnsDomainDao(DnsDomainDao dnsDomainDao)
    {
        this.dnsDomainDao = dnsDomainDao;
    }
    
    public void setDnsServerDao(DnsServerDao dnsServerDao)
    {
        this.dnsServerDao = dnsServerDao;
    }
    
}
