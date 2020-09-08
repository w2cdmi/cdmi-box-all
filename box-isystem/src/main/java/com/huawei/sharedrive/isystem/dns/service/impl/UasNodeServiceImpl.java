package com.huawei.sharedrive.isystem.dns.service.impl;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.dns.common.DnsThriftCommon;
import com.huawei.sharedrive.isystem.dns.dao.UasNodeDao;
import com.huawei.sharedrive.isystem.dns.domain.DnsServer;
import com.huawei.sharedrive.isystem.dns.domain.UasNode;
import com.huawei.sharedrive.isystem.dns.service.UasNodeService;

@Service("uasNodeService")
public class UasNodeServiceImpl implements UasNodeService
{    
    @Autowired
    private UasNodeDao uasNodeDao;
    
    @Autowired
    private DnsThriftCommon dnsThriftCommon;
    
    public static final int DNSSERVERID = 1;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateUasNode(UasNode uasNode) throws TException
    {
        uasNodeDao.update(uasNode);
        
        DnsServer dnsServer = new DnsServer();
        dnsServer.setId(DNSSERVERID);
        dnsThriftCommon.setUASNodes(dnsServer);
    }
}
