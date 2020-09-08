package com.huawei.sharedrive.isystem.dns.service;

import java.util.List;

import org.apache.thrift.TException;

import com.huawei.sharedrive.isystem.dns.domain.DnsServer;
import com.huawei.sharedrive.isystem.dns.domain.DssDomain;

public interface DnsServerService
{
    List<DnsServer> getAllListDnsServer() throws TException;
    
    boolean addDnsSever(DnsServer dnsServer);
    
    void deleteDnsServer(int id) throws TException;
    
    void deleteDnsDomainAndDssDomainByKey(String domainName) throws TException;
    
    DnsServer createView(int id);
    
    boolean addDomain(DssDomain dssDomain) throws TException;
}
