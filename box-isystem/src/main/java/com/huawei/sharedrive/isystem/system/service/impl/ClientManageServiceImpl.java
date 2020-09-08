/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.system.dao.ClientManageDAO;
import com.huawei.sharedrive.isystem.system.service.ClientManageService;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.ClientManage;

/**
 * @author d00199602
 * 
 */
@Component
public class ClientManageServiceImpl implements ClientManageService
{
    @Autowired
    private ClientManageDAO clientManageDAO;
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private ConfigManager configManager;
    
    private ClientManageService proxySelf;
    
    @PostConstruct
    public void setSelf()
    {
        // 从上下文获取代理对象（如果通过proxtSelf=this是不对的，this是目标对象）
        proxySelf = context.getBean(ClientManageService.class);
    }
    
    @Override
    public List<ClientManage> getAll()
    {
        // TODO Auto-generated method stub
        return clientManageDAO.getAll();
    }
    
    @Override
    public void updateClient(ClientManage clientManage)
    {
        // TODO Auto-generated method stub
        if (clientManage == null)
        {
            return;
        }
        if (proxySelf != null)
        {
            proxySelf.doUpdate(clientManage);
            clientManage.setContent(null);
            configManager.setConfig(ClientManage.class.getSimpleName(), clientManage);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doUpdate(ClientManage clientManage)
    {
        clientManageDAO.delete(clientManage.getType());
        clientManageDAO.insert(clientManage);
    }
    
}
