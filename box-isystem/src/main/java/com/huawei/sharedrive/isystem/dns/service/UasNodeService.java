package com.huawei.sharedrive.isystem.dns.service;

import org.apache.thrift.TException;

import com.huawei.sharedrive.isystem.dns.domain.UasNode;

public interface UasNodeService
{    
    void updateUasNode(UasNode uasNode) throws TException;
}
