package com.huawei.sharedrive.isystem.dns.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.dns.domain.DnsServer;

import pw.cdmi.box.dao.BaseDAO;

public interface DnsServerDao extends BaseDAO<DnsServer, Integer>
{
    List<DnsServer> getAllDnsServer();
    
    int getNextId();
    
    List<DnsServer> getDnsServerByIPandPort(DnsServer dnsServer);
}
