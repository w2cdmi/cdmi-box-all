package com.huawei.sharedrive.uam.authserver.manager;

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;

public interface AccountAuthserverNetworkManager
{
    
    void create(Long accountId, Long authServerId, String ipStart, String ipEnd);
    
    void update(Long id, String ipStart, String ipEnd);
    
    void deleteByIds(String ids);
    
    void deleteAll(Long authServerId, Long accountId);
    
    void checkNetworkIp(String ipStart, String ipEnd);
    
    Page<AccountAuthserverNetwork> listNetwork(Long authServerId, Long accountId, PageRequest pageRequest);
    
    AuthServer checkAndGetAuthServerId(String realIp, Long accountId);
    
}
