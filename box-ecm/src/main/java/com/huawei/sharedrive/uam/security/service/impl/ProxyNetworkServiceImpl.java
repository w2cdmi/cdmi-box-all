package com.huawei.sharedrive.uam.security.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.uam.security.dao.ProxyNetworkDAO;
import com.huawei.sharedrive.uam.security.domain.ProxyNetwork;
import com.huawei.sharedrive.uam.security.service.ProxyNetworkService;

import pw.cdmi.common.config.service.ConfigManager;

@Component
public class ProxyNetworkServiceImpl implements ProxyNetworkService
{
    @Autowired
    private ProxyNetworkDAO accessNetworkDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private ApplicationContext context;
    
    private ProxyNetworkService proxySelf;
    
    @PostConstruct
    public void setSelf()
    {
        proxySelf = context.getBean(ProxyNetworkService.class);
    }
    
    @Override
    public List<ProxyNetwork> getTypeList(Integer networkType)
    {
        return accessNetworkDAO.getTypeList(networkType);
    }
    
    @Override
    public void save(List<ProxyNetwork> acessNetworkList)
    {
        proxySelf.doSave(acessNetworkList);
        configManager.setConfig(ProxyNetworkServiceImpl.class.getSimpleName(), null);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doSave(List<ProxyNetwork> acessNetworkList)
    {
        if (acessNetworkList == null)
        {
            return;
        }
        int networkType = acessNetworkList.get(0).getNetworkType().getType();
        accessNetworkDAO.delete(networkType);
        for (ProxyNetwork acessNetwork : acessNetworkList)
        {
            accessNetworkDAO.create(acessNetwork);
        }
        configManager.setConfig(ProxyNetworkServiceImpl.class.getSimpleName(), null);
    }
}
