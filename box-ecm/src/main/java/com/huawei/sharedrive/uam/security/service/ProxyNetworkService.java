    package com.huawei.sharedrive.uam.security.service;

import java.util.List;

import com.huawei.sharedrive.uam.security.domain.ProxyNetwork;

public interface ProxyNetworkService
{
    /**
     * 
     * @param networkType
     * @return
     */
    List<ProxyNetwork> getTypeList(Integer networkType);
    
    /**
     * 
     * @param acessNetworkList
     */
    void save(List<ProxyNetwork> acessNetworkList);
    
    /**
     * @param acessNetworkList
     */
    void doSave(List<ProxyNetwork> acessNetworkList);
    
}
